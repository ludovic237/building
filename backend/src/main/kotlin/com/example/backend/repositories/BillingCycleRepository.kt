package com.example.backend.repositories

import com.example.backend.models.BillingCycle
import com.example.backend.models.Subscription
import org.springframework.data.jpa.repository.JpaRepository
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
}
