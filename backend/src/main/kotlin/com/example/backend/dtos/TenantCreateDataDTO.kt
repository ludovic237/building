package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDate

data class TenantCreateDataDTO(
  val housingUnitId: Long,
  val userId: Long,
  val serviceId: Long,
  val logementBasePrice: BigDecimal,
  var startDate: LocalDate,
  val endDate: LocalDate,
  val securityDeposit: BigDecimal,
  val paymentMode: String,
  val numberOfSubscription: Int
)
