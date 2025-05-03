package com.example.backend.dtos

import com.example.backend.models.Payment
import java.math.BigDecimal
import java.time.LocalDateTime

data class BillingCycleDetailsDTO(
  var billingCycleId: Long?,
  var startDate: LocalDateTime?,
  var endDate: LocalDateTime?,
  var amountDue: BigDecimal?,
  var amountPaid: BigDecimal?,
  var status: String?,
  var subscriptionName: String?,
  var tenantName: String?,
  var userName: String?,
  var payment: Payment?,
)
