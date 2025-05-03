package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentDTO(
  val id: Long?,
  val amountPaid: BigDecimal?,
  val paymentDate: LocalDateTime?,
  val serviceName: String?,
  val serviceDescription: String?,
  val paymentMethod: String?,
  val billingCycleStatus: String?
)
