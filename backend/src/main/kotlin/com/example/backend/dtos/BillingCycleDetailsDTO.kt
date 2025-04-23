package com.example.backend.dtos

import com.example.backend.models.Payment
import java.math.BigDecimal
import java.time.LocalDate

data class BillingCycleDetailsDTO(
  var billingCycleId: Long?,
  var startDate: LocalDate?,
  var endDate: LocalDate?,
  var amountDue: BigDecimal?,
  var amountPaid: BigDecimal?,
  var status: String?,
  var subscriptionName: String?,
  var tenantName: String?,
  var userName: String?,
  var payment: Payment?,
)
