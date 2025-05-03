package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentLineDetailDTO(
  val id: Long?,
  val amountPaid: BigDecimal?,
  val paymentDate: LocalDateTime?,
  val serviceDescription: String?,
  val paymentMethod: String?,
  val subscriptionStatus: String?,
  val serviceBillingMode: String?,
  val billingCycleStatus: String?,
)
