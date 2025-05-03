package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentLineDTO(
    val id: Long?,
    val billingCycleId: Long?,
    val description: String?,
    val amountPaid: BigDecimal?,
    val paymentDate: LocalDateTime?
)
