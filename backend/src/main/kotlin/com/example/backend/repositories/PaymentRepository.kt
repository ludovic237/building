package com.example.backend.repositories

import com.example.backend.models.Payment
import com.example.backend.models.Tenant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

interface PaymentRepository : JpaRepository<Payment, Long> {

  fun findByTenant(tenant: Tenant): List<Payment>

  // Find payments by tenant ID
  fun findByTenantId(tenantId: Long): List<Payment>

  /*   // Find payments by status
     fun findByStatus(status: String): List<Payment>
 */
  /* // Find payments by total amount greater than or equal to a specific value
   fun findByTotalAmountGreaterThanEqual(amount: Double): List<Payment>
*/
  // Find payments by payment method
  fun findByPaymentMethod(paymentMethod: String): List<Payment>

  /*  // Find a payment by its ID
    fun findById(paymentId: Long): Optional<Payment>*/

  @Query("SELECT SUM(p.totalAmount) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
  fun findTotalPaymentsBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): BigDecimal?

}
