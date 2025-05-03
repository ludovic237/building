package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDateTime

data class BillingCycleDTO(
    val id: Long?,
    val subscriptionId: Long?,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?,
    val amountDue: BigDecimal?,
    val amountPaid: BigDecimal?,
    val status: String?
)
