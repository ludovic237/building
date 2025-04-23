package com.example.backend.services

import com.example.backend.dtos.PaymentDTO
import com.example.backend.models.Payment
import com.example.backend.repositories.PaymentRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PaymentLineService(
  private val paymentRepository: PaymentRepository
) {

  fun getAllPaymentLines(): List<PaymentDTO> {
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
