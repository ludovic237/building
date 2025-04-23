package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDate

data class BillingCycleDTO(
    val id: Long?,
    val subscriptionId: Long?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val amountDue: BigDecimal?,
    val amountPaid: BigDecimal?,
    val status: String?
)
