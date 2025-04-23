package com.example.backend.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Mapping for DB view
 */
@Immutable
@Table(name = "payments_view")
@Entity
class PaymentsView protected constructor() {
  @Id
  @Column(name = "payment_id", nullable = false)
  var paymentId: Long? = null

  @Column(name = "payment_method", length = 50)
  var paymentMethod: String? = null

  @Column(name = "payment_total_amount", nullable = false, precision = 10, scale = 2)
  var paymentTotalAmount: BigDecimal? = null

  @Column(name = "payment_date")
  var paymentDate: LocalDate? = null

  @Column(name = "payment_line_id")
  var paymentLineId: Long? = null

  @Column(name = "amount_paid", precision = 10, scale = 2)
  var amountPaid: BigDecimal? = null

  @Column(name = "tenant_id")
  var tenantId: Long? = null

  @Column(name = "tenant_move_in_date")
  var tenantMoveInDate: LocalDate? = null

  @Column(name = "tenant_move_out_date")
  var tenantMoveOutDate: LocalDate? = null

  @Column(name = "tenant_security_deposit", precision = 10, scale = 2)
  var tenantSecurityDeposit: BigDecimal? = null

  @Column(name = "user_id")
  var userId: Long? = null

  @Column(name = "user_first_name", length = 100)
  var userFirstName: String? = null

  @Column(name = "user_last_name", length = 100)
  var userLastName: String? = null

  @Column(name = "user_username")
  var userUsername: String? = null

  @Column(name = "housing_unit_number", length = 20)
  var housingUnitNumber: String? = null

  @Column(name = "housing_unit_type", length = 50)
  var housingUnitType: String? = null

  @Column(name = "billing_cycle_id")
  var billingCycleId: Long? = null

  @Column(name = "billing_cycle_amount_due", precision = 10, scale = 2)
  var billingCycleAmountDue: BigDecimal? = null

  @Lob
  @Column(name = "billing_cycle_status")
  var billingCycleStatus: String? = null

  @Column(name = "subscription_id")
  var subscriptionId: Long? = null

  @Lob
  @Column(name = "subscription_status")
  var subscriptionStatus: String? = null

  @Column(name = "service_id")
  var serviceId: Long? = null

  @Column(name = "service_code", length = 50)
  var serviceCode: String? = null

  @Column(name = "service_name", length = 100)
  var serviceName: String? = null

  @Lob
  @Column(name = "service_description")
  var serviceDescription: String? = null

  @Lob
  @Column(name = "service_billing_mode")
  var serviceBillingMode: String? = null

  @Column(name = "service_is_active")
  var serviceIsActive: Boolean? = null
}
