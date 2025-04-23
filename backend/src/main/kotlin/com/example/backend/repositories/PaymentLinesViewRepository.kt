package com.example.backend.repositories

    import com.example.backend.models.PaymentLinesView
    import org.springframework.data.jpa.repository.JpaRepository
    import org.springframework.data.jpa.repository.Query
    import org.springframework.data.repository.query.Param
    import org.springframework.stereotype.Repository
    import java.math.BigDecimal
    import java.time.LocalDate

    @Repository
    interface PaymentLinesViewRepository : JpaRepository<PaymentLinesView, Long> {

        // Find by payment method
        fun findByPaymentMethod(paymentMethod: String): List<PaymentLinesView>

        // Find by tenant ID
        fun findByTenantId(tenantId: Long): List<PaymentLinesView>

        // Find by user username
        fun findByUserUsername(username: String): List<PaymentLinesView>

        // Find by payment date range
        fun findByPaymentDateBetween(startDate: LocalDate, endDate: LocalDate): List<PaymentLinesView>

        // Find by amount paid greater than or equal to a value
        fun findByAmountPaidGreaterThanEqual(amount: BigDecimal): List<PaymentLinesView>

        // Find by amount paid less than or equal to a value
        fun findByAmountPaidLessThanEqual(amount: BigDecimal): List<PaymentLinesView>

        // Find by service name
        fun findByServiceName(serviceName: String): List<PaymentLinesView>

        // Find by service billing mode
        fun findByServiceBillingMode(billingMode: String): List<PaymentLinesView>

        // Find by user first name and last name
        fun findByUserFirstNameAndUserLastName(firstName: String, lastName: String): List<PaymentLinesView>

        @Query("""
            SELECT p FROM PaymentLinesView p
            WHERE (:paymentMethod IS NULL OR p.paymentMethod = :paymentMethod)
            AND (:tenantId IS NULL OR p.tenantId = :tenantId)
            AND (:username IS NULL OR p.userUsername = :username)
            AND (:startDate IS NULL OR p.paymentDate >= :startDate)
            AND (:endDate IS NULL OR p.paymentDate <= :endDate)
            AND (:amountPaidMin IS NULL OR p.amountPaid >= :amountPaidMin)
            AND (:amountPaidMax IS NULL OR p.amountPaid <= :amountPaidMax)
            AND (:serviceName IS NULL OR p.serviceName = :serviceName)
            AND (:billingMode IS NULL OR p.serviceBillingMode = :billingMode)
            AND (:userFirstName IS NULL OR p.userFirstName = :userFirstName)
            AND (:userLastName IS NULL OR p.userLastName = :userLastName)
        """)
        fun filterPaymentLines(
            @Param("paymentMethod") paymentMethod: String?,
            @Param("tenantId") tenantId: Long?,
            @Param("username") username: String?,
            @Param("startDate") startDate: LocalDate?,
            @Param("endDate") endDate: LocalDate?,
            @Param("amountPaidMin") amountPaidMin: BigDecimal?,
            @Param("amountPaidMax") amountPaidMax: BigDecimal?,
            @Param("serviceName") serviceName: String?,
            @Param("billingMode") billingMode: String?,
            @Param("userFirstName") userFirstName: String?,
            @Param("userLastName") userLastName: String?
        ): List<PaymentLinesView>
    }
