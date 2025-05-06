package com.example.backend.services

import com.example.backend.dtos.PaymentDTO
import com.example.backend.dtos.PaymentLineDetailDTO
import com.example.backend.models.PaymentLine
import com.example.backend.repositories.PaymentLineRepository
import com.example.backend.repositories.PaymentLinesViewRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PaymentLineService(
  private val paymentLineRepository: PaymentLineRepository,
  private val paymentLinesViewRepository: PaymentLinesViewRepository
) {

  fun getAllPaymentLines(): List<PaymentLineDetailDTO> {
    return paymentLinesViewRepository.findAll().map { payment ->
      PaymentLineDetailDTO(
        id = payment.paymentLineId,
        amountPaid = payment.amountPaid,
        paymentDate = payment.paymentDate,
        serviceDescription = payment.serviceDescription,
        paymentMethod = payment.paymentMethod,
        subscriptionStatus =payment.subscriptionStatus,
        billingCycleStatus =payment.billingCycleStatus,
        serviceBillingMode =payment.serviceBillingMode,
        tenantName = "${payment.userLastName} ${payment.userLastName}",
        serviceName = payment.serviceName
      )
    }
  }

  fun getPaymentById(id: Long): Optional<PaymentLine> {
    return paymentLineRepository.findById(id)
  }

  fun createPayment(payment: PaymentLine): PaymentLine {
    return paymentLineRepository.save(payment)
  }

  fun updatePayment(id: Long, updatedPayment: PaymentLine): PaymentLine {
    val existingPayment = paymentLineRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Payment with ID $id not found") }

    return paymentLineRepository.save(updatedPayment)
  }

  fun deletePayment(id: Long) {
    if (!paymentLineRepository.existsById(id)) {
      throw IllegalArgumentException("Payment with ID $id not found")
    }
    paymentLineRepository.deleteById(id)
  }
}
