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

  @Column(name = "subscription_id")
  var subscriptionId: Long? = null

  @Column(name = "subscription_tenant_id")
  var subscriptionTenantId: Long? = null

  @Column(name = "subscription_start_date")
  var subscriptionStartDate: LocalDate? = null

  @Column(name = "subscription_end_date")
  var subscriptionEndDate: LocalDate? = null

  @Column(name = "service_id")
  var serviceId: Long? = null

  @Lob
  @Column(name = "service_billing_mode")
  var serviceBillingMode: String? = null

  @Column(name = "service_code", length = 50)
  var serviceCode: String? = null

  @Column(name = "service_name", length = 100)
  var serviceName: String? = null

  @Lob
  @Column(name = "service_description")
  var serviceDescription: String? = null

  @Column(name = "tenant_id")
  var tenantId: Long? = null

  @Column(name = "user_id")
  var userId: Long? = null

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
