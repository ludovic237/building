package com.example.backend.repositories

import com.example.backend.models.PaymentLine
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentLineRepository : JpaRepository<PaymentLine, Long> {

  // Find payment lines by payment ID
  fun findByPaymentId(paymentId: Long): PaymentLine

  fun findAllByBillingCycleIdIn(billingCycleIds: List<Long?>): List<PaymentLine>

  // Find payment lines by billing cycle ID
  fun findByBillingCycleId(billingCycleId: Long): PaymentLine

 /* // Find payment lines by amount paid greater than or equal to a specific value
  fun findByAmountPaidGreaterThanEqual(amountPaid: Double): List<PaymentLine>

  // Find a payment line by its ID
  fun findById(paymentLineId: Long): Optional<PaymentLine>
*/
}
