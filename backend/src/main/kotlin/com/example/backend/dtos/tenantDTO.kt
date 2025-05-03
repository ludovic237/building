package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDateTime

data class TenantDTO(
    val id: Long?,
    val userId: Long?,
    val housingUnitId: Long?,
    val userName: String?,
    val userEmail: String?,
    val paymentStatus: String?,
    val housingUnitName: String?,
    val moveInDate: LocalDateTime?,
    val moveOutDate: LocalDateTime?,
    val securityDeposit: BigDecimal?,
    val houstinUnitPrice: BigDecimal?
)
