package com.example.backend.repositories

  import com.example.backend.models.Rent
  import com.example.backend.models.Tenant
  import org.springframework.data.jpa.repository.JpaRepository
  import java.math.BigDecimal
  import java.time.LocalDateTime

  interface RentRepository : JpaRepository<Rent, Long> {

      // Find rents by tenant
      fun findByTenant(tenant: Tenant): List<Rent>

      // Find rents by month and year
      fun findByMonthAndYear(month: Int, year: Int): List<Rent>

      // Find rents with an amount greater than or equal to a specific value
      fun findByAmountGreaterThanEqual(amount: BigDecimal): List<Rent>

      // Find rents by payment date
      fun findByPaymentDate(paymentDate: LocalDateTime): List<Rent>

      // Find rents by status
      fun findByStatus(status: String): List<Rent>

      // Check if a rent exists by tenant and month/year
      fun existsByTenantAndMonthAndYear(tenant: Tenant, month: Int, year: Int): Boolean
  }
