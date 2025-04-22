package com.example.backend.repositories

import com.example.backend.models.BillingCycle
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.*

interface BillingCycleRepository : JpaRepository<BillingCycle, Long> {

/*  // Find billing cycles by tenant ID
  fun findByTenantId(tenantId: Long): List<BillingCycle>*/

  // Find billing cycles by status
  fun findByStatus(status: String): List<BillingCycle>

  // Find billing cycles within a specific period
  fun findByPeriodStartBetween(startDate: LocalDate, endDate: LocalDate): List<BillingCycle>

/*  // Find a billing cycle by its ID
  fun findById(billingCycleId: Long): Optional<BillingCycle>*/
}
