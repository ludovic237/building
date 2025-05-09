package com.example.backend.services

import com.example.backend.dtos.DashboardDTO
import com.example.backend.models.User
import com.example.backend.repositories.*
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class DashboardService(
  private val userRepository: UserRepository,
  private val housingUnitRepository: HoustingUnitRepository,
  private val paymentRepository: PaymentRepository,
  private val subscriptionRepository: SubscriptionRepository,
  private val billingCycleRepository: BillingCycleRepository,
  private val issueRepository: IssueRepository,
  private val tenantRepository: TenantRepository
) {

  fun getDashboardData(): DashboardDTO {
    val totalActiveSubscriptions = 10 // Example value
    val totalAmountDue = BigDecimal("1000.00") // Example value
    val totalPaymentsPending = 5 // Example value
    val tenantsWithOverduePayments = listOf("Tenant A", "Tenant B") // Example value
    val monthlyRevenue = mapOf("January" to BigDecimal("500.00"), "February" to BigDecimal("600.00")) // Example value

    return DashboardDTO(
      totalActiveSubscriptions = totalActiveSubscriptions,
      totalAmountDue = totalAmountDue,
      totalPaymentsPending = totalPaymentsPending,
      tenantsWithOverduePayments = tenantsWithOverduePayments,
      monthlyRevenue = monthlyRevenue
    )
  }

  fun getAllUsers(): List<User> {
    return userRepository.findAll()
  }

  fun getUserById(id: Long): Optional<User> {
    return userRepository.findById(id)
  }

  fun createUser(user: User): User {
    return userRepository.save(user)
  }

  fun updateUser(id: Long, updatedUser: User): User {
    val existingUser = userRepository.findById(id)
      .orElseThrow { IllegalArgumentException("User with ID $id not found") }

    updatedUser.joinedDate = Date().toInstant()

    return userRepository.save(updatedUser)
  }

  fun deleteUser(id: Long) {
    if (!userRepository.existsById(id)) {
      throw IllegalArgumentException("User with ID $id not found")
    }
    userRepository.deleteById(id)
  }


fun getAdminDashboardData(startDate: LocalDateTime?, endDate: LocalDateTime?): Map<String, Any> {
    val subscriptions = subscriptionRepository.subscriptionsByStatusAndDates("", startDate, endDate)

    // Subscriptions
    val activeSubscriptions = subscriptionRepository.countActiveSubscriptionsBetweenDates(startDate, endDate)
    val expiredSubscriptions = subscriptionRepository.countExpiredSubscriptionsBetweenDates(startDate, endDate)
    val canceledSubscriptions = subscriptionRepository.countCanceledSubscriptionsBetweenDates(startDate, endDate)

    // Payments
    val totalPayments = paymentRepository.findTotalPaymentsBetweenDates(startDate, endDate) ?: BigDecimal.ZERO
    val overduePayments = billingCycleRepository.findTotalOverduePaymentsBetweenDates(startDate, endDate) ?: BigDecimal.ZERO
    val pendingPayments = billingCycleRepository.findTotalPendingPaymentsBetweenDates(startDate, endDate) ?: BigDecimal.ZERO
    val averagePaymentAmount = paymentRepository.findAveragePaymentAmountBetweenDates(startDate, endDate) ?: BigDecimal.ZERO

    // Billing Cycles
    val paidBillingCycles = billingCycleRepository.countPaidBillingCyclesBetweenDates(startDate, endDate)
    val partiallyPaidBillingCycles = billingCycleRepository.countPartiallyPaidBillingCyclesBetweenDates(startDate, endDate)
    val overdueBillingCycles = billingCycleRepository.countOverdueBillingCyclesBetweenDates(startDate, endDate)

    // Issues
    val openIssues = issueRepository.countOpenIssuesBetweenDates(startDate, endDate)
    val resolvedIssues = issueRepository.countResolvedIssuesBetweenDates(startDate, endDate)
    val pendingIssues = issueRepository.countPendingIssuesBetweenDates(startDate, endDate)
    val issueResolutionRate = if (openIssues + resolvedIssues > 0) {
        (resolvedIssues.toDouble() / (openIssues + resolvedIssues)) * 100
    } else {
        0.0
    }

    // Tenants
    val activeTenants = tenantRepository.countActiveTenantsBetweenDates(startDate, endDate)
    val inactiveTenants = tenantRepository.countInactiveTenantsBetweenDates(startDate, endDate)
    val tenantPaymentCompliance = if (activeTenants > 0) {
        ((activeTenants - overdueBillingCycles).toDouble() / activeTenants) * 100
    } else {
        0.0
    }

    // Financial Summary
    val totalRevenue = totalPayments
    val subscriptionRevenue = subscriptionRepository.findTotalRevenueFromActiveSubscriptions(startDate, endDate) ?: BigDecimal.ZERO

    val housingUnitStatistics = getHousingUnitStatistics()

    return mapOf(
        "housingUnitStatistics" to housingUnitStatistics,
        "payments" to mapOf(
            "totalPayments" to totalPayments,
            "overduePayments" to overduePayments,
            "pendingPayments" to pendingPayments,
            "averagePaymentAmount" to averagePaymentAmount
        ),
        "subscriptions" to mapOf(
            "activeSubscriptions" to activeSubscriptions,
            "expiredSubscriptions" to expiredSubscriptions,
            "canceledSubscriptions" to canceledSubscriptions,
            "subscriptionRevenue" to subscriptionRevenue
        ),
        "billingCycles" to mapOf(
            "paidBillingCycles" to paidBillingCycles,
            "partiallyPaidBillingCycles" to partiallyPaidBillingCycles,
            "overdueBillingCycles" to overdueBillingCycles
        ),
        "issues" to mapOf(
            "openIssues" to openIssues,
            "resolvedIssues" to resolvedIssues,
            "pendingIssues" to pendingIssues,
            "issueResolutionRate" to issueResolutionRate
        ),
        "tenants" to mapOf(
            "activeTenants" to activeTenants,
            "inactiveTenants" to inactiveTenants,
            "tenantPaymentCompliance" to tenantPaymentCompliance
        ),
        "financialSummary" to mapOf(
            "totalRevenue" to totalRevenue
        )
    )
}
  fun getHousingUnitStatistics(): Map<String, Any> {
      val totalHousingUnits = housingUnitRepository.count()
      val occupiedHousingUnits = housingUnitRepository.findAll().count { it.tenant != null }
      val activeLeases = housingUnitRepository.findAll().count {
          it.tenant?.moveOutDate == null || it.tenant?.moveOutDate?.isAfter(LocalDateTime.now()) == true
      }
      val housingUnitsWithDebt = housingUnitRepository.findAll().count { housingUnit ->
          val tenant = housingUnit.tenant
          tenant != null && tenantRepository.findById(tenant.id!!).get().let { t ->
              val subscriptions = subscriptionRepository.findByTenant(t)
              val totalDue = subscriptions.sumOf { sub ->
                  val billingCycles = billingCycleRepository.findBySubscription(sub)
                  billingCycles.sumOf { it.amountDue ?: BigDecimal.ZERO }
              }
              val totalPaid = paymentRepository.findByTenant(t).sumOf { it.totalAmount ?: BigDecimal.ZERO }
              totalPaid < totalDue
          }
      }

      return mapOf(
          "totalHousingUnits" to totalHousingUnits,
          "occupiedHousingUnits" to occupiedHousingUnits,
          "activeLeases" to activeLeases,
          "housingUnitsWithDebt" to housingUnitsWithDebt
      )
  }

}
