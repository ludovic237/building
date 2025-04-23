package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDate

data class PaymentLineDTO(
    val id: Long?,
    val billingCycleId: Long?,
    val description: String?,
    val amountPaid: BigDecimal?,
    val paymentDate: LocalDate?
)
