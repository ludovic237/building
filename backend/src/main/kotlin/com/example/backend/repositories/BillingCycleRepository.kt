package com.example.backend.repositories

import com.example.backend.models.BillingCycle
import com.example.backend.models.Subscription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

interface BillingCycleRepository : JpaRepository<BillingCycle, Long> {

/*  // Find billing cycles by tenant ID
  fun findByTenantId(tenantId: Long): List<BillingCycle>*/

  fun findBySubscriptionId(subscriptionId: Long): List<BillingCycle>

  fun findBySubscription(subscription: Subscription): List<BillingCycle>

  // Find billing cycles by status
  fun findByStatus(status: String): List<BillingCycle>

  // Find billing cycles within a specific period
  fun findByPeriodStartBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<BillingCycle>

/*  // Find a billing cycle by its ID
  fun findById(billingCycleId: Long): Optional<BillingCycle>*/

  @Query("SELECT SUM(bc.amountDue) FROM BillingCycle bc WHERE bc.status = 'Due' AND bc.periodEnd BETWEEN :startDate AND :endDate")
  fun findTotalOverduePaymentsBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): BigDecimal?

  @Query("SELECT SUM(bc.amountDue) FROM BillingCycle bc WHERE bc.status = 'Partial Paid' AND bc.periodEnd BETWEEN :startDate AND :endDate")
  fun findTotalPendingPaymentsBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): BigDecimal?

  @Query("SELECT COUNT(bc) FROM BillingCycle bc WHERE bc.status = 'Paid' AND bc.periodEnd BETWEEN :startDate AND :endDate")
  fun countPaidBillingCyclesBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): Int

  @Query("SELECT COUNT(bc) FROM BillingCycle bc WHERE bc.status = 'Partial Paid' AND bc.periodEnd BETWEEN :startDate AND :endDate")
  fun countPartiallyPaidBillingCyclesBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): Int

  @Query("SELECT COUNT(bc) FROM BillingCycle bc WHERE bc.status = 'Due' AND bc.periodEnd BETWEEN :startDate AND :endDate")
  fun countOverdueBillingCyclesBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): Int

  @Query("""
      SELECT COALESCE(SUM(pl.amountPaid), 0)
      FROM PaymentLine pl
      WHERE pl.billingCycle.id = :billingCycleId
  """)
  fun getTotalPaymentsForBillingCycle(billingCycleId: Long): BigDecimal
}
