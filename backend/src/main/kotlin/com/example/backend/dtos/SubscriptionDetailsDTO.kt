package com.example.backend.dtos

import com.example.backend.models.BillingCycle
import com.example.backend.models.Payment
import com.example.backend.models.PaymentLine
import com.example.backend.models.Tenant

data class SubscriptionDetailsDTO(
  val tenant: Tenant,
  val billingCycles: List<BillingCycle>,
  val payments: List<Payment>,
  val paymentLines: List<PaymentLine>
)
