package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDate

data class SubscriptionDTO(
    val id: Long?,
    val tenantName: String?,
    val serviceName: String?,
    val moveInDate: LocalDate?,
    val moveOutDate: LocalDate?,
    val paymentStatus: String?,
)
