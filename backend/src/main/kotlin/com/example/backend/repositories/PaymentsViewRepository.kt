package com.example.backend.repositories

import com.example.backend.models.PaymentsView
import com.example.backend.models.Subscription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
interface PaymentsViewRepository : JpaRepository<PaymentsView, Long> {


  // Find by payment method
  fun findByPaymentMethod(paymentMethod: String): List<PaymentsView>

  // Find by tenant ID
  fun findByTenantId(tenantId: Long): List<PaymentsView>

  // Find by subscription ID
  fun findBySubscriptionId(subscriptionId: Long): List<PaymentsView>

  // Find by username
  fun findByUserUsername(username: String): List<PaymentsView>

  // Find by payment date range
  fun findByPaymentDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<PaymentsView>

  // Find by user first name and last name
  fun findByUserFirstNameAndUserLastName(firstName: String, lastName: String): List<PaymentsView>

  @Query(
    """
        SELECT p FROM PaymentsView p
        WHERE (:paymentMethod IS NULL OR p.paymentMethod = :paymentMethod)
        AND (:tenantId IS NULL OR p.tenantId = :tenantId)
        AND (:username IS NULL OR p.userUsername = :username)
        AND (:startDate IS NULL OR p.paymentDate >= :startDate)
        AND (:endDate IS NULL OR p.paymentDate <= :endDate)
        AND (:totalAmountMin IS NULL OR p.paymentTotalAmount >= :totalAmountMin)
        AND (:totalAmountMax IS NULL OR p.paymentTotalAmount <= :totalAmountMax)
        AND (:userFirstName IS NULL OR p.userFirstName = :userFirstName)
        AND (:userLastName IS NULL OR p.userLastName = :userLastName)
    """
  )
  fun filterPayments(
    @Param("paymentMethod") paymentMethod: String?,
    @Param("tenantId") tenantId: Long?,
    @Param("username") username: String?,
    @Param("startDate") startDate: LocalDateTime?,
    @Param("endDate") endDate: LocalDateTime?,
    @Param("totalAmountMin") totalAmountMin: BigDecimal?,
    @Param("totalAmountMax") totalAmountMax: BigDecimal?,
    @Param("userFirstName") userFirstName: String?,
    @Param("userLastName") userLastName: String?
  ): List<PaymentsView>
}
