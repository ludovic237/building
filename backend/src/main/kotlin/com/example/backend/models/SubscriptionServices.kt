package com.example.backend.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "subscription_services")
class SubscriptionServices (
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "subscription_id", nullable = false)
  var subscription: Subscription? = null,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "service_id", nullable = false)
  var service: Services? = null,

  @Column(name = "quantity", nullable = false, columnDefinition = "INT DEFAULT 1")
  var quantity: Int = 1,

  @Column(name = "price", nullable = false, columnDefinition = "DECIMAL(10, 2) DEFAULT 0.00")
  var price: BigDecimal = BigDecimal.ZERO,

  @ManyToOne
  @JoinColumn(name = "billing_cycle_id")
  var billingCycle: BillingCycle? = null,

  @Column(name = "amount_due", nullable = false, columnDefinition = "DECIMAL(10, 2) DEFAULT 0.00")
  var amountDue: BigDecimal = BigDecimal.ZERO,

  @Column(name = "start_date", nullable = false)
  var startDate: LocalDateTime? = null,

  @Column(name = "end_date")
  var endDate: LocalDateTime? = null,

  @Column(name = "created_date", nullable = false, updatable = false)
  var createdDate: LocalDateTime = LocalDateTime.now(),

  @Column(name = "updated_date", nullable = false)
  var updatedDate: LocalDateTime = LocalDateTime.now()
)
