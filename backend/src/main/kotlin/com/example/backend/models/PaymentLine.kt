package com.example.backend.models

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "payment_lines")
class PaymentLine {
  @Id
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id")
  var payment: com.example.backend.models.Payment? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "billing_cycle_id")
  var billingCycle: BillingCycle? = null

  @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
  var amountPaid: BigDecimal? = null
}
