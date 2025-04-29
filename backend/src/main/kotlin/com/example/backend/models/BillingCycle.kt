package com.example.backend.models

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "billing_cycles")
class BillingCycle {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subscription_id")
  var subscription: com.example.backend.models.Subscription? = null

  @Column(name = "amount_due", precision = 10, scale = 2)
  var amountDue: BigDecimal? = null

  @Column(name = "period_start", nullable = false)
  var periodStart: LocalDate? = null

  @Column(name = "period_end", nullable = false)
  var periodEnd: LocalDate? = null

  @Lob
  @Column(name = "status", nullable = false)
  var status: String? = null

  override fun toString(): String {
    return "BillingCycle(id=$id, subscription=${subscription?.id}, amountDue=$amountDue, periodStart=$periodStart, periodEnd=$periodEnd, status='$status')"
  }
}
