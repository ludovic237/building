package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDate

data class TenantCreateDTO(
    val housingUnitId: Int,
    val moveInDate: LocalDate,
    val moveOutDate: LocalDate,
    val securityDeposit: BigDecimal,
    val userId: Int
)
