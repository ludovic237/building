package com.example.backend.services

import com.example.backend.dtos.SubscriptionDTO
import com.example.backend.dtos.SubscriptionDetailsDTO
import com.example.backend.models.Subscription
import com.example.backend.repositories.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class SubscriptionService(
  private val subscriptionRepository: SubscriptionRepository,
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
}
