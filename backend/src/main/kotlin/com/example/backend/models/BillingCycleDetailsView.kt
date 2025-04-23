package com.example.backend.models

import jakarta.persistence.*
import lombok.Data
import org.hibernate.annotations.Immutable
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Mapping for DB view
 */
@Data
@Immutable
@Table(name = "billing_cycle_details_view")
@Entity
class BillingCycleDetailsView protected constructor() {

  @Id
  @Column(name = "billing_cycle_id", nullable = false)
  var billingCycleId: Long? = null

  @Column(name = "start_date", nullable = false)
  var startDate: LocalDate? = null

  @Column(name = "end_date", nullable = false)
  var endDate: LocalDate? = null

  @Column(name = "amount_due", precision = 10, scale = 2)
  var amountDue: BigDecimal? = null

  @Lob
  @Column(name = "billing_cycle_status", nullable = false)
  var billingCycleStatus: String? = null

  @Column(name = "subscription_name")
  var subscriptionName: Long? = null

  @Lob
  @Column(name = "billing_mode")
  var billingMode: String? = null

  @Column(name = "code", length = 50)
  var code: String? = null

  @Column(name = "tenant_id")
  var tenantId: Long? = null

  @Column(name = "user_first_name", length = 100)
  var userFirstName: String? = null

  @Column(name = "user_last_name", length = 100)
  var userLastName: String? = null

  @Column(name = "user_username")
  var userUsername: String? = null

  @Column(name = "payment_id")
  var paymentId: Long? = null

  @Column(name = "payment_total_amount", precision = 10, scale = 2)
  var paymentTotalAmount: BigDecimal? = null

  @Column(name = "payment_date")
  var paymentDate: LocalDate? = null

  @Column(name = "payment_line_amount_paid", precision = 10, scale = 2)
  var paymentLineAmountPaid: BigDecimal? = null
}
