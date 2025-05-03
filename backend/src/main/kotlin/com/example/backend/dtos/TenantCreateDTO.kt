package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDateTime

data class TenantCreateDTO(
    val housingUnitId: Int,
    val moveInDate: LocalDateTime,
    val moveOutDate: LocalDateTime,
    val securityDeposit: BigDecimal,
    val userId: Int
)
