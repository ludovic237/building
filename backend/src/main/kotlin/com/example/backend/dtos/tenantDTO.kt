package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDate

data class TenantDTO(
    val id: Long?,
    val userId: Long?,
    val housingUnitId: Long?,
    val userName: String?,
    val userEmail: String?,
    val paymentStatus: String?,
    val housingUnitName: String?,
    val moveInDate: LocalDate?,
    val moveOutDate: LocalDate?,
    val securityDeposit: BigDecimal?,
    val houstinUnitPrice: BigDecimal?
)
