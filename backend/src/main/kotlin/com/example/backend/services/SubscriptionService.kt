package com.example.backend.services

import com.example.backend.constants.PaymentTypeConstants
import com.example.backend.constants.StatusConstants
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
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
          "monthly" -> currentStartDate?.plusMonths(1)
          "yearly" -> currentStartDate?.plusYears(1)
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
    paymentService.updateAmountsDue(subscription)

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
        val billingCycles = billingCycleRepository.findById(subscriptionService.billingCycle?.id ?: 0L)
          .orElseThrow { IllegalArgumentException("Billing cycle not found for subscription service") }
        val paymentLines = paymentLineRepository.findByBillingCycleId(subscriptionService.billingCycle?.id ?: 0L)

       val options = subscriptionOptionRepository.findBySubscriptionService(subscriptionService) ?: emptyList()
       val totalOptionsPrice = if (options.isEmpty())
         BigDecimal.ZERO
       else options.sumOf { it.amountDue ?: BigDecimal.ZERO }

        val servicePrice = (subscriptionService.totalPrice ?: BigDecimal.ZERO) + totalOptionsPrice
        val amountToPay = remainingAmount.min(servicePrice)

        paymentLines.payment = payment
        paymentLines.amountPaid = amountToPay
        paymentLines.updatedDate = LocalDateTime.now()

        // Update a payment line for the service
        val savePaymentLine = paymentLineRepository.save(paymentLines)


        billingCycles.status = StatusConstants.BILLING_CYCLE_STATUS_PAID
        billingCycles.updatedDate = LocalDateTime.now()
        billingCycleRepository.save(billingCycles)

        remainingAmount -= amountToPay
        if (remainingAmount <= BigDecimal.ZERO) return@forEach
      }

      // Update subscription status if fully paid
      if (remainingAmount <= BigDecimal.ZERO) {
        subscription.updatedDate = LocalDateTime.now()
        subscriptionRepository.save(subscription)
      }

      var invoice = invoiceRepository.findById(subscription.invoice?.id ?: 0L)
        .orElseThrow { IllegalArgumentException("Invoice not found for subscription") }
      if (invoice.amount == subscription.totalPrice) {
        invoice.status = "PAID"
        invoice.paymentDate = LocalDateTime.now()
        invoice.updatedDate = LocalDateTime.now()
        invoiceRepository.save(invoice)
      } else {
        invoice.status = "PARTIAL PAID"
        invoice.paymentDate = LocalDateTime.now()
        invoice.updatedDate = LocalDateTime.now()
        invoiceRepository.save(invoice)
      }
    }

    return mapOf(
      "message" to "Payment processed successfully",
      "paymentId" to payment.id
    )
  }

  fun isServiceActive(subscriptionId: Long): Boolean {
    val subscription = subscriptionRepository.findById(subscriptionId)
      .orElseThrow { IllegalArgumentException("Subscription not found") }
    return subscription.status == "active" &&
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
    val subscriptionServices = subscriptionServiceRepository.findBySubscription(subscription)

    val subscriptionOptions = subscriptionServices.flatMap { subscriptionService ->
      subscriptionOptionRepository.findBySubscriptionService(subscriptionService)
    }

    val formattedData = mapOf(
      "tenantId" to (subscription.tenant?.id ?: throw IllegalArgumentException("Tenant is null")),
      "subscriptionServices" to subscriptionServices.map { subscriptionService ->
        mapOf(
          "serviceId" to subscriptionService.service?.id,
          "serviceName" to subscriptionService.service?.name,
          "serviceDescription" to subscriptionService.service?.description,
          "options" to subscriptionOptionRepository.findBySubscriptionService(subscriptionService)
            .map { subscriptionOption ->
              mapOf(
                "subscriptionOptionId" to subscriptionOption.id,
                "optionId" to subscriptionOption.option?.id,
                "quantity" to subscriptionOption.quantity
              )
            }
        )
      },
      "dateDebut" to subscription.startDate.toString(),
      "dateFin" to subscription.endDate.toString(),
      "status" to subscription.status
    )
    return formattedData
  }

  fun updateSubscriptionStatus(subscriptionId: Long, newStatus: String): Map<String, Any?> {
    var subscription = subscriptionRepository.findById(subscriptionId)
      .orElseThrow { IllegalArgumentException("Subscription not found with ID $subscriptionId") }

    subscription.status = newStatus
    subscription.updatedDate = LocalDateTime.now()
    return validateAndSaveSubscription(subscription)
  }

  fun validateAndSaveSubscription(subscription: Subscription): Map<String, Any?> {
    val validStatuses = listOf(
      StatusConstants.SUBSCRIPTION_STATUS_ACTIVE,
      StatusConstants.SUBSCRIPTION_STATUS_EXPIRED,
      StatusConstants.SUBSCRIPTION_STATUS_CANCELED
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

    // Récupérer les souscriptions expirées
    val expiredSubscriptions = subscriptionRepository.findByEndDateBeforeAndStatusNot(now, "EXPIRED")

    // Mettre à jour leur statut
    expiredSubscriptions.forEach { subscription ->
      subscription.status = "EXPIRED"
      subscriptionRepository.save(subscription)
    }

    println("Checked and updated expired subscriptions: ${expiredSubscriptions.size}")
  }

  @Transactional
  fun saveSubscriptionsTenantWithInvoice(data: Map<String, Any?>): Map<String, Any?> {
    val userConnect = userService.getCurrentUser()
    val finalTotal = (data["finalTotal"] as Int).toBigDecimal()
    val tenantId = (data["tenantId"] as Int).toLong()
    val selectedServices = data["selectedServices"] as List<Map<String, Any?>>

    val tenant = tenantRepository.findByIdWithUser(tenantId)
      .orElseThrow { IllegalArgumentException("Tenant not found with ID $tenantId") }

    // Create invoice

    val saveInvoice = invoiceRepository.save(Invoice().apply {
      user = tenant.user
      modify = userConnect
      type = "subscription"
      amount = finalTotal
      status = "PENDING"
      createdDate = LocalDateTime.now()
      updatedDate = LocalDateTime.now()
      number = invoiceService.generateInvoiceNumber()
    })
    println("Invoice created with ID: ${saveInvoice.id}")
    val status = "ACTIVE"
    val subscription = Subscription().apply {
      this.invoice = saveInvoice
      this.modifyBy = userConnect
      this.tenant = tenant
      startDate = LocalDateTime.now()
      endDate = LocalDateTime.now().plusMonths(1)
      this.status = status
      totalPrice = finalTotal
      this.createdDate = LocalDateTime.now()
      this.updatedDate = LocalDateTime.now()
    }

    if (subscription.tenant == null || subscription.startDate == null || subscription.endDate == null) {
      throw IllegalArgumentException("Subscription data is incomplete.")
    }

    val savedSubscription = createSubscriptionSimple(subscription)

    selectedServices.forEach { serviceData ->
      val serviceId = (serviceData["id"] as Number).toLong()
      val service = serviceRepository.findById(serviceId)
        .orElseThrow { IllegalArgumentException("Service not found with ID $serviceId") }

      val startDate = (serviceData["startDate"] as? String)?.let {
        LocalDateTime.parse(it.removeSuffix("Z"))
      } ?: LocalDateTime.now()
      val numberOfSubscriptions = serviceData["numberOfSubscriptions"] as? Int ?: 1
      val endDate = (serviceData["endDate"] as? String)?.let {
        LocalDate.parse(it).atTime(startDate.toLocalTime())
      } ?: when (service.billingMode?.lowercase()) {
        "monthly" -> startDate.plusMonths(1L * numberOfSubscriptions)
        "yearly" -> startDate.plusYears(1L * numberOfSubscriptions)
        else -> startDate.plusMonths(1L * numberOfSubscriptions)
      }

      val totalPrice = (serviceData["totalPrice"] as? Int ?: 0).toBigDecimal()

      // Create billing cycle
      val billingCycle = BillingCycle().apply {
        this.subscription = savedSubscription
        this.periodStart = startDate
        this.periodEnd = endDate
        this.amountDue = totalPrice
        this.status = "PENDING"
        this.createdDate = LocalDateTime.now()
        this.updatedDate = LocalDateTime.now()
      }
      val savedBillingCycle = billingCycleRepository.save(billingCycle)


      // Create subscription service
      val subscriptionService = SubscriptionServices().apply {
        this.subscription = savedSubscription
        this.billingCycle = savedBillingCycle
        this.service = service
        this.quantity = numberOfSubscriptions
        this.subscriptNumber = numberOfSubscriptions
        this.price = service.price ?: BigDecimal.ZERO
        this.totalPrice =
          (service.price ?: BigDecimal.ZERO).multiply(numberOfSubscriptions.toBigDecimal()) // Deduce the amount due
        this.amountDue = totalPrice
        this.startDate = startDate
        this.endDate = endDate
        this.createdDate = LocalDateTime.now()
        this.updatedDate = LocalDateTime.now()
      }
      val savedSubscriptionService = subscriptionServiceRepository.save(subscriptionService)

      // Create payment line
      val paymentLine = PaymentLine().apply {
        this.billingCycle = savedBillingCycle
        this.amountPaid = BigDecimal.ZERO // No payment yet
        this.createdDate = LocalDateTime.now()
        this.updatedDate = LocalDateTime.now()
      }
      val savedPaymentLine = paymentLineRepository.save(paymentLine)

      // Save subscription options
      val options = serviceData["options"] as List<Map<String, Any?>>
      options.forEach { optionData ->
        val optionId = (optionData["id"] as Number).toLong()
        val quantity = (optionData["quantity"] as? Number)?.toInt() ?: 1
//       val price = (optionData["price"] as? Number)?.toInt() ?: 1
        val serviceOption = serviceOptionRepository.findById(optionId)
          .orElseThrow { IllegalArgumentException("Option not found with ID $optionId") }

        val saveSubscriptionOption = SubscriptionOptions().apply {
          this.paymentLine = savedPaymentLine
          this.subscriptionService = savedSubscriptionService
          this.subscription = savedSubscription
          this.option = serviceOption
          this.quantity = quantity
          this.price = (serviceOption.price ?: BigDecimal.ZERO)
          this.amountDue =
            (serviceOption.price
              ?: BigDecimal.ZERO).multiply(quantity.toBigDecimal()) * numberOfSubscriptions.toBigDecimal()// Deduce the amount due
          this.createdDate = LocalDateTime.now()
          this.updatedDate = LocalDateTime.now()
        }
        subscriptionOptionRepository.save(
          saveSubscriptionOption
        )
      }
    }

    return mapOf(
      "message" to "Subscription processed successfully",
      "invoiceId" to saveInvoice.id
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
}
