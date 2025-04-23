package com.example.backend.services

import com.example.backend.dtos.BillingCycleDetailsDTO
import com.example.backend.dtos.PaymentDTO
import com.example.backend.models.BillingCycleDetailsView
import com.example.backend.models.Payment
import com.example.backend.repositories.BillingCycleDetailsViewRepository
import com.example.backend.repositories.BillingCycleRepository
import com.example.backend.repositories.PaymentLineRepository
import com.example.backend.repositories.PaymentRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class BillingCycleService(
  private val paymentRepository: PaymentRepository,
  private val paymentLineRepository: PaymentLineRepository,
  private val billingCycleRepository: BillingCycleRepository,
  private val billingCycleDetailsViewRepository: BillingCycleDetailsViewRepository,
) {

  fun getAllBillingCycles(): List<PaymentDTO> {
    return billingCycleDetailsViewRepository.findAll().map { payment ->
      PaymentDTO(
        id = payment.billingCycleId,
        amountPaid = payment.paymentLineAmountPaid,
        paymentDate = payment.paymentDate,
        serviceName = payment.serviceName,
        serviceDescription = payment.serviceDescription,
        paymentMethod = payment.paymentId?.let { paymentRepository.findById(it).orElse(null) }?.paymentMethod,
        billingCycleStatus = payment.billingCycleStatus,
      )
    }
  }

  fun getBillingCycleDetails(): List<BillingCycleDetailsDTO> {
    // Fetch billing cycles and join with related entities
    val billingCycles = billingCycleRepository.findAll()
    return billingCycles.map { cycle ->
      println("Cycle: ${cycle.id}, ${cycle.periodStart}, ${cycle.periodEnd}, ${cycle.amountDue}, ${cycle.status}")
      val paymentLine = paymentLineRepository.findByBillingCycleId(cycle.id!!)

//      val payment = paymentLine.paymentId?.let {
//        paymentRepository.findById(it).orElse(null)
//      }

      val payment = paymentRepository.findById(paymentLine.payment!!.id!!).orElse(null)

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
        payment = payment
      )
    }
  }

  fun getBillingCycleDetailsView(): List<BillingCycleDetailsView> {
   return billingCycleDetailsViewRepository.findAll()
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

  fun filterBillingCycles(
    status: String?,
    tenantId: Long?,
    username: String?,
    startDate: LocalDate?,
    endDate: LocalDate?,
    userFirstName: String?,
    userLastName: String?
  ): List<BillingCycleDetailsView> {
      return billingCycleDetailsViewRepository.filterBillingCycles(
          status, tenantId, username, startDate, endDate, userFirstName, userLastName
      )
  }
}
