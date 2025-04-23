package com.example.backend.services

import com.example.backend.dtos.BillingCycleDetailsDTO
import com.example.backend.dtos.PaymentDTO
import com.example.backend.models.Payment
import com.example.backend.repositories.BillingCycleRepository
import com.example.backend.repositories.PaymentLineRepository
import com.example.backend.repositories.PaymentRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class BillingCycleService(
  private val paymentRepository: PaymentRepository,
  private val paymentLineRepository: PaymentLineRepository,
  private val billingCycleRepository: BillingCycleRepository,
) {

  fun getAllBillingCycles(): List<PaymentDTO> {
    return paymentRepository.findAll().map { payment ->
      PaymentDTO(
        id = payment.id,
        amount = payment.totalAmount,
        date = payment.paymentDate,
        description = "payment,.description",
        paymentMethod = payment.paymentMethod,
        status =" payment.p",
      )
    }
  }

  fun getBillingCycleDetails(): List<BillingCycleDetailsDTO> {
    // Fetch billing cycles and join with related entities
    val billingCycles = billingCycleRepository.findAll()
    return billingCycles.map { cycle ->
      println("Cycle: $cycle")
      val paymentLine = paymentLineRepository.findByPaymentId(cycle.id!!)

      if (paymentLine.paymentId == null) {
          throw IllegalArgumentException("Payment ID is null for billing cycle ID ${cycle.id}")
      }

      val payment = paymentRepository.findById(paymentLine.paymentId!!)
          .orElseThrow { IllegalArgumentException("Payment not found for payment ID ${paymentLine.paymentId}") }

      BillingCycleDetailsDTO(
          billingCycleId = cycle.id!!,
          startDate = cycle.periodStart!!,
          endDate = cycle.periodEnd!!,
          amountDue = cycle.amountDue!!,
          amountPaid = paymentLine.amountPaid,
          status = cycle.status!!,
          subscriptionName = cycle.subscription!!.service!!.name!!,
          tenantName = cycle.subscription!!.tenant!!.user!!.firstName!! + " " + cycle.subscription!!.tenant!!.user!!.lastName!!,
          userName = cycle.subscription!!.tenant!!.user!!.username!!,
          payment = payment,
          payments = paymentRepository.findByTenant(cycle.subscription!!.tenant!!).map { payment ->
              PaymentDTO(
                  id = payment.id,
                  amount = payment.totalAmount,
                  paymentMethod = payment.paymentMethod,
                  status = "payment.status",
                  date = payment.paymentDate,
                  description = "payment.description"
              )
          }
      )
    }
  }

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
}
