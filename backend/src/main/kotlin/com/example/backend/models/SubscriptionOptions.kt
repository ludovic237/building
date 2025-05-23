package com.example.backend.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "subscription_options")
class SubscriptionOptions (
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "subscription_service_id", nullable = false)
  var subscriptionService: SubscriptionServices? = null,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "option_id", nullable = false)
  var option: ServiceOption? = null,

  @ColumnDefault("1")
  @Column(name = "quantity", nullable = false, columnDefinition = "INT DEFAULT 1")
  var quantity: Int = 1,

  @Column(name = "price", nullable = false)
  var price: BigDecimal=1.toBigDecimal(),

  @ManyToOne
  @JoinColumn(name = "payment_line_id")
  var paymentLine: PaymentLine? = null,

  @Column(name = "amount_due", nullable = false, columnDefinition = "DECIMAL(10, 2) DEFAULT 0.00")
  var amountDue: BigDecimal = BigDecimal.ZERO,

  @Column(name = "created_date", nullable = false, updatable = false)
  var createdDate: LocalDateTime = LocalDateTime.now(),

  @Column(name = "updated_date", nullable = false)
  var updatedDate: LocalDateTime = LocalDateTime.now()
) {

}
