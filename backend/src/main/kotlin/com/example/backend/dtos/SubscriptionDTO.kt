package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDateTime

data class SubscriptionDTO(
    val id: Long?,
    val tenantName: String?,
    val serviceName: String?,
    val moveInDate: LocalDateTime?,
    val moveOutDate: LocalDateTime?,
    val paymentStatus: String?,
)
