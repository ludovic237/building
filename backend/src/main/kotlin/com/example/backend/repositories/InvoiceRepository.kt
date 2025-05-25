package com.example.backend.repositories

import com.example.backend.models.Invoice
import com.example.backend.models.Tenant
import com.example.backend.models.User
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal
import java.time.LocalDateTime

interface InvoiceRepository : JpaRepository<Invoice, Long> {

    // Find invoices by tenant
    fun findByUser(user: User): List<Invoice>

    // Find invoices by type
    fun findByType(type: String): List<Invoice>

    // Find invoices by status
    fun findByStatus(status: String): List<Invoice>

    fun findByTenantIdAndStatus(tenantId:Long, status: String): List<Invoice>

    fun findByTenantIdAndStatusIsNot(tenantId:Long, status: String): List<Invoice>

    fun findByIdAndStatusIsNot(id:Long, status: String): Invoice

    // Find invoices by month and year
    fun findByMonthAndYear(month: Int, year: Int): List<Invoice>

    // Find invoices with an amount greater than or equal to a specific value
    fun findByAmountGreaterThanEqual(amount: BigDecimal): List<Invoice>

    // Find invoices by payment date
    fun findByPaymentDate(paymentDate: LocalDateTime): List<Invoice>

    // Check if an invoice exists by tenant and month/year
    fun existsByUserAndMonthAndYear(user: User, month: Int, year: Int): Boolean
}
