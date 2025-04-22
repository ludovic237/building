package com.example.backend.models

import jakarta.persistence.*
import lombok.Data
import java.math.BigDecimal

@Data
@Entity
@Table(name = "payment_lines")
class PaymentLine {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @Column(name = "payment_id", nullable = true)
  var paymentId: Long? = null

  @Column(name = "billing_cycle_id", nullable = true)
  var billingCycleId: Long? = null

  @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
  var amountPaid: BigDecimal? = null
}
