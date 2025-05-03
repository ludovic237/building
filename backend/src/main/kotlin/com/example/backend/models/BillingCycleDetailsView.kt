package com.example.backend.models

import jakarta.persistence.*
import org.hibernate.annotations.Immutable
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Mapping for DB view
 */
@Immutable
@Table(name = "billing_cycle_details_view")
@Entity
class BillingCycleDetailsView protected constructor() {
  @Id
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @Column(name = "billing_cycle_id", nullable = false)
  var billingCycleId: Long? = null

  @Column(name = "start_date", nullable = false)
  var startDate: LocalDateTime? = null

  @Column(name = "end_date", nullable = false)
  var endDate: LocalDateTime? = null

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
  var subscriptionStartDate: LocalDateTime? = null

  @Column(name = "subscription_end_date")
  var subscriptionEndDate: LocalDateTime? = null

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
  var paymentDate: LocalDateTime? = null

  @Column(name = "payment_line_amount_paid", precision = 10, scale = 2)
  var paymentLineAmountPaid: BigDecimal? = null

  override fun toString(): String {
    return "BillingCycleDetailsView(id=$id, billingCycleId=$billingCycleId, startDate=$startDate, endDate=$endDate, amountDue=$amountDue, billingCycleStatus='$billingCycleStatus', subscriptionId=$subscriptionId, subscriptionTenantId=$subscriptionTenantId, subscriptionStartDate=$subscriptionStartDate, subscriptionEndDate=$subscriptionEndDate, serviceId=$serviceId, serviceBillingMode='$serviceBillingMode', serviceCode='$serviceCode', serviceName='$serviceName', serviceDescription='$serviceDescription', tenantId=$tenantId, userId=$userId, userFirstName='$userFirstName', userLastName='$userLastName', userUsername='$userUsername', paymentId=$paymentId, paymentTotalAmount=$paymentTotalAmount, paymentDate=$paymentDate, paymentLineAmountPaid=$paymentLineAmountPaid)"
  }
}
