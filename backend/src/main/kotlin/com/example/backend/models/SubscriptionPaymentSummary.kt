package com.example.backend.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable
import java.math.BigDecimal


@Table(name = "subscription_payment_summary")
@Entity
class SubscriptionPaymentSummary {

  @Id
  @Column(name = "subscription_id", nullable = false)
  var subscriptionId: Long? = null

  @Column(name = "subscription_service_id")
  var subscriptionServiceId: Long? = null

  @Column(name = "subscription_option_id")
  var subscriptionOptionId: Long? = null

  @Column(name = "subscription_total_price", precision = 10, scale = 2)
  var subscriptionTotalPrice: BigDecimal? = null

  @Column(name = "service_price", precision = 10, scale = 2)
  var servicePrice: BigDecimal? = null

  @Column(name = "option_price", precision = 38, scale = 2)
  var optionPrice: BigDecimal? = null

  @Column(name = "total_paid", nullable = false, precision = 32, scale = 2)
  var totalPaid: BigDecimal? = null

  @Column(name = "remaining_balance", precision = 33, scale = 2)
  var remainingBalance: BigDecimal? = null
}
