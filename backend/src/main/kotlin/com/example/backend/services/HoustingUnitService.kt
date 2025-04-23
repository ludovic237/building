package com.example.backend.services

import com.example.backend.models.HoustingUnit
import com.example.backend.models.Tenant
import com.example.backend.repositories.*
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class HoustingUnitService(
  private val housingUnitRepository: HoustingUnitRepository,
  private val houstingUnitRepository: HoustingUnitRepository,
  private val subscriptionRepository: SubscriptionRepository,
  private val issueRepository: IssueRepository,
  private val invoiceRepository: InvoiceRepository,
  private val billingCycleRepository: BillingCycleRepository,
  private val tenantRepository: TenantRepository
) {

  fun getAllHoustingUnits(): List<HoustingUnit> {
    return housingUnitRepository.findAll()
  }

  fun getHoustingUnitById(id: Long): Optional<HoustingUnit> {
    return housingUnitRepository.findById(id)
  }

  fun createHoustingUnit(housingUnit: HoustingUnit): HoustingUnit {
    return housingUnitRepository.save(housingUnit)
  }

  fun updateHoustingUnit(id: Long, updatedHoustingUnit: HoustingUnit): HoustingUnit {
    val existingHoustingUnit = housingUnitRepository.findById(id)
      .orElseThrow { IllegalArgumentException("HoustingUnit with ID $id not found") }

    existingHoustingUnit.number = updatedHoustingUnit.number
    existingHoustingUnit.floor = updatedHoustingUnit.floor
    existingHoustingUnit.area = updatedHoustingUnit.area
    existingHoustingUnit.address = updatedHoustingUnit.address
    existingHoustingUnit.type = updatedHoustingUnit.type
    existingHoustingUnit.price = updatedHoustingUnit.price
    // Update other fields as necessary

    return housingUnitRepository.save(existingHoustingUnit)
  }

  fun deleteHoustingUnit(id: Long) {
    if (!housingUnitRepository.existsById(id)) {
      throw IllegalArgumentException("HoustingUnit with ID $id not found")
    }
    housingUnitRepository.deleteById(id)
  }

  fun getHousingUnitDetails(housingUnit: HoustingUnit): Map<String, Any?> {
    return mapOf(
      "housingUnitId" to housingUnit.id,
      "housingUnitNumber" to housingUnit.number,
      "address" to housingUnit.address,
      "type" to housingUnit.type,
      "floor" to housingUnit.floor,
      "area" to housingUnit.area,
      "price" to housingUnit.price
    )
  }

  fun getTenantInformation(tenant: Tenant): Map<String, Any?> {
    return mapOf(
      "fullName" to "${tenant.user?.firstName ?: "Unknown"} ${tenant.user?.lastName ?: "Unknown"}",
      "email" to tenant.user?.email,
      "phone" to tenant.user?.phone,
      "moveInDate" to tenant.moveInDate,
      "moveOutDate" to tenant.moveOutDate,
      "securityDeposit" to tenant.securityDeposit,
      "monthlyRent" to tenant.houstingPrice
    )
  }

  fun getFinancialInformation(tenant: Tenant): Map<String, Any?> {
    val subscriptions = subscriptionRepository.findByTenant(tenant).orEmpty()
    val billingCycles = subscriptions.flatMap { subscription ->
      billingCycleRepository.findBySubscription(subscription).orEmpty()
    }
    val paidBillingCycles = billingCycles.filter { it.status == "Paid" }
    val unpaidBillingCycles = billingCycles.filter { it.status != "Paid" }

    return mapOf(
      "billingCycles" to billingCycles.map { cycle ->
        mapOf(
          "id" to cycle.id,
          "amountDue" to cycle.amountDue,
          "periodStart" to cycle.periodStart,
          "periodEnd" to cycle.periodEnd,
          "status" to cycle.status
        )
      },
      "subscriptions" to subscriptions.map { subscription ->
        mapOf(
          "id" to subscription.id,
          "serviceName" to subscription.service?.name,
          "price" to subscription.price,
          "startDate" to subscription.startDate,
          "endDate" to subscription.endDate,
          "status" to subscription.status
        )
      },
      "totalPayments" to billingCycles.size,
      "completedPayments" to paidBillingCycles.size,
      "remainingPayments" to unpaidBillingCycles.size,
      "amountPaid" to paidBillingCycles.sumOf { it.amountDue ?: BigDecimal.ZERO },
      "outstandingDebt" to unpaidBillingCycles.sumOf { it.amountDue ?: BigDecimal.ZERO }
    )
  }

  fun getIssueTracking(tenant: Tenant): List<Map<String, Any?>> {
    val issues = issueRepository.findByTenant(tenant).orEmpty()
    return issues.map { issue ->
      mapOf(
        "id" to issue.id,
        "title" to issue.title,
        "description" to issue.description,
        "declarationDate" to issue.declarationDate,
        "status" to issue.status
      )
    }
  }

  fun getAdditionalInformation(tenant: Tenant): Map<String, Any?> {
    val invoices = invoiceRepository.findByTenant(tenant).orEmpty()
    return mapOf(
      "invoices" to invoices.map { invoice ->
        mapOf(
          "id" to invoice.id,
          "type" to invoice.type,
          "month" to invoice.month,
          "year" to invoice.year,
          "amount" to invoice.amount,
          "paymentDate" to invoice.paymentDate,
          "status" to invoice.status
        )
      },
      "userActivity" to mapOf(
        "registrationDate" to tenant.user?.registrationDate,
        "accountStatus" to if (tenant.user?.isActive == true) "Active" else "Inactive"
      )
    )
  }

  fun getHoustingUnitDetailsById(houstingUnitId: Long, tenantId: Long?): Map<String, Any?> {
    val houstingUnit = houstingUnitRepository.findById(houstingUnitId)
      .orElseThrow { IllegalArgumentException("HoustingUnit with ID $houstingUnitId not found") }

    return if (tenantId != null) {
      val tenant = tenantRepository.findById(tenantId)
        .orElseThrow { IllegalArgumentException("Tenant with ID $tenantId not found") }

      mapOf(
        "housingUnitDetails" to getHousingUnitDetails(houstingUnit),
        "tenantInformation" to getTenantInformation(tenant),
        "financialInformation" to getFinancialInformation(tenant),
        "issueTracking" to getIssueTracking(tenant),
        "additionalInformation" to getAdditionalInformation(tenant)
      )
    } else {
      val tenants = tenantRepository.findByHousingUnit(houstingUnit).orEmpty()

      mapOf(
        "housingUnitDetails" to getHousingUnitDetails(houstingUnit),
        "previousTenants" to tenants.map { tenant ->
          mapOf(
            "tenantInformation" to getTenantInformation(tenant),
            "financialInformation" to getFinancialInformation(tenant),
            "issueTracking" to getIssueTracking(tenant),
            "additionalInformation" to getAdditionalInformation(tenant)
          )
        }
      )
    }
  }

}
