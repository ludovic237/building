package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDate

data class TenantDetailsDTO(
  val tenantId: Long,
  val tenantName: String,
  val tenantEmail: String,
  val housingUnitId: Long?,
  val housingUnitName: String?,
  val moveInDate: String?,
  val moveOutDate: String?,
  val securityDeposit: BigDecimal?,
  val status: String
)
