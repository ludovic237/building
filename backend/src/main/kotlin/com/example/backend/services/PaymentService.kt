package com.example.backend.services

import com.example.backend.dtos.PaymentDTO
import com.example.backend.models.Payment
import com.example.backend.repositories.PaymentRepository
import com.example.backend.repositories.PaymentsViewRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PaymentService(
  private val paymentRepository: PaymentRepository,
  private val paymentsViewRepository: PaymentsViewRepository,
) {

  fun getAllPayments(): List<PaymentDTO> {
    return paymentsViewRepository.findAll().map { payment ->
      PaymentDTO(
        id = payment.paymentId,
        amountPaid = payment.paymentTotalAmount,
        paymentDate = payment.paymentDate,
        serviceName = payment.serviceName,
        serviceDescription = payment.serviceDescription,
        paymentMethod = payment.paymentMethod,
        billingCycleStatus =payment.billingCycleStatus,
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
