package com.example.backend.services

import com.example.backend.constants.StatusConstants
import com.example.backend.constants.StatusConstants.SUBSCRIPTION_STATUS_ACTIVE
import com.example.backend.constants.StatusConstants.SUBSCRIPTION_STATUS_CANCELED
import com.example.backend.constants.StatusConstants.SUBSCRIPTION_STATUS_EXPIRED
import com.example.backend.dtos.DashboardDTO
import com.example.backend.models.HoustingUnit
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
  private val tenantRepository: TenantRepository,
  private val paymentsViewRepository: PaymentsViewRepository
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
    // Subscriptions
    val subscriptions = subscriptionRepository.subscriptionsByStatusAndDates("", startDate, endDate)
    val activeSubscriptions =
      subscriptionRepository.subscriptionsByStatusAndDates(SUBSCRIPTION_STATUS_ACTIVE, startDate, endDate)
    val expiredSubscriptions =
      subscriptionRepository.subscriptionsByStatusAndDates(SUBSCRIPTION_STATUS_EXPIRED, startDate, endDate)
    val canceledSubscriptions =
      subscriptionRepository.subscriptionsByStatusAndDates(SUBSCRIPTION_STATUS_CANCELED, startDate, endDate)

    val subscriptionDetails = mapOf(
      "totalSubscriptions" to subscriptions.size,
      "totalActiveSubscriptions" to activeSubscriptions.size,
      "totalExpiredSubscriptions" to expiredSubscriptions.size,
      "totalCanceledSubscriptions" to canceledSubscriptions.size,
      "totalAmountSubscriptions" to subscriptions.sumOf {
        (it.totalPrice ?: BigDecimal.ZERO)
      },
      "totalAmountActiveSubscriptions" to activeSubscriptions.sumOf {
        (it.totalPrice ?: BigDecimal.ZERO)
      },
      "totalAmountExpiredSubscriptions" to expiredSubscriptions.sumOf {
        (it.totalPrice ?: BigDecimal.ZERO)
      },
      "totalAmountCanceledSubscriptions" to canceledSubscriptions.sumOf {
        (it.totalPrice ?: BigDecimal.ZERO)
      },
    )

    // Payments
    val payments = paymentsViewRepository.findAllByPaymentDateBetween(startDate, endDate)

    val totalPayments = payments.sumOf { it.amountPaid ?: BigDecimal.ZERO }
    val totalPendingPayments = subscriptions.sumOf {
      (it.totalPrice ?: BigDecimal.ZERO)
    } - totalPayments

    val totalPartialPaidPayments =
      payments.filter { it.billingCycleStatus == StatusConstants.BILLING_CYCLE_STATUS_PARTIAL_PAID }
        .sumOf { it.amountPaid ?: BigDecimal.ZERO }
    val totalPaidPayments = payments.filter { it.billingCycleStatus == StatusConstants.BILLING_CYCLE_STATUS_PAID }
      .sumOf { it.amountPaid ?: BigDecimal.ZERO }

    val overduePayments = totalPayments - totalPaidPayments - totalPartialPaidPayments

    val pendingPayments = totalPayments - totalPaidPayments - totalPartialPaidPayments
    val paymentDetails = mapOf(
      "overduePayments" to overduePayments,
      "pendingPayments" to pendingPayments,
      "totalPayments" to totalPayments,
      "totalPendingPayments" to totalPendingPayments,
      "totalPartialPaidPayments" to totalPartialPaidPayments,
      "totalPaidPayments" to totalPaidPayments,
    )
//    val averagePaymentAmount = paymentRepository.findAveragePaymentAmountBetweenDates(startDate, endDate) ?: BigDecimal.ZERO

    // Billing Cycles
    val paidBillingCycles = billingCycleRepository.countPaidBillingCyclesBetweenDates(startDate, endDate)
    val partiallyPaidBillingCycles =
      billingCycleRepository.countPartiallyPaidBillingCyclesBetweenDates(startDate, endDate)
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
    val tenants =
      tenantRepository.findAllByMoveInDateBetweenOrMoveOutDateBetweenOrMoveInDateLessThanEqualAndMoveOutDateGreaterThanEqual(
        startDate,
        endDate,
        startDate,
        endDate,
        startDate,
        endDate,
      ) ?: emptyList()
    val (activeTenantsList, inactiveTenantsList) = tenants.partition { it.housingUnit != null }
    val activeTenants = activeTenantsList.size
    val inactiveTenants = inactiveTenantsList.size
    val tenantPaymentCompliance = if (activeTenants > 0) {
      ((activeTenants - overdueBillingCycles).toDouble() / activeTenants) * 100
    } else {
      0.0
    }

    // Financial Summary
    val totalRevenue = totalPayments
//    val subscriptionRevenue = subscriptionRepository.findTotalRevenueFromActiveSubscriptions(startDate, endDate) ?: BigDecimal.ZERO

    val housingUnitStatistics = getHousingUnitStatistics()

    return mapOf(
      "housingUnitStatistics" to housingUnitStatistics,
      "payments" to paymentDetails,
      "subscriptions" to subscriptionDetails,
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
      it.tenant != null && (it.tenant!!.moveOutDate == null || it.tenant!!.moveOutDate!!.isAfter(LocalDateTime.now()))
    }

    var totalAmountDue = BigDecimal.ZERO
    var totalAmountPaid = BigDecimal.ZERO
    var totalAmountToPay = BigDecimal.ZERO
    val housingUnitsWithDebtList = mutableListOf<HoustingUnit>()

    val housingUnitsWithDebt = housingUnitRepository.findAll().count { housingUnit ->
      val tenant = housingUnit.tenant
      tenant != null && tenantRepository.findById(tenant.id!!).get().let { t ->
        val subscriptions = subscriptionRepository.findByTenant(t)
        val totalDue = subscriptions.sumOf {
          (it.totalPrice ?: BigDecimal.ZERO)
        }
        val totalPaid = paymentRepository.findByTenant(t).sumOf { it.totalAmount ?: BigDecimal.ZERO }
        totalAmountToPay += totalDue - totalPaid
        totalAmountPaid += totalPaid
        val hasDebt = totalPaid < totalDue
        if (hasDebt) {
          totalAmountDue += totalDue - totalPaid
          housingUnitsWithDebtList.add(housingUnit) // Assuming housingUnit has an `id` field
        }
        hasDebt
      }
    }

    return mapOf(
      "totalHousingUnits" to totalHousingUnits,
      "occupiedHousingUnits" to occupiedHousingUnits,
      "activeLeases" to activeLeases,
      "housingUnitsWithDebt" to housingUnitsWithDebt,
      "totalAmountDue" to totalAmountDue,
      "totalAmountPaid" to totalAmountPaid,
      "totalAmountToPay" to totalAmountToPay,
      "housingUnitsWithDebtList" to housingUnitsWithDebtList
    )
  }

}
