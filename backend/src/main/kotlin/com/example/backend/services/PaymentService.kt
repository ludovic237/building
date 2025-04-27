package com.example.backend.services

import com.example.backend.dtos.PaymentDTO
import com.example.backend.models.Payment
import com.example.backend.models.PaymentsView
import com.example.backend.repositories.*
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class PaymentService(
  private val billingCycleDetailsViewRepository: BillingCycleDetailsViewRepository,
  private val billingCycleRepository: BillingCycleRepository,
  private val paymentLineRepository: PaymentLineRepository,
  private val paymentRepository: PaymentRepository,
  private val paymentsViewRepository: PaymentsViewRepository,
) {

  fun getAllPayments(): List<PaymentDTO> {
    return paymentsViewRepository.findAll().map { payment ->
      println("Payment: ${payment.toString()}")
      PaymentDTO(
        id = payment.paymentId,
        amountPaid = payment.amountPaid,
        paymentDate = payment.paymentDate,
        serviceName = payment.serviceName,
        serviceDescription = payment.serviceDescription,
        paymentMethod = payment.paymentMethod,
        billingCycleStatus = payment.billingCycleStatus,
      )
    }
  }

/*fun getAllPaymentsSimple(): List<Map<String, Any?>> {
    return paymentRepository.findAll().map { payment ->
      var pa
        mapOf(
            "paymentId" to payment.id,
            "amountPaid" to payment.paymentLines.sumOf { it.amountPaid ?: BigDecimal.ZERO },
            "paymentDate" to payment.paymentDate,
            "paymentMethod" to payment.paymentMethod,
            "billingCycleStatus" to payment.paymentLines.firstOrNull()?.billingCycle?.status,
            "subscription" to payment.paymentLines.firstOrNull()?.billingCycle?.subscription?.let { subscription ->
                mapOf(
                    "subscriptionId" to subscription.id,
                    "subscriptionStatus" to subscription.status
                )
            },
            "tenant" to payment.tenant?.let { tenant ->
                mapOf(
                    "tenantId" to tenant.id,
                    "moveInDate" to tenant.moveInDate,
                    "moveOutDate" to tenant.moveOutDate,
                    "securityDeposit" to tenant.securityDeposit
                )
            },
            "service" to payment.paymentLines.firstOrNull()?.billingCycle?.subscription?.service?.let { service ->
                mapOf(
                    "serviceId" to service.id,
                    "serviceName" to service.name,
                    "serviceDescription" to service.description,
                    "serviceCode" to service.code,
                    "billingMode" to service.billingMode,
                    "isActive" to service.isActive
                )
            }
        )
    }
}*/

  fun getPaymentById(id: Long): Optional<Payment> {
    return paymentRepository.findById(id)
  }

  fun createPayment(payment: Payment): Payment {
    return paymentRepository.save(payment)
  }

  fun updatePayment(id: Long, updatedPayment: Payment): Payment {
    val existingPayment = paymentRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Payment with ID $id not found") }

    return paymentRepository.save(updatedPayment)
  }

  fun deletePayment(id: Long) {
    if (!paymentRepository.existsById(id)) {
      throw IllegalArgumentException("Payment with ID $id not found")
    }
    paymentRepository.deleteById(id)
  }

  fun deletePaymentWithValidationAndUpdate(paymentId: Long): Map<String, String> {
    var message: String = ""
    val paymentToDelete = paymentsViewRepository.findById(paymentId)
      .orElseThrow { IllegalArgumentException("Payment with ID $paymentId not found") }

    val subscriptionId = paymentToDelete.subscriptionId
    val payments = paymentsViewRepository.findBySubscriptionId(subscriptionId!!)

    val partialPaidPayment = payments.find { it.billingCycleStatus == "Partial Paid" }
    if (partialPaidPayment != null) {
      if (partialPaidPayment.paymentId == paymentId) {
        paymentRepository.deleteById(paymentId)
        updatePaymentLinesAndBillingCycles(paymentToDelete, isDeleted = true)
        println("Deleted payment with status Partial Paid and updated related tables.")
        message = ("Deleted payment with status Partial Paid and updated related tables.")
      } else {
        throw IllegalStateException("A payment with status Partial Paid exists. Delete it first.")
      }
    } else {
      val lastPayment = payments.maxByOrNull { it.paymentDate!! }
      if (lastPayment != null && lastPayment.paymentId == paymentId) {
        paymentRepository.deleteById(paymentId)
        updatePaymentLinesAndBillingCycles(paymentToDelete, isDeleted = true)
        println("Deleted the last payment and updated related tables.")
        message = ("Deleted the last payment and updated related tables.")
      } else {
        throw IllegalStateException("Only the last payment can be deleted.")
      }
    }
    return mapOf(
      "message" to message,
    )
  }

  private fun updatePaymentLinesAndBillingCycles(payment: PaymentsView, isDeleted: Boolean) {
    val paymentLineId = payment.paymentLineId
    if (paymentLineId != null) {
      val paymentLine = paymentLineRepository.findById(paymentLineId)
        .orElseThrow { IllegalArgumentException("Payment line with ID $paymentLineId not found") }

      if (isDeleted) {
        paymentLine.amountPaid = paymentLine.amountPaid?.minus(payment.paymentTotalAmount ?: BigDecimal.ZERO)
        if (paymentLine.amountPaid == BigDecimal.ZERO) {
          paymentLine.payment = null
        }
        paymentLineRepository.save(paymentLine)
        println("Updated payment_lines for paymentLineId: $paymentLineId")
      }
    }

    val billingCycleId = payment.billingCycleId
    if (billingCycleId != null) {
      val billingCycleDetails = billingCycleDetailsViewRepository.findByBillingCycleId(billingCycleId)

      val billingCycle = billingCycleRepository.findById(billingCycleId)
        .orElseThrow { IllegalArgumentException("Billing cycle with ID $billingCycleId not found") }

      if (isDeleted) {
        billingCycleDetails.billingCycleStatus =
          if (billingCycleDetails.paymentLineAmountPaid!! < billingCycleDetails.amountDue) {
            "Partial Paid"
          } else {
            "Due"
          }
        billingCycleRepository.save(billingCycle)
        println("Updated billing_cycles for billingCycleId: $billingCycleId")
      }
    }
  }

}
