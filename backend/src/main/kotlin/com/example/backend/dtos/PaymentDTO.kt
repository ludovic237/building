package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDate

data class PaymentDTO(
  val id: Long?,
  val amount: BigDecimal?,
  val date: LocalDate?,
  val description: String?,
  val paymentMethod: String?,
  val status: String?
)
