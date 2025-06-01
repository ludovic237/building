package com.example.backend.services

import com.example.backend.constants.PaymentTypeConstants
import com.example.backend.constants.StatusConstants
import com.example.backend.constants.StatusConstants.BILLING_CYCLE_STATUS_CANCELED
import com.example.backend.constants.StatusConstants.BILLING_CYCLE_STATUS_PARTIAL_PAID
import com.example.backend.constants.StatusConstants.BILLING_CYCLE_STATUS_PENDING
import com.example.backend.constants.StatusConstants.SERVICE_BILLING_MODEL_MONTHLY
import com.example.backend.constants.StatusConstants.SERVICE_BILLING_MODEL_YEARLY
import com.example.backend.constants.StatusConstants.SUBSCRIPTION_STATUS_ACTIVE
import com.example.backend.constants.StatusConstants.SUBSCRIPTION_STATUS_CANCELED
import com.example.backend.constants.StatusConstants.SUBSCRIPTION_STATUS_EXPIRED
import com.example.backend.dtos.SubscriptionDTO
import com.example.backend.dtos.SubscriptionDetailsDTO
import com.example.backend.models.*
import com.example.backend.models.SubscriptionServices
import com.example.backend.repositories.*
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter
import java.util.*

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.temporal.ChronoUnit
import java.util.logging.Logger
import kotlin.math.log

