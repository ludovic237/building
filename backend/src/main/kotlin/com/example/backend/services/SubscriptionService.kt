package com.example.backend.services

import com.example.backend.constants.PaymentTypeConstants
import com.example.backend.constants.StatusConstants
import com.example.backend.dtos.SubscriptionDTO
import com.example.backend.dtos.SubscriptionDetailsDTO
import com.example.backend.models.*
import com.example.backend.repositories.*
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class SubscriptionService(
  private val serviceRepository: ServiceRepository,
  private val serviceOptionRepository: ServiceOptionRepository,
  private val subscriptionRepository: SubscriptionRepository,
  private val subscriptionOptionRepository: SubscriptionOptionRepository,
  private val serviceUsageRepository: ServiceUsageRepository,
  private val tenantRepository: TenantRepository,
  private val billingCycleRepository: BillingCycleRepository,
  private val paymentRepository: PaymentRepository,
  private val paymentLineRepository: PaymentLineRepository
) {

  fun getAllSubscriptions(): List<SubscriptionDTO> {
    return subscriptionRepository.findAll().map { subscription ->

      val tenantName = tenantRepository.findById(subscription.tenant!!.id!!).get()
        .let { tenant -> "${tenant.user!!.firstName} ${tenant.user!!.lastName}" }
      val serviceName = subscription.service!!.name
      SubscriptionDTO(
        id = subscription.id,
        tenantName = tenantName,
        serviceName = serviceName,
        moveInDate = subscription.startDate,
        moveOutDate = subscription.endDate,
        paymentStatus = subscription.status
      )
    }
  }

  fun getSubscriptionById(id: Long): Optional<Subscription> {
    return subscriptionRepository.findById(id)
  }

  fun createSubscription(subscription: Subscription): Subscription {
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

  fun getSubscriptionsByTenantId(tenantId: Long): List<Map<String, Any>> {
    // Retrieve the tenant or throw an exception if not found
    val tenant = tenantRepository.findById(tenantId)
      .orElseThrow { IllegalArgumentException("Tenant not found with ID $tenantId") }

    // Fetch all subscriptions for the given tenant
    val subscriptions = subscriptionRepository.findByTenant(tenant)
      ?: throw IllegalArgumentException("No subscriptions found for tenant with ID $tenantId")

    return subscriptions
      .filter { it.service?.isActive == true } // Filter only active services
      .map { subscription ->
        // Fetch the associated service
        val service = subscription.service
          ?: throw IllegalArgumentException("Service not found for subscription ID ${subscription.id}")


        // Calculate the remaining amount to be paid
        val billingCycles = billingCycleRepository.findBySubscriptionId(subscription.id!!)

        val billingCycleIds = billingCycles.map { it.id }
        println("Billing Cycle IDs: $billingCycleIds")
        val totalAmountPaid = paymentLineRepository.findAllByBillingCycleIdIn(billingCycleIds)
          .sumOf { it.amountPaid ?: BigDecimal.ZERO }
        println("Total Amount Paid: $totalAmountPaid")

        val totalAmountSubscription =
          (subscription.subscriptNumber ?: 0).toBigDecimal() * (subscription.price ?: BigDecimal.ZERO)
        val remainingAmount = totalAmountSubscription - totalAmountPaid

        // Calculate the number of unpaid cycles
        val remainingCycles = subscription.subscriptNumber!! - billingCycles.size

        // Build the result map
        mapOf(
          "id" to subscription.id!!,
          "serviceId" to service.id!!,
          "serviceName" to service.name!!,
          "serviceCode" to service.code!!,
          "serviceDescription" to service.description!!,
          "billingMode" to service.billingMode!!,
          "remainingCycles" to remainingCycles,
          "remainingAmount" to remainingAmount
        )
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

    val service = subscription.service
      ?: throw IllegalArgumentException("Service not found for subscription ID $subscriptionId")

    val billingPrice = subscription.price ?: BigDecimal.ZERO

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
      val currentEndDate = when (service.billingMode!!.lowercase()) {
        "monthly" -> currentStartDate?.plusMonths(1)
        "yearly" -> currentStartDate?.plusYears(1)
        else -> throw IllegalArgumentException("Unsupported billing mode")
      }

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

    return mapOf(
      "message" to "Payment processed successfully",
      "paymentId" to payment.id,
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

    val options = subscriptionOptionRepository.findBySubscription(subscription)
    val usage = serviceUsageRepository.findBySubscription(subscription)

    val allOptionsUsed = options.all { option ->
      val usedQuantity = usage.filter { usageItem -> usageItem.option!!.id == option.id }.sumOf { it.quantityUsed ?: 0 }
      usedQuantity >= (option.quantity ?: 0)
    }

    val endDate = subscription.endDate
    return when {
      endDate != null && endDate.isBefore(LocalDateTime.now()) -> "Expired"
      allOptionsUsed -> "Completed"
      else -> "Active"
    }
  }

  fun createSubscriptionWithDetails(
    tenantId: Long,
    serviceId: Long,
    dateDebut: String,
    dateFin: String,
    status: String,
    options: List<Map<String, Any>>
  ): Subscription {
    val tenant = tenantRepository.findById(tenantId)
      .orElseThrow { IllegalArgumentException("Tenant not found with ID $tenantId") }

    val service = serviceRepository.findById(serviceId)
      .orElseThrow { IllegalArgumentException("Service not found with ID $serviceId") }
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val subscription = Subscription().apply {
      this.tenant = tenant
      this.service = service
      this.startDate = LocalDateTime.parse(dateDebut, formatter) // Extract only the date part
      this.endDate = LocalDateTime.parse(dateFin, formatter) // Extract only the date part
      this.status = status
      this.createdDate = LocalDateTime.now()
      this.updatedDate = LocalDateTime.now()
    }

    // Save the subscription
    val savedSubscription = subscriptionRepository.save(subscription)

    // Save the options
    options.forEach { option ->
      val optionId = (option["id"] as Number).toLong()
      val serviceOption = serviceOptionRepository.findById(optionId)
        .orElseThrow { IllegalArgumentException("Service option not found with ID $optionId") }
      val quantity = (option["quantity"] as Number).toInt()
      subscriptionOptionRepository.save(
        SubscriptionOption().apply {
          this.subscription = savedSubscription
          this.quantity = quantity
          this.option = serviceOption
          this.createdDate = LocalDateTime.now()
          this.updatedDate = LocalDateTime.now()
        }
      )
    }

    return savedSubscription
  }

  fun getSubscriptionWithDetails(
    subscriptionId: Long,
  ): Map<String, Any?> {
    val subscription = subscriptionRepository.getById(subscriptionId)

    val subscriptionOptions = subscriptionOptionRepository.findBySubscription(subscription)

    val formattedData = mapOf(
      "tenantId" to (subscription.tenant?.id ?: throw IllegalArgumentException("Tenant is null")),
      "serviceId" to (subscription.service?.id ?: throw IllegalArgumentException("Service is null")),
      "dateDebut" to subscription.startDate.toString(),
      "dateFin" to subscription.endDate.toString(),
      "status" to subscription.status,
      "options" to subscriptionOptions.map { subscriptionOption ->
        mapOf(
          "subscriptionOptionId" to subscriptionOption.id,
          "subscriptionId" to subscriptionOption.subscription?.id,
          "optionId" to subscriptionOption.option?.id,
          "quantity" to subscriptionOption.quantity
        )
      }
    )
    return formattedData
  }

  fun updateSubscriptionStatus(subscriptionId: Long, newStatus: String): Map<String,Any?>  {
      var subscription = subscriptionRepository.findById(subscriptionId)
          .orElseThrow { IllegalArgumentException("Subscription not found with ID $subscriptionId") }

      subscription.status = newStatus
      subscription.updatedDate = LocalDateTime.now()
      return validateAndSaveSubscription(subscription)
  }

  fun validateAndSaveSubscription(subscription: Subscription):Map<String, Any?> {
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

}
