package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class TenantCreateDataDTO(
  val housingUnitId: Long,
  val userId: Long,
  val serviceId: Long,
  val logementBasePrice: BigDecimal,
  var startDate: LocalDateTime,
  val endDate: LocalDate,
  val securityDeposit: BigDecimal,
  val paymentMode: String,
  val numberOfSubscription: Int
)
