package com.example.backend.services

import com.example.backend.dtos.SubscriptionDTO
import com.example.backend.dtos.SubscriptionDetailsDTO
import com.example.backend.models.Payment
import com.example.backend.models.PaymentLine
import com.example.backend.models.ServiceUsage
import com.example.backend.models.Subscription
import com.example.backend.repositories.*
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@Service
class SubscriptionService(
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
    return subscriptionRepository.save(subscription)
  }

  fun updateSubscription(id: Long, updatedSubscription: Subscription): Subscription {
    val existingSubscription = subscriptionRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Subscription with ID $id not found") }

    existingSubscription.startDate = updatedSubscription.startDate
    existingSubscription.endDate = updatedSubscription.endDate
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

        var billingCycleIds = billingCycles.filter { it.status == "Due" || it.status == "Partial Paid" }.map { it.id }
        println("Billing Cycle IDs: $billingCycleIds")
        var totalAmountPaid = paymentLineRepository.findAllByBillingCycleIdIn(billingCycleIds)
          .sumOf { it.amountPaid ?: BigDecimal.ZERO }
        println("Total Amount Paid: $totalAmountPaid")
        var remainingAmount = billingCycles
          .filter { it.status == "Due" || it.status == "Partial Paid" }
          .sumOf { it.amountDue ?: BigDecimal.ZERO }
        println("Remaining Amount: $remainingAmount")
        remainingAmount = remainingAmount - totalAmountPaid

        // Calculate the number of unpaid cycles
        val remainingCycles = billingCycles
          .count { it.status == "Due" }

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
    var paymentAmount = paymentAmount

    // Retrieve tenant and subscription
    val tenant = tenantRepository.findById(tenantId)
      .orElseThrow { IllegalArgumentException("Tenant not found with ID $tenantId") }

    val subscription = subscriptionRepository.findById(subscriptionId)
      .orElseThrow { IllegalArgumentException("Subscription not found with ID $subscriptionId") }

    // Fetch unpaid billing cycles for the subscription, sorted by period_start
    val unpaidBillingCycles = billingCycleRepository.findBySubscriptionId(subscriptionId)
      .filter { it.status == "Due" || it.status == "Partial Paid" }
      .sortedBy { it.periodStart }

    if (unpaidBillingCycles.isEmpty()) {
      throw IllegalArgumentException("No unpaid billing cycles found for subscription ID $subscriptionId")
    }

    // Create a new payment record
    val payment = paymentRepository.save(
      Payment().apply {
        this.tenant = tenant
        this.totalAmount = paymentAmount
        this.paymentMethod = paymentMode
        this.paymentDate = LocalDate.now()
      }
    )

    // Distribute the payment amount to unpaid billing cycles
    for (billingCycle in unpaidBillingCycles) {
      if (paymentAmount <= BigDecimal.ZERO) break

      // Retrieve or create a payment line for the billing cycle
      val paymentLine = paymentLineRepository.findByBillingCycleId(billingCycle.id!!) ?: PaymentLine()

      // Calculate the amount already paid and the remaining amount to pay
      val alreadyPaid = paymentLine.amountPaid ?: BigDecimal.ZERO
      val amountToPay = billingCycle.amountDue!! - alreadyPaid

      // Determine the amount to pay for this cycle
      val paid = paymentAmount.min(amountToPay)
      paymentAmount -= paid

      // Update the billing cycle status
      billingCycle.status = when {
        paid == amountToPay -> "Paid"
        paid > BigDecimal.ZERO -> "Partial Paid"
        else -> billingCycle.status
      }

      billingCycleRepository.save(billingCycle)

      // Update or create the payment line
      paymentLine.apply {
        this.payment = if (billingCycle.status == "Due") null else payment
        this.billingCycle = billingCycle
        this.amountPaid = alreadyPaid + paid
      }

      paymentLineRepository.save(paymentLine)
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
      subscription.startDate!! <= LocalDate.now() &&
      (subscription.endDate == null || subscription.endDate!! >= LocalDate.now())
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
      endDate != null && endDate.isBefore(LocalDate.now()) -> "Expired"
      allOptionsUsed -> "Completed"
      else -> "Active"
    }
  }


}
