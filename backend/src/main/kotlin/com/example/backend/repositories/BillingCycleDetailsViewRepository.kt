package com.example.backend.repositories

import com.example.backend.models.BillingCycleDetailsView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate

@Repository
interface BillingCycleDetailsViewRepository : JpaRepository<BillingCycleDetailsView, Long> {

    // Find by billing cycle status
    fun findByBillingCycleStatus(status: String): List<BillingCycleDetailsView>

    // Find by tenant ID
    fun findByTenantId(tenantId: Long): List<BillingCycleDetailsView>

    // Find by user username
    fun findByUserUsername(username: String): List<BillingCycleDetailsView>

    // Find by payment ID
    fun findByPaymentId(paymentId: Long): List<BillingCycleDetailsView>

    // Find by date range
    fun findByStartDateBetween(startDate: LocalDate, endDate: LocalDate): List<BillingCycleDetailsView>

    // Find by amount due greater than or equal to a value
    fun findByAmountDueGreaterThanEqual(amount: BigDecimal): List<BillingCycleDetailsView>

    // Find by amount due less than or equal to a value
    fun findByAmountDueLessThanEqual(amount: BigDecimal): List<BillingCycleDetailsView>

    // Find by subscription name
    fun findBySubscriptionName(subscriptionName: Long): List<BillingCycleDetailsView>

    // Find by billing mode
    fun findByBillingMode(billingMode: String): List<BillingCycleDetailsView>

    // Find by user first name and last name
    fun findByUserFirstNameAndUserLastName(firstName: String, lastName: String): List<BillingCycleDetailsView>

    @Query("""
        SELECT b FROM BillingCycleDetailsView b
        WHERE (:status IS NULL OR b.billingCycleStatus = :status)
        AND (:tenantId IS NULL OR b.tenantId = :tenantId)
        AND (:username IS NULL OR b.userUsername = :username)
        AND (:startDate IS NULL OR b.startDate >= :startDate)
        AND (:endDate IS NULL OR b.endDate <= :endDate)
        AND (:userFirstName IS NULL OR b.userFirstName = :userFirstName)
        AND (:userLastName IS NULL OR b.userLastName = :userLastName)
    """)
    fun filterBillingCycles(
      @Param("status") status: String?,
      @Param("tenantId") tenantId: Long?,
      @Param("username") username: String?,
      @Param("startDate") startDate: LocalDate?,
      @Param("endDate") endDate: LocalDate?,
      @Param("userFirstName") userFirstName: String?,
      @Param("userLastName") userLastName: String?
    ): List<BillingCycleDetailsView>
}
