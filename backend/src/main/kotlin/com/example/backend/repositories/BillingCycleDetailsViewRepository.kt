package com.example.backend.repositories

import com.example.backend.models.BillingCycleDetailsView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface BillingCycleDetailsViewRepository : JpaRepository<BillingCycleDetailsView, Long> {

  // Find by status
  fun findByBillingCycleId(billingCycleId: Long): BillingCycleDetailsView

  // Find by status
  fun findByBillingCycleStatus(billingCycleStatus: String): List<BillingCycleDetailsView>

  // Find by tenant ID
  fun findByTenantId(tenantId: Long): List<BillingCycleDetailsView>

  // Find by username
  fun findByUserUsername(username: String): List<BillingCycleDetailsView>

  // Find by billing cycle date range
  fun findByStartDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<BillingCycleDetailsView>

  // Find by user first name and last name
  fun findByUserFirstNameAndUserLastName(firstName: String, lastName: String): List<BillingCycleDetailsView>

  @Query(
    """
                SELECT b FROM BillingCycleDetailsView b
                WHERE (:billingCycleStatus IS NULL OR b.billingCycleStatus = :billingCycleStatus)
                AND (:tenantId IS NULL OR b.tenantId = :tenantId)
                AND (:username IS NULL OR b.userUsername = :username)
                AND (:startDate IS NULL OR b.startDate >= :startDate)
                AND (:endDate IS NULL OR b.endDate <= :endDate)
                AND (:userFirstName IS NULL OR b.userFirstName = :userFirstName)
                AND (:userLastName IS NULL OR b.userLastName = :userLastName)
            """
  )
  fun filterBillingCycles(
    @Param("billingCycleStatus") billingCycleStatus: String?,
    @Param("tenantId") tenantId: Long?,
    @Param("username") username: String?,
    @Param("startDate") startDate: LocalDateTime?,
    @Param("endDate") endDate: LocalDateTime?,
    @Param("userFirstName") userFirstName: String?,
    @Param("userLastName") userLastName: String?
  ): List<BillingCycleDetailsView>
}