@Service
class SubscriptionService(
  private val paymentService: PaymentService,
  private val invoiceRepository: InvoiceRepository,
  private val subscriptionServiceRepository: SubscriptionServiceRepository,
  private val invoiceService: InvoiceService,
  private val serviceRepository: ServiceRepository,
  private val serviceOptionRepository: ServiceOptionRepository,
  private val subscriptionRepository: SubscriptionRepository,
  private val subscriptionOptionRepository: SubscriptionOptionRepository,
  private val serviceUsageRepository: ServiceUsageRepository,
  private val tenantRepository: TenantRepository,
  private val billingCycleRepository: BillingCycleRepository,
  private val paymentRepository: PaymentRepository,
  private val paymentLineRepository: PaymentLineRepository,
  private val userService: UserService
) {

  fun getAllSubscriptions(): List<Map<String, Any?>> {
    return subscriptionRepository.findAll().map { subscription ->
      val tenantName = tenantRepository.findById(subscription.tenant!!.id!!).get()
        .let { tenant -> "${tenant.user!!.firstName} ${tenant.user!!.lastName}" }

      val subscriptionServices =
        subscriptionServiceRepository.findBySubscription(subscription).map { subscriptionService ->
          mapOf(
            "serviceId" to subscriptionService.service?.id,
            "serviceName" to subscriptionService.service?.name,
            "serviceDescription" to subscriptionService.service?.description,
            "price" to subscriptionService.price,
            "quantity" to subscriptionService.quantity,
            "options" to subscriptionOptionRepository.findBySubscriptionService(subscriptionService)
              .map { subscriptionOption ->
                mapOf(
                  "subscriptionOptionId" to subscriptionOption.id,
                  "optionId" to subscriptionOption.option?.id,
                  "quantity" to subscriptionOption.quantity,
                  "price" to subscriptionOption.price
                )
              }
          )
        }

      mapOf(
        "subscriptionId" to subscription.id,
        "tenantName" to tenantName,
        "startDate" to subscription.startDate,
        "endDate" to subscription.endDate,
        "status" to subscription.status,
        "totalPrice" to subscription.totalPrice,
        "services" to subscriptionServices
      )
    }
  }

  fun getSubscriptionById(id: Long): Optional<Subscription> {
    return subscriptionRepository.findById(id)
  }

  fun createSubscription(subscription: Subscription): Subscription {
    var user = userService.getCurrentUser()
    val currentYear = Year.now().value
    val today = LocalDate.now()
    val currentMonth = String.format("%02d", today.monthValue)
    val currentDay = String.format("%02d", today.dayOfMonth)
    var invoice = Invoice().apply {
      this.user = subscription.tenant?.user
      type = "subscription"
      month = currentMonth.toInt()
      year = currentYear
      amount = subscription.totalPrice?.multiply(subscription.subscriptNumber?.toBigDecimal() ?: BigDecimal.ZERO)
      paymentDate = LocalDateTime.now()
      createdDate = LocalDateTime.now()
      status = "PENDING"
      number = invoiceService.generateInvoiceNumber()
    }
    invoiceRepository.save(invoice)
    subscription.createdDate = LocalDateTime.now()
    subscription.updatedDate = LocalDateTime.now()
    return subscriptionRepository.save(subscription)
  }

  fun updateSubscription(id: Long, updatedSubscription: Subscription): Subscription {
    val existingSubscription = subscriptionRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Subscription with ID $id not found") }

    existingSubscription.startDate = updatedSubscription.startDate
    existingSubscription.endDate = updatedSubscription.endDate
    existingSubscription.updatedDate = LocalDateTime.now()
    existingSubscription.status = updatedSubscription.status
    // Update other fields as necessary

    return subscriptionRepository.save(existingSubscription)
  }

  fun deleteSubscription(id: Long) {
    if (!subscriptionRepository.existsById(id)) {
      throw IllegalArgumentException("Subscription with ID $id not found")
    }
    subscriptionRepository.deleteById(id)
  }

  fun canceledSubscription(id: Long): Subscription {
    if (!subscriptionRepository.existsById(id)) {
      throw IllegalArgumentException("Subscription with ID $id not found")
    }
    val subscription = subscriptionRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Subscription with ID $id not found") }
    subscription.status = StatusConstants.SUBSCRIPTION_STATUS_CANCELED
    return subscriptionRepository.save(subscription)

  }

  fun getSubscriptionDetails(subscriptionId: Long): SubscriptionDetailsDTO {
    val subscription = subscriptionRepository.findById(subscriptionId)
      .orElseThrow { IllegalArgumentException("Subscription with ID $subscriptionId not found") }

    val tenant = tenantRepository.findById(subscription.tenant!!.id!!)
      .orElseThrow { IllegalArgumentException("Tenant not found") }

    val billingCycles = billingCycleRepository.findBySubscriptionId(subscriptionId)
    val payments = paymentRepository.findByTenant(tenant)
    val paymentLines = payments.map { payment ->
      paymentLineRepository.findByPaymentId(payment.id!!)
    }

    return SubscriptionDetailsDTO(
      tenant = tenant,
      billingCycles = billingCycles,
      payments = payments,
      paymentLines = paymentLines
    )
  }

  fun getSubscriptionsByTenantId(tenantId: Long): List<Map<String, Any?>> {
    // Retrieve the tenant or throw an exception if not found
    val tenant = tenantRepository.findById(tenantId)
      .orElseThrow { IllegalArgumentException("Tenant not found with ID $tenantId") }

    // Fetch all subscriptions for the given tenant
    val subscriptions = subscriptionRepository.findByTenant(tenant)
      ?: throw IllegalArgumentException("No subscriptions found for tenant with ID $tenantId")

    /*    // Map subscription services for all subscriptions
        return subscriptions.flatMap { subscription ->
          subscriptionServiceRepository.findBySubscription(subscription).map { subscriptionService ->
            mapOf(
              "subscriptionId" to subscription.id,
              "subscriptionServiceId" to subscriptionService.service?.id,
              "serviceName" to subscriptionService.service?.name,
              "serviceDescription" to subscriptionService.service?.description,
              "price" to subscriptionService.price,
              "quantity" to subscriptionService.quantity,
              "totalPrice" to subscriptionService.totalPrice,
              "options" to subscriptionOptionRepository.findBySubscriptionService(subscriptionService).map { subscriptionOption ->
                mapOf(
                  "subscriptionOptionId" to subscriptionOption.id,
                  "optionId" to subscriptionOption.option?.id,
                  "quantity" to subscriptionOption.quantity,
                  "price" to subscriptionOption.price
                )
              }
            )
          }
        }*/

    return subscriptions.mapNotNull { subscription ->
      val tenantName = tenantRepository.findById(subscription.tenant!!.id!!).get()
        .let { tenant -> "${tenant.user!!.firstName} ${tenant.user!!.lastName}" }

      if (subscription.invoice?.status != "PAID") {
        val subscriptionServices =
          subscriptionServiceRepository.findBySubscription(subscription).map { subscriptionService ->
            mapOf(
              "subscriptionId" to subscription.id,
              "subscriptionServiceId" to subscriptionService.id,
              "serviceId" to subscriptionService.service?.id,
              "serviceName" to subscriptionService.service?.name,
              "serviceDescription" to subscriptionService.service?.description,
              "price" to subscriptionService.price,
              "quantity" to subscriptionService.quantity,
              "options" to subscriptionOptionRepository.findBySubscriptionService(subscriptionService)
                .map { subscriptionOption ->
                  mapOf(
                    "subscriptionOptionId" to subscriptionOption.id,
                    "optionId" to subscriptionOption.option?.id,
                    "quantity" to subscriptionOption.quantity,
                    "price" to subscriptionOption.price
                  )
                }
            )
          }
        mapOf(
          "subscriptionId" to subscription.id,
          "remainingAmountToPay" to calculateTotalDue(subscription),
          "tenantName" to tenantName,
          "startDate" to subscription.startDate,
          "endDate" to subscription.endDate,
          "status" to subscription.status,
          "totalPrice" to subscription.totalPrice,
          "services" to subscriptionServices
        )
      } else {
        null
      }
    }
  }

  fun calculateTotalDue(subscription: Subscription): BigDecimal {
    val billingCycles = billingCycleRepository.findBySubscription(subscription)

    return billingCycles.sumOf { billingCycle ->
      val paymentLines = paymentLineRepository.findByBillingCycle(billingCycle)
      val totalAmountPaid = paymentLines.sumOf { it.amountPaid ?: BigDecimal.ZERO }
      (billingCycle.amountDue ?: BigDecimal.ZERO) - totalAmountPaid
    }
  }

  fun processPayment(
    tenantId: Long,
    paymentMode: String,
    subscriptionId: Long,
    paymentAmount: BigDecimal
  ): Map<String, Any?> {
    var remainingAmount = paymentAmount

    // Retrieve tenant and subscription
    val tenant = tenantRepository.findById(tenantId)
      .orElseThrow { IllegalArgumentException("Tenant not found with ID $tenantId") }

    val subscription = subscriptionRepository.findById(subscriptionId)
      .orElseThrow { IllegalArgumentException("Subscription not found with ID $subscriptionId") }

    val billingPrice = subscription.totalPrice ?: BigDecimal.ZERO

    // Create a new invoice
    val invoice = Invoice().apply {
      this.user = tenant.user
      this.type = "payment"
      this.month = LocalDate.now().monthValue
      this.year = LocalDate.now().year
      this.amount = paymentAmount
      this.paymentDate = LocalDateTime.now()
      this.createdDate = LocalDateTime.now()
      this.status = "PENDING"
      this.number = invoiceService.generateInvoiceNumber()
    }
    invoiceRepository.save(invoice)

    // Create a new payment record
    val payment = paymentRepository.save(
      Payment().apply {
        this.tenant = tenant
        this.totalAmount = paymentAmount
        this.paymentMethod = when (paymentMode.uppercase()) {
          PaymentTypeConstants.PAYMENT_METHOD_CASH -> PaymentTypeConstants.PAYMENT_METHOD_CASH
          PaymentTypeConstants.PAYMENT_METHOD_CREDIT_CARD -> PaymentTypeConstants.PAYMENT_METHOD_CREDIT_CARD
          PaymentTypeConstants.PAYMENT_METHOD_BANK_TRANSFER -> PaymentTypeConstants.PAYMENT_METHOD_BANK_TRANSFER
          PaymentTypeConstants.PAYMENT_METHOD_CHECK -> PaymentTypeConstants.PAYMENT_METHOD_CHECK
          PaymentTypeConstants.PAYMENT_METHOD_MOBILE_PAYMENT -> PaymentTypeConstants.PAYMENT_METHOD_MOBILE_PAYMENT
          else -> "OTHER"
        }
        this.paymentDate = LocalDateTime.now()
        this.createdDate = LocalDateTime.now()
        this.updatedDate = LocalDateTime.now()
      }
    )

    // Handle Partial Paid billing cycles
    val partialPaidCycles = billingCycleRepository.findBySubscription(subscription)
      .filter { it.status == "Partial Paid" }

    partialPaidCycles.forEach { billingCycle ->
      val remainingDue =
        (billingCycle.amountDue ?: BigDecimal.ZERO) - paymentLineRepository.findByBillingCycle(billingCycle)
          .sumOf { it.amountPaid ?: BigDecimal.ZERO }

      val amountToPay = remainingAmount.min(remainingDue)
      paymentLineRepository.save(
        PaymentLine().apply {
          this.payment = payment
          this.createdDate = LocalDateTime.now()
          this.updatedDate = LocalDateTime.now()
          this.billingCycle = billingCycle
          this.amountPaid = amountToPay
        }
      )

      if (amountToPay == remainingDue) {
        billingCycle.status = StatusConstants.BILLING_CYCLE_STATUS_PAID
        billingCycle.createdDate = LocalDateTime.now()
        billingCycle.updatedDate = LocalDateTime.now()
        billingCycleRepository.save(billingCycle)
      }

      remainingAmount -= amountToPay
      if (remainingAmount <= BigDecimal.ZERO) return@forEach
    }

    // Generate new billing cycles and payment lines
    val billingCycles = mutableListOf<BillingCycle>()
    val lastBillingCycle = billingCycleRepository.findBySubscription(subscription)
      .maxByOrNull { it.periodEnd!! }

    var currentStartDate = lastBillingCycle?.periodEnd?.plusDays(1) ?: subscription.startDate

    while (remainingAmount > BigDecimal.ZERO) {
      val subscriptionServices = subscriptionServiceRepository.findBySubscription(subscription)
      subscriptionServices.forEach { subscriptionService ->
        val currentEndDate = when (subscriptionService.service?.billingMode?.lowercase()) {
          SERVICE_BILLING_MODEL_MONTHLY -> currentStartDate?.plusMonths(1)
          SERVICE_BILLING_MODEL_YEARLY -> currentStartDate?.plusYears(1)
          else -> throw IllegalArgumentException("Unsupported billing mode")
        }

        val billingPrice = subscriptionService.price
        val amountToPay = remainingAmount.min(billingPrice)
        val billingCycle = BillingCycle().apply {
          this.periodStart = currentStartDate
          this.periodEnd = currentEndDate
          this.amountDue = billingPrice
          this.subscription = subscription
          this.createdDate = LocalDateTime.now()
          this.updatedDate = LocalDateTime.now()
          this.status =
            if (amountToPay == billingPrice) StatusConstants.BILLING_CYCLE_STATUS_PAID else StatusConstants.BILLING_CYCLE_STATUS_PARTIAL_PAID
        }
        billingCycleRepository.save(billingCycle)
        billingCycles.add(billingCycle)

        paymentLineRepository.save(
          PaymentLine().apply {
            this.payment = payment
            this.billingCycle = billingCycle
            this.amountPaid = amountToPay
            this.createdDate = LocalDateTime.now()
            this.updatedDate = LocalDateTime.now()
          }
        )

        remainingAmount -= amountToPay
        currentStartDate = currentEndDate
      }
    }

    // Call updateAmountsDue to update the amounts due for the subscription
//    paymentService.updateAmountsDue(subscription)

    return mapOf(
      "message" to "Payment processed successfully",
      "paymentId" to payment.id,
      "invoiceId" to invoice.id
    )
  }

  /**
   * Processes a payment for multiple subscriptions.
   *
   * @param tenantId The ID of the tenant making the payment.
   * @param paymentMode The mode of payment (e.g., cash, credit card).
   * @param selectedSubscriptions A list of selected subscriptions to process payment for.
   * @param amount The total amount to be paid.
   * @return A map containing the result of the payment processing.
   */
  fun processMultipleSubscriptionsPayment(
    tenantId: Long,
    paymentMode: String,
    selectedSubscriptions: List<Map<String, Any?>>,
    amount: BigDecimal
  ): Map<String, Any?> {
    var remainingAmount = amount

    // Retrieve tenant
    val tenant = tenantRepository.findById(tenantId)
      .orElseThrow { IllegalArgumentException("Tenant not found with ID $tenantId") }

    // Create a new payment record
    val payment = paymentRepository.save(
      Payment().apply {
        this.tenant = tenant
        this.totalAmount = amount
        this.paymentMethod = when (paymentMode.uppercase()) {
          PaymentTypeConstants.PAYMENT_METHOD_CASH -> PaymentTypeConstants.PAYMENT_METHOD_CASH
          PaymentTypeConstants.PAYMENT_METHOD_CREDIT_CARD -> PaymentTypeConstants.PAYMENT_METHOD_CREDIT_CARD
          PaymentTypeConstants.PAYMENT_METHOD_BANK_TRANSFER -> PaymentTypeConstants.PAYMENT_METHOD_BANK_TRANSFER
          PaymentTypeConstants.PAYMENT_METHOD_CHECK -> PaymentTypeConstants.PAYMENT_METHOD_CHECK
          PaymentTypeConstants.PAYMENT_METHOD_MOBILE_PAYMENT -> PaymentTypeConstants.PAYMENT_METHOD_MOBILE_PAYMENT
          else -> "OTHER"
        }
        this.paymentDate = LocalDateTime.now()
        this.createdDate = LocalDateTime.now()
        this.updatedDate = LocalDateTime.now()
      }
    )

    // Process each subscription
    selectedSubscriptions.forEach { subscriptionData ->
      val subscriptionId = (subscriptionData["subscriptionId"] as Number).toLong()
      val subscription = subscriptionRepository.findById(subscriptionId)
        .orElseThrow { IllegalArgumentException("Subscription not found with ID $subscriptionId") }

      val subscriptionServices = subscriptionServiceRepository.findBySubscription(subscription)

      subscriptionServices.forEach { subscriptionService ->
        println("Processing subscription service billing id : ${subscriptionService.billingCycle?.id}")
        val billingCycles = billingCycleRepository.findBySubscriptionServices(subscriptionService)

        billingCycles.forEach { billingCycle ->
          // Skip processing if the billing cycle is already paid
          if (billingCycle.status == StatusConstants.BILLING_CYCLE_STATUS_PAID) {
            return@forEach
          }

          val paymentLine = paymentLineRepository.findByBillingCycleId(billingCycle.id ?: 0L)

          val options = subscriptionOptionRepository.findBySubscriptionService(subscriptionService) ?: emptyList()
          val totalOptionsPrice = if (options.isEmpty())
            BigDecimal.ZERO
          else options.sumOf { it.amountDue ?: BigDecimal.ZERO }

          val servicePrice = (subscriptionService.totalPrice ?: BigDecimal.ZERO) + totalOptionsPrice
          var amountToPay = remainingAmount.min(billingCycle.amountDue)

          if (billingCycle.status == BILLING_CYCLE_STATUS_PARTIAL_PAID) {
            var remainingToPay = billingCycle.amountDue!! - paymentLine[0].amountPaid!!
            if (remainingAmount >= remainingToPay) {
              amountToPay = remainingToPay
              val paymentLineNew = PaymentLine()
              paymentLineNew.amountPaid = amountToPay
              paymentLineNew.payment = payment
              paymentLineNew.createdDate = LocalDateTime.now()
              paymentLineNew.updatedDate = LocalDateTime.now()
              paymentLineNew.billingCycle = paymentLine[0].billingCycle
              paymentLineNew.updatedDate = LocalDateTime.now()
              paymentLineRepository.save(paymentLineNew)

              billingCycle.status = StatusConstants.BILLING_CYCLE_STATUS_PAID
              billingCycle.updatedDate = LocalDateTime.now()
              billingCycleRepository.save(billingCycle)
            }
          } else if (billingCycle.status == BILLING_CYCLE_STATUS_PENDING) {
            if (remainingAmount > BigDecimal.ZERO && remainingAmount < billingCycle.amountDue) {
              billingCycle.status = StatusConstants.BILLING_CYCLE_STATUS_PARTIAL_PAID
              amountToPay = remainingAmount
              val newPaymentLine = PaymentLine().apply {
                this.payment = payment
                this.billingCycle = billingCycle
                this.amountPaid = amountToPay
                this.createdDate = LocalDateTime.now()
                this.updatedDate = LocalDateTime.now()
              }
              paymentLineRepository.save(newPaymentLine)
              billingCycle.updatedDate = LocalDateTime.now()
              billingCycleRepository.save(billingCycle)
            } else if (remainingAmount > billingCycle.amountDue) {
              billingCycle.status = StatusConstants.BILLING_CYCLE_STATUS_PAID
              amountToPay = remainingAmount.min(billingCycle.amountDue)
              val newPaymentLine = PaymentLine().apply {
                this.payment = payment
                this.billingCycle = billingCycle
                this.amountPaid = amountToPay
                this.createdDate = LocalDateTime.now()
                this.updatedDate = LocalDateTime.now()
              }
              paymentLineRepository.save(newPaymentLine)
              billingCycle.updatedDate = LocalDateTime.now()
              billingCycleRepository.save(billingCycle)
            }

          }

          remainingAmount -= amountToPay
          if (remainingAmount <= BigDecimal.ZERO) return@forEach
        }
      }

      // Update subscription status if fully paid
      if (remainingAmount <= BigDecimal.ZERO) {
        subscription.updatedDate = LocalDateTime.now()
        subscriptionRepository.save(subscription)
      }

      val billingCycles = billingCycleRepository.findBySubscription(subscription)
      val allBillingCyclesPaid = billingCycles.all { it.status == StatusConstants.BILLING_CYCLE_STATUS_PAID }

      val invoice = invoiceRepository.findById(subscription.invoice?.id ?: 0L)
        .orElseThrow { IllegalArgumentException("Invoice not found for subscription") }

      if (allBillingCyclesPaid) {
        invoice.status = "PAID"
      } else {
        invoice.status = "PARTIAL PAID"
      }

      invoice.paymentDate = LocalDateTime.now()
      invoice.updatedDate = LocalDateTime.now()
      invoiceRepository.save(invoice)
    }

    return mapOf(
      "message" to "Payment processed successfully",
      "paymentId" to payment.id
    )
  }

  fun isServiceActive(subscriptionId: Long): Boolean {
    val subscription = subscriptionRepository.findById(subscriptionId)
      .orElseThrow { IllegalArgumentException("Subscription not found") }
    return subscription.status == SUBSCRIPTION_STATUS_ACTIVE &&
      subscription.startDate!! <= LocalDateTime.now() &&
      (subscription.endDate == null || subscription.endDate!! >= LocalDateTime.now())
  }

  fun recordServiceUsage(subscriptionId: Long, optionId: Long, quantityUsed: Int): ServiceUsage {
    if (!isServiceActive(subscriptionId)) {
      throw IllegalStateException("Service is not active")
    }

    val subscription = subscriptionRepository.findById(subscriptionId)
      .orElseThrow { IllegalArgumentException("Subscription not found") }
    val option = subscriptionOptionRepository.findById(optionId)
      .orElseThrow { IllegalArgumentException("Subscription option not found") }

    val serviceUsage = ServiceUsage().apply {
      this.subscription = subscription
      this.option = option
      this.quantityUsed = quantityUsed
    }
    return serviceUsageRepository.save(serviceUsage)
  }

  fun checkSubscriptionStatus(subscriptionId: Long): String {
    val subscription = subscriptionRepository.findById(subscriptionId)
      .orElseThrow { IllegalArgumentException("Subscription not found") }

    val subscriptionServices = subscriptionServiceRepository.findBySubscription(subscription)

    val options = subscriptionServices.flatMap { subscriptionService ->
      subscriptionOptionRepository.findBySubscriptionService(subscriptionService)
    }
    val usage = serviceUsageRepository.findBySubscription(subscription)

    val allOptionsUsed = options.all { option ->
      val usedQuantity = usage.filter { usageItem -> usageItem.option?.id == option.id }.sumOf { it.quantityUsed ?: 0 }
      usedQuantity >= (option.quantity ?: 0)
    }

    val endDate = subscription.endDate
    return when {
      endDate != null && endDate.isBefore(LocalDateTime.now()) -> "Expired"
      allOptionsUsed -> "Completed"
      else -> "Active"
    }
  }

  fun getSubscriptionWithDetails(
    subscriptionId: Long,
  ): Map<String, Any?> {
    val subscription = subscriptionRepository.getById(subscriptionId)
    var remainingAmountToPay = BigDecimal.ZERO
    var amountPay = BigDecimal.ZERO
    val subscriptionServices = subscriptionServiceRepository.findBySubscription(subscription)
    val billingCycles = billingCycleRepository.findBySubscription(subscription)
    billingCycles.forEach { billingCycle ->
      val paymentLines = paymentLineRepository.findByBillingCycle(billingCycle)
      paymentLines.forEach { paymentLine ->
        amountPay += paymentLine.amountPaid!!
      }
    }
    val subscriptionOptions = subscriptionServices.flatMap { subscriptionService ->
      subscriptionOptionRepository.findBySubscriptionService(subscriptionService)
    }

    remainingAmountToPay = subscription.totalPrice!! - amountPay

    val formattedData = mapOf(
      "tenantId" to (subscription.tenant?.id ?: throw IllegalArgumentException("Tenant is null")),
      "totalPrice" to (subscription.totalPrice),
      "subscriptionId" to (subscription.id),
      "tenantName" to ("${{ subscription.tenant!!.user?.lastName }} ${{ subscription.tenant!!.user?.lastName }}"
        ?: throw IllegalArgumentException("Tenant is null")),
      "subscriptionServices" to subscriptionServices.map { subscriptionService ->
        mapOf(
          "subscriptionServiceId" to subscriptionService.id,
          "subscriptionId" to subscription.id,
          "serviceId" to subscriptionService.service?.id,
          "serviceName" to subscriptionService.service?.name,
          "serviceDescription" to subscriptionService.service?.description,
          "price" to subscriptionService.price,
          "quantity" to subscriptionService.quantity,
          "startDate" to subscriptionService.startDate,
          "endDate" to subscriptionService.endDate,
          "totalPrice" to subscriptionService.totalPrice,
          "subscriptNumber" to subscriptionService.subscriptNumber,
          "options" to subscriptionOptionRepository.findBySubscriptionService(subscriptionService)
            .map { subscriptionOption ->
              mapOf(
                "subscriptionOptionId" to subscriptionOption.id,
                "optionId" to subscriptionOption.option?.id,
                "name" to subscriptionOption.option?.name,
                "price" to subscriptionOption.price,
                "quantity" to subscriptionOption.quantity
              )
            }
        )
      },
      "dateDebut" to subscription.startDate.toString(),
      "dateFin" to subscription.endDate.toString(),
      "startDate" to subscription.startDate.toString(),
      "endDate" to subscription.endDate.toString(),
      "remainingAmountToPay" to remainingAmountToPay,
      "status" to subscription.status
    )
    return formattedData
  }

  fun updateSubscriptionStatus(subscriptionId: Long, newStatus: String): Map<String, Any?> {
    var subscription = subscriptionRepository.findById(subscriptionId)
      .orElseThrow { IllegalArgumentException("Subscription not found with ID $subscriptionId") }

    when {
      newStatus.contains("cancel") -> {
        subscription.status = SUBSCRIPTION_STATUS_CANCELED
        Logger.getLogger("SubscriptionUpdate").info("Subscription $subscriptionId has been canceled")
      }

      newStatus.contains("expire") -> {
        subscription.status = SUBSCRIPTION_STATUS_EXPIRED
        Logger.getLogger("SubscriptionUpdate").info("Subscription $subscriptionId has expired")
      }

      newStatus.contains("active") -> {
        subscription.status = SUBSCRIPTION_STATUS_ACTIVE
        Logger.getLogger("SubscriptionUpdate").info("Subscription $subscriptionId is now active")
      }

      else -> {
        println("No match found")
      }
    }
    subscription.updatedDate = LocalDateTime.now()
    return validateAndSaveSubscription(subscription)
  }

  fun validateAndSaveSubscription(subscription: Subscription): Map<String, Any?> {
    val validStatuses = listOf(
      SUBSCRIPTION_STATUS_ACTIVE,
      SUBSCRIPTION_STATUS_EXPIRED,
      SUBSCRIPTION_STATUS_CANCELED
    )

    if (subscription.status !in validStatuses) {
      throw IllegalArgumentException("Invalid subscription status: ${subscription.status}")
    }

    val subscriptionData = subscriptionRepository.save(subscription)
    return mapOf(
      "message" to "Payment processed successfully",
      "subscriptionId" to subscriptionData.id
    )
  }

  @Scheduled(fixedRate = 3600000) // Exécute toutes les heures (en millisecondes)
  @Transactional
  fun checkForExpiredSubscriptions() {
    val now = LocalDateTime.now()

    // Récupouscriptions dont la date de fin est passée et qui ne sont pas déjà EXPIRED ou CANCELED
    val subscriptionsToExpire = subscriptionRepository.findByEndDateBeforeAndStatusNotIn(
      now,
      listOf(SUBSCRIPTION_STATUS_EXPIRED, SUBSCRIPTION_STATUS_CANCELED)
    )

    if (subscriptionsToExpire.isNotEmpty()) {
      Logger.getLogger(this.javaClass.name)
        .info("Scheduler: Found ${subscriptionsToExpire.size} subscriptions to expire.")
      subscriptionsToExpire.forEach { subscription ->
        try {
          Logger.getLogger(this.javaClass.name).info("Scheduler: Expiring subscription ${subscription.id}")
          // La date d'effet est la date de fin de la souscription
          processSubscriptionStatusChange(
            subscriptionId = subscription.id!!,
            newStatus = SUBSCRIPTION_STATUS_EXPIRED,
            effectiveDateTime = subscription.endDate
              ?: now // Utilise endDate, ou now si endDate est null (ne devrait pas arriver pour expiration)
          )
        } catch (e: Exception) {
          Logger.getLogger(this.javaClass.name)
            .severe("Scheduler: Error expiring subscription ${subscription.id}: ${e.message}")
        }
      }
      Logger.getLogger(this.javaClass.name).info("Scheduler: Finished processing expired subscriptions.")
    } else {
      Logger.getLogger(this.javaClass.name).info("Scheduler: No subscriptions to expire at this time.")
    }

  }

  @Transactional
  fun saveSubscriptionsTenantWithInvoice(data: Map<String, Any?>): Map<String, Any?> {
    val userConnect = userService.getCurrentUser()
    val tenantId = (data["tenantId"] as? Number)?.toLong()
      ?: throw IllegalArgumentException("Tenant ID is missing or invalid in the request.")
    val requestedServicesData = data["selectedServices"] as? List<Map<String, Any?>>
      ?: throw IllegalArgumentException("Selected services are missing in the request.")

    val tenant =
      tenantRepository.findByIdWithUser(tenantId) // Assurez-vous que findByIdWithUser charge bien tenant.user
        .orElseThrow { IllegalArgumentException("Tenant not found with ID $tenantId") }

    // 1. Récupérer les IDs des services déjà activement souscrits par le locataire
    val existingActiveServiceIds = subscriptionRepository.findByTenantAndStatus(tenant, SUBSCRIPTION_STATUS_ACTIVE)
      .flatMap { sub -> subscriptionServiceRepository.findBySubscription(sub) }
      .mapNotNull { subService -> subService.service?.id }
      .toSet()

    // 2. Filtrer les services demandés
    val servicesToCreateData = mutableListOf<Map<String, Any?>>()
    val skippedServicesInfo = mutableListOf<String>()

    requestedServicesData.forEach { serviceData ->
      val serviceId = (serviceData["id"] as? Number)?.toLong()
        ?: throw IllegalArgumentException("Service ID is missing for one of the selected services.")

      if (existingActiveServiceIds.contains(serviceId)) {
        val serviceName = serviceRepository.findById(serviceId).map { it.name }.orElse("Unknown Service")
        val infoMsg =
          "Service '$serviceName' (ID: $serviceId) is already actively subscribed by this tenant and will be skipped."
        skippedServicesInfo.add(infoMsg)
        Logger.getLogger(this.javaClass.name).info(infoMsg)
      } else {
        servicesToCreateData.add(serviceData)
      }
    }

    // 3. Si aucun nouveau service n'est réer
    if (servicesToCreateData.isEmpty()) {
      val message = if (requestedServicesData.isNotEmpty() && skippedServicesInfo.isNotEmpty()) {
        "No new subscriptions created. All requested services are already active for this tenant. Details: ${skippedServicesInfo.joinToString()}"
      } else if (requestedServicesData.isEmpty()) {
        "No services provided in the request."
      } else { // requestedServicesData non vide, mais tous ont été skipped
        "No new subscriptions created. All requested services are already active for this tenant."
      }
      Logger.getLogger(this.javaClass.name).info(message)
      return mapOf(
        "message" to message,
        "invoiceId" to null,
        "subscriptionId" to null,
        "createdServicesCount" to 0,
        "skippedServicesInfo" to skippedServicesInfo
      )
    }

    // 4. Recalculer le finalTotal basé sur les services qui VONT être créés
    val actualFinalTotal = servicesToCreateData.sumOf { serviceData ->
      // Assurez-vous que "totalPrice" dans serviceData est un Number (Int, Double, Long)
      (serviceData["totalPrice"] as? Number)?.let { BigDecimal(it.toString()) } ?: BigDecimal.ZERO
    }

    // 5. Créer la facture avec le montant recalculé
    val savedInvoice = invoiceRepository.save(Invoice().apply {
      this.user = tenant.user // Le propriétaire de la facture (souvent l'utilisateur du locataire)
//      this.tenant = tenant // Lier la facture au locataire directement si votre modèle le permet et que c'est pertinent
      this.modify = userConnect // L'utilisateur qui a effectué l'action
      this.type = "subscription"
      this.amount = actualFinalTotal
      this.status = StatusConstants.BILLING_CYCLE_STATUS_PENDING // Ou un statut de facture approprié
      this.createdDate = LocalDateTime.now()
      this.updatedDate = LocalDateTime.now()
      this.number = invoiceService.generateInvoiceNumber()
    })
    Logger.getLogger(this.javaClass.name)
      .info("Invoice ${savedInvoice.id} created with amount $actualFinalTotal for tenant $tenantId.")

    // Déterminer les dates de début et de fin globales pour la souscription principale
    var globalSubscriptionStartDate: LocalDateTime? = null
    var globalSubscriptionEndDate: LocalDateTime? = null

    servicesToCreateData.forEach { serviceData ->
      val serviceStartDate = (serviceData["startDate"] as? String)?.let {
        LocalDateTime.parse(it.removeSuffix("Z")) // Attention au format de date du frontend
      } ?: LocalDateTime.now()

      val service = serviceRepository.findById((serviceData["id"] as Number).toLong()).orElse(null)
      val numberOfSubscriptions = serviceData["numberOfSubscriptions"] as? Int ?: 1

      val serviceEndDate = (serviceData["endDate"] as? String)?.let {
        LocalDate.parse(it).atStartOfDay() // Assurez-vous que c'est un LocalDateTime
      } ?: when (service?.billingMode?.lowercase()) {
        SERVICE_BILLING_MODEL_MONTHLY -> serviceStartDate.plusMonths(numberOfSubscriptions.toLong())
        SERVICE_BILLING_MODEL_YEARLY -> serviceStartDate.plusYears(numberOfSubscriptions.toLong())
        else -> serviceStartDate.plusMonths(numberOfSubscriptions.toLong())
      }

      if (globalSubscriptionStartDate == null || serviceStartDate.isBefore(globalSubscriptionStartDate)) {
        globalSubscriptionStartDate = serviceStartDate
      }
      if (globalSubscriptionEndDate == null || serviceEndDate.isAfter(globalSubscriptionEndDate)) {
        globalSubscriptionEndDate = serviceEndDate
      }
    }


    // 6. Créer la souscription principale
    val subscription = Subscription().apply {
      this.invoice = savedInvoice
      this.modifyBy = userConnect
      this.tenant = tenant
      this.startDate = globalSubscriptionStartDate ?: LocalDateTime.now()
      this.endDate = globalSubscriptionEndDate ?: LocalDateTime.now().plusMonths(1) // Fallback
      this.status = SUBSCRIPTION_STATUS_ACTIVE // Ou PENDING_PAYMENT si un paiement est requis avant activation
      this.totalPrice = actualFinalTotal
      this.createdDate = LocalDateTime.now()
      this.updatedDate = LocalDateTime.now()
    }

    val savedSubscription = createSubscriptionSimple(subscription)
    Logger.getLogger(this.javaClass.name)
      .info("Main subscription ${savedSubscription.id} created for invoice ${savedInvoice.id}.")

    // 7. Boucler sur `servicesToCreateData` pour créer les SubscriptionServices, Options et BillingCycles
    servicesToCreateData.forEach { serviceData ->
      val serviceId = (serviceData["id"] as Number).toLong()
      val serviceEntity = serviceRepository.findById(serviceId)
        .orElseThrow { IllegalArgumentException("Service not found with ID $serviceId") }

      val serviceStartDate = (serviceData["startDate"] as? String)?.let {
        LocalDateTime.parse(it.removeSuffix("Z"))
      } ?: LocalDateTime.now()
      val numberOfCycles =
        serviceData["numberOfSubscriptions"] as? Int ?: 1 // Renommer pour clarté, c'est le nombre de cycles

      val serviceEndDate = (serviceData["endDate"] as? String)?.let {
        LocalDate.parse(it).atTime(serviceStartDate.toLocalTime())
      } ?: when (serviceEntity.billingMode?.lowercase()) {
        SERVICE_BILLING_MODEL_MONTHLY -> serviceStartDate.plusMonths(numberOfCycles.toLong())
        SERVICE_BILLING_MODEL_YEARLY -> serviceStartDate.plusYears(numberOfCycles.toLong())
        else -> serviceStartDate.plusMonths(numberOfCycles.toLong())
      }

      // Ce `totalPriceForThisService` vient du front et représente le cot total pour ce service et ses options sur `numberOfCycles` périodes.
      val totalPriceForThisService =
        (serviceData["totalPrice"] as? Number)?.let { BigDecimal(it.toString()) } ?: BigDecimal.ZERO

      val subscriptionService = SubscriptionServices().apply {
        this.subscription = savedSubscription
        this.service = serviceEntity
        this.quantity =
          1 // Généralement 1, sauf si le service lui-même peut être pris en plusieurs unités indépendantes
        this.subscriptNumber = numberOfCycles // Nombre de périodes/cycles pour ce service
        this.price = serviceEntity.price ?: BigDecimal.ZERO // Prix de base du service (par cycle ou total ?)
        // this.totalPrice doit être le prix total du service de base pour tous les cycles (serviceEntity.price * numberOfCycles)
        this.totalPrice = (serviceEntity.price ?: BigDecimal.ZERO).multiply(numberOfCycles.toBigDecimal())
        // this.amountDue est le montant total pour ce service incluant ses options pour tous les cycles.
        // Il doit correspondre à totalPriceForThisService.
        this.amountDue = totalPriceForThisService
        this.startDate = serviceStartDate
        this.endDate = serviceEndDate
        this.createdDate = LocalDateTime.now()
        this.updatedDate = LocalDateTime.now()
      }
      val savedSubscriptionService = subscriptionServiceRepository.save(subscriptionService)
      Logger.getLogger(this.javaClass.name)
        .info("SubscriptionService ${savedSubscriptionService.id} (Service: ${serviceEntity.name}) created for subscription ${savedSubscription.id}.")

      val optionsData = serviceData["options"] as? List<Map<String, Any?>> ?: emptyList()
      optionsData.forEach { optionData ->
        val optionId = (optionData["id"] as Number).toLong()
        val quantity = (optionData["quantity"] as? Number)?.toInt() ?: 1
        val serviceOptionEntity = serviceOptionRepository.findById(optionId)
          .orElseThrow { IllegalArgumentException("Option not found with ID $optionId") }

        // Montant dû pour cette option pour TOUS les cycles du service parent
        val optionAmountDueTotal = (serviceOptionEntity.price ?: BigDecimal.ZERO)
          .multiply(quantity.toBigDecimal())
          .multiply(numberOfCycles.toBigDecimal())

        SubscriptionOptions().apply {
          this.subscriptionService = savedSubscriptionService
          this.subscription = savedSubscription // Peut être redondant
          this.option = serviceOptionEntity
          this.quantity = quantity // Quantité de cette option
          this.price = serviceOptionEntity.price ?: BigDecimal.ZERO // Prix unitaire de l'option (par cycle ?)
          this.amountDue = optionAmountDueTotal
          this.createdDate = LocalDateTime.now()
          this.updatedDate = LocalDateTime.now()
        }.also { subscriptionOptionRepository.save(it) }
      }

      // Génération des cycles de facturation
      var currentBillingStartDate = serviceStartDate
      val amountDuePerCycle = if (numberOfCycles > 0) {
        totalPriceForThisService.divide(numberOfCycles.toBigDecimal(), 2, RoundingMode.HALF_UP)
      } else {
        BigDecimal.ZERO
      }

      for (i in 1..numberOfCycles) {
        val currentBillingEndDate = when (serviceEntity.billingMode?.lowercase()) {
          SERVICE_BILLING_MODEL_MONTHLY -> currentBillingStartDate.plusMonths(1)
          SERVICE_BILLING_MODEL_YEARLY -> currentBillingStartDate.plusYears(1)
          else -> currentBillingStartDate.plusMonths(1) // Fallback
        } ?: throw IllegalStateException("Could not determine billing end date.")


        BillingCycle().apply {
          this.subscription = savedSubscription
          this.subscriptionServices = savedSubscriptionService
          this.periodStart = currentBillingStartDate
          this.periodEnd = currentBillingEndDate
          this.amountDue = amountDuePerCycle
          this.status = BILLING_CYCLE_STATUS_PENDING // Statut initial
          this.createdDate = LocalDateTime.now()
          this.updatedDate = LocalDateTime.now()
        }.also { billingCycleRepository.save(it) }

        currentBillingStartDate = currentBillingEndDate
      }
    }

    val successMessage = "Subscription processed successfully." +
      if (skippedServicesInfo.isNotEmpty()) " Some services were skipped: ${skippedServicesInfo.joinToString()}" else ""

    return mapOf(
      "message" to successMessage,
      "invoiceId" to savedInvoice.id,
      "subscriptionId" to savedSubscription.id,
      "createdServicesCount" to servicesToCreateData.size,
      "skippedServicesInfo" to skippedServicesInfo
    )
  }

  /* fun getUserSubscriptionsGroupedByInvoice(): List<Map<String, Any?>> {

       // Fetch all subscriptions for the user's tenant(s)
       val subscriptions = subscriptionRepository.findAll()

       // Group subscriptions by invoice_id
       val groupedSubscriptions = subscriptions.groupBy { it.invoice?.id }

       // Format the result
       return groupedSubscriptions.map { (invoiceId, subscriptions) ->
           mapOf(
               "invoiceId" to invoiceId?.toString(),
               "subscriptions" to subscriptions.map { subscription ->
                   mapOf(
                       "id" to subscription.id ?: 0L,
                       "serviceName" to subscription.service?.name ?: "Unknown",
                       "startDate" to subscription.startDate?.toString(),
                       "endDate" to subscription.endDate?.toString(),
                       "status" to subscription.status ?: "Unknown",
                       "totalPrice" to subscription.totalPrice ?: BigDecimal.ZERO
                   )
               }
           )
       }
   }
 */

  @Transactional
  fun createSubscriptionSimple(subscription: Subscription): Subscription {
    return try {
      subscriptionRepository.save(subscription)
    } catch (e: Exception) {
      throw RuntimeException("Erreur lors de la sauvegarde de la souscription : ${e.message}", e)
    }
  }

  // Fonction principale pour gérer le changement de statut et ses impacts
  @Transactional
  protected fun processSubscriptionStatusChange(
    subscriptionId: Long,
    newStatus: String,
    effectiveDateTime: LocalDateTime // La date à laquelle le changement prend effet
  ): Subscription {
    val subscription = subscriptionRepository.findById(subscriptionId)
      .orElseThrow { IllegalArgumentException("Subscription not found with ID $subscriptionId") }

    // 1. Mettre à jour la souscription principale
    subscription.status = newStatus
    subscription.updatedDate = LocalDateTime.now()

    // Si c'est une annulation ou une expiration, la date de fin doit être mise à jour.
    if (newStatus == SUBSCRIPTION_STATUS_CANCELED || newStatus == SUBSCRIPTION_STATUS_EXPIRED) {
      subscription.endDate = effectiveDateTime
    }
    val savedSubscription = subscriptionRepository.save(subscription)
    Logger.getLogger("SubscriptionUpdate")
      .info("Subscription $subscriptionId status changed to $newStatus, effective $effectiveDateTime")

    // 2. Mettre à jour les SubscriptionServices associés
    val subscriptionServices = subscriptionServiceRepository.findBySubscription(savedSubscription)
    subscriptionServices.forEach { subService ->

      if (newStatus == SUBSCRIPTION_STATUS_CANCELED || newStatus == SUBSCRIPTION_STATUS_EXPIRED) {
        // Mettre à jour la date de fin du service si elle est postérieure à la date d'effet
        if (subService.endDate == null || subService.endDate!!.isAfter(effectiveDateTime)) {
          subService.endDate = effectiveDateTime
        }
      }
      // Si vous aviez un champ "status" sur SubscriptionServices, vous le mettriez à jour ici.
      // Actuellement, le statut de la souscription parente dicte l'accès.
      subService.updatedDate = LocalDateTime.now()
      subscriptionServiceRepository.save(subService)
    }

    // 3. Mettre à jour les BillingCycles futurs ou affectés
    // On cible les cycles de facturation liés aux services de cette souscription
    val billingCyclesToUpdate = billingCycleRepository.findBySubscriptionServicesIn(subscriptionServices)

    billingCyclesToUpdate.forEach { bc ->
      if (newStatus == SUBSCRIPTION_STATUS_CANCELED || newStatus == SUBSCRIPTION_STATUS_EXPIRED) {
        // Annuler les cycles PENDING dont la période de début est après ou égale à la date d'effet
        if (bc.status == BILLING_CYCLE_STATUS_PENDING && !bc.periodStart!!.isBefore(
            effectiveDateTime.truncatedTo(
              ChronoUnit.DAYS
            )
          )
        ) {
          bc.status = BILLING_CYCLE_STATUS_CANCELED
          // Optionnel: Mettre amountDue à 0 pour les cycles annulés avant paiement
          // bc.amountDue = BigDecimal.ZERO
          bc.updatedDate = LocalDateTime.now()
          billingCycleRepository.save(bc)
          Logger.getLogger("SubscriptionUpdate")
            .info("BillingCycle ${bc.id} for Subscription $subscriptionId CANCELED.")
        }
        // Gérer les cycles PARTIAL_PAID ou PENDING qui chevauchent la date d'effet (plus complexe, nécessite une politique métier)
        // Par exemple, si un cycle est PENDING et que effectiveDateTime tombe au milieu :
        // - Vous pourriez le laisser tel quel et s'attendre à ce qu'il ne soit pas payé.
        // - Vous pourriez le recalculer au prorata (si la politique le permet).
        // - Vous pourriez l'annuler.
        // Pour l'instant, on se concentre sur l'annulation des cycles futurs non payés.
      }
    }
    // Gérer les factures (Invoices) associées si nécessaire (ex: annuler une facture PENDING si tous ses cycles sont annulés)

    return savedSubscription
  }

  /**
   * Expire une souscription.
   * Généralement appelé par une tâche planifiée ou si la date de fin est atteinte.
   */
  @Transactional
  fun expireSubscription(subscriptionId: Long): Map<String, Any?> {
    val subscription = subscriptionRepository.findById(subscriptionId)
      .orElseThrow { IllegalArgumentException("Subscription not found with ID $subscriptionId for expiration") }

    // La date d'effet est la date de fin de la souscription, ou maintenant si la date de fin est passée.
    val effectiveDate = subscription.endDate?.takeIf { it.isBefore(LocalDateTime.now()) } ?: LocalDateTime.now()

    val updatedSubscription = processSubscriptionStatusChange(
      subscriptionId = subscriptionId,
      newStatus = SUBSCRIPTION_STATUS_EXPIRED,
      effectiveDateTime = effectiveDate
    )
    return mapOf(
      "message" to "Subscription $subscriptionId has been expired.",
      "subscriptionId" to updatedSubscription.id,
      "newStatus" to updatedSubscription.status
    )
  }

  /**
   * Annule une souscription.
   * Peut être immédiat ou à la fin du cycle de facturation en cours.
   * Pour cet exemple, nous faisons une annulation immédiate.
   */
  @Transactional
  fun cancelSubscription(subscriptionId: Long /*, cancelEffectiveDate: LocalDateTime? = null */): Map<String, Any?> {
    // Par défaut, l'annulation est immédiate.
    // Une logique plus avancée pourrait permettre de choisir une date d'effet
    // (ex: fin du cycle payé actuel).
    val effectiveDate = LocalDateTime.now()

    val updatedSubscription = processSubscriptionStatusChange(
      subscriptionId = subscriptionId,
      newStatus = SUBSCRIPTION_STATUS_CANCELED,
      effectiveDateTime = effectiveDate
    )
    return mapOf(
      "message" to "Subscription $subscriptionId has been canceled.",
      "subscriptionId" to updatedSubscription.id,
      "newStatus" to updatedSubscription.status
    )
  }

}

