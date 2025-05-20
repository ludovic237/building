package com.example.backend.controllers

import com.example.backend.constants.StatusConstants
import com.example.backend.models.HoustingUnit
import com.example.backend.repositories.BillingCycleRepository
import com.example.backend.repositories.PaymentLineRepository
import com.example.backend.repositories.SubscriptionRepository
import com.example.backend.services.HoustingUnitService
import com.example.backend.services.SubscriptionService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/housing-units")
class HoustingUnitController(
  private val housingUnitService: HoustingUnitService,
  private val subscriptionService: SubscriptionService,
  private val subscriptionRepository: SubscriptionRepository,
  private val billingCycleRepository: BillingCycleRepository,
  private val paymentLineRepository: PaymentLineRepository,
) {

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping
  fun getAllHoustingUnits(): ResponseEntity<List<HoustingUnit>> {
    return ResponseEntity.ok(housingUnitService.getAllHoustingUnits())
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}")
  fun getHoustingUnitById(@PathVariable id: Long): ResponseEntity<HoustingUnit> {
    return ResponseEntity.ok(
      housingUnitService.getHoustingUnitById(id).orElseThrow { IllegalArgumentException("Housting Unit not found") })
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @PostMapping
  fun createHoustingUnit(@RequestBody housingUnit: HoustingUnit): ResponseEntity<HoustingUnit> {
    return ResponseEntity.ok(housingUnitService.createHoustingUnit(housingUnit))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @PutMapping("/{id}")
  fun updateHoustingUnit(
    @PathVariable id: Long,
    @RequestBody updatedHoustingUnit: HoustingUnit
  ): ResponseEntity<HoustingUnit> {
    return ResponseEntity.ok(housingUnitService.updateHoustingUnit(id, updatedHoustingUnit))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{id}")
  fun deleteHoustingUnit(@PathVariable id: Long): ResponseEntity<Void> {
    housingUnitService.deleteHoustingUnit(id)
    return ResponseEntity.noContent().build()
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/occupancy-details")
  fun getHousingUnitsWithOccupancyDetails(): ResponseEntity<List<Map<String, Any?>>> {
    val housingUnits = housingUnitService.getAllHoustingUnits()

    val result = housingUnits.map { unit ->
      val tenant = unit.tenant
      val isOccupied = tenant != null
      val leaseStatus: String?
      var remainingAmount: BigDecimal = 0.0.toBigDecimal()
      var outstandingDebt: BigDecimal = 0.0.toBigDecimal()
      var amountPaid: BigDecimal = 0.0.toBigDecimal()
      var leaseStartDate: LocalDateTime? = null
      var leaseEndDate: LocalDateTime? = null
      var billingMode: String? = null
      var totalPayments: Int? = null
      var completedPayments: Int? = null
      var remainingPayments: Int = 0
      var totalToPay: BigDecimal = 0.0.toBigDecimal()

      if (isOccupied) {
        val moveOutDate = tenant!!.moveOutDate
        val currentDate = LocalDateTime.now()

        leaseStatus = if (moveOutDate == null || moveOutDate.isAfter(currentDate)) {
          "Active"
        } else {
          "Expired"
        }

        val subscriptions = subscriptionRepository.findByTenant(tenant!!).orEmpty()
          .filter { subscription -> subscription.service?.name == "loyer" }

        var total = subscriptions.sumOf { it.totalPrice!!*it.subscriptNumber!!.toBigDecimal() }

        val billingCycles = subscriptions.flatMap { subscription ->
          billingCycleRepository.findBySubscription(subscription).orEmpty()
        }

        val unpaidBillingCycles = billingCycles.filter { billingCycle -> billingCycle.status != StatusConstants.BILLING_CYCLE_STATUS_PAID }
        val paidBillingCyclesCompleted = billingCycles.filter { billingCycle ->
          billingCycle.status == StatusConstants.BILLING_CYCLE_STATUS_PAID
        }
        val paidBillingCyclesPartial = billingCycles.filter { billingCycle ->
          billingCycle.status == StatusConstants.BILLING_CYCLE_STATUS_PARTIAL_PAID
        }

        totalPayments = billingCycles.size
        completedPayments = paidBillingCyclesCompleted.size
        remainingPayments = totalPayments - completedPayments

        val paidBillingCyclesCompletedIds = paidBillingCyclesCompleted.map { it.id }
        val paidBillingCyclesPartialIds = paidBillingCyclesPartial.map { it.id }

        amountPaid =
          paymentLineRepository.findAllByBillingCycleIdIn(paidBillingCyclesCompletedIds + paidBillingCyclesPartialIds)
            .sumOf { it.amountPaid ?: BigDecimal.ZERO }
//        amountPaid = paidBillingCycles.sumOf { it.amountDue ?: BigDecimal.ZERO }
        leaseStartDate = tenant.moveInDate
        leaseEndDate = tenant.moveOutDate
        billingMode = subscriptions.firstOrNull()?.service?.billingMode

        if (leaseStatus == "Active") {
          remainingAmount = total-amountPaid
          outstandingDebt = 0.0.toBigDecimal()
        } else {
          remainingAmount = 0.0.toBigDecimal()
          outstandingDebt = unpaidBillingCycles.sumOf { it.amountDue ?: BigDecimal.ZERO }
        }
        totalToPay = remainingAmount + amountPaid
      } else {
        leaseStatus = "Unoccupied"
      }

      mapOf(
        "housingUnitId" to (unit.id ?: 0L),
        "tenantId" to (tenant?.id ?: null),
        "tenantName" to (tenant?.user?.let { "${it.firstName} ${it.lastName}" } ?: "No tenant"),
        "housingUnitNumber" to (unit.number ?: "Unknown"),
        "housingUnitType" to (unit.type ?: "Unknown"),
        "isOccupied" to isOccupied,
        "leaseStatus" to leaseStatus,
        "remainingAmount" to remainingAmount,
        "outstandingDebt" to outstandingDebt,
        "amountPaid" to amountPaid,
        "leaseStartDate" to leaseStartDate,
        "leaseEndDate" to leaseEndDate,
        "billingMode" to billingMode,
        "totalPayments" to (totalPayments ?: 0),
        "completedPayments" to (completedPayments ?: 0),
        "remainingPayments" to remainingPayments,
        "totalToPay" to totalToPay
      )
    }

    return ResponseEntity.ok(result)
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}/tenant-details")
  fun getTenantDetailsByHousingUnit(@PathVariable id: Long): ResponseEntity<Map<String, Any>> {
    val housingUnit = housingUnitService.getHoustingUnitById(id)
      .orElseThrow { IllegalArgumentException("Housing Unit not found") }

    val tenant = housingUnit.tenant
      ?: return ResponseEntity.badRequest().body(mapOf("error" to "No tenant associated with this housing unit"))

    // Retrieve subscriptions for the tenant
    val subscriptions = subscriptionRepository.findByTenant(tenant).orEmpty()

    // Retrieve billing cycles for the tenant's subscriptions
    val billingCycles = subscriptions.flatMap { subscription ->
      billingCycleRepository.findBySubscription(subscription).orEmpty()
    }

    // Map subscription details
    val subscriptionDetails = subscriptions.map { subscription ->
      mapOf(
        "subscriptionId" to subscription.id,
        "serviceName" to subscription.service?.name,
        "serviceDescription" to subscription.service?.description,
        "billingMode" to subscription.service?.billingMode,
        "price" to subscription.totalPrice,
        "startDate" to subscription.startDate,
        "endDate" to subscription.endDate,
        "status" to subscription.status
      )
    }

    // Map billing cycle details
    val billingCycleDetails = billingCycles.map { cycle ->
      mapOf(
        "billingCycleId" to cycle.id,
        "amountDue" to cycle.amountDue,
        "periodStart" to cycle.periodStart,
        "periodEnd" to cycle.periodEnd,
        "status" to cycle.status
      )
    }

    // Map user details
    val user = tenant.user
    val userDetails = mapOf(
      "userId" to (user?.id ?: 0L),
      "firstName" to (user?.firstName ?: "Unknown"),
      "lastName" to (user?.lastName ?: "Unknown"),
      "email" to (user?.email ?: "Unknown"),
      "username" to (user?.username ?: "Unknown"),
      "phone" to (user?.phone ?: "Unknown")
    )

    return ResponseEntity.ok(
      mapOf(
        "tenantId" to (tenant.id ?: 0L),
        "tenantName" to "${tenant.user?.firstName ?: "Unknown"} ${tenant.user?.lastName ?: "Unknown"}",
        "userDetails" to userDetails,
        "billingCycles" to billingCycleDetails,
        "subscriptions" to subscriptionDetails
      )
    )
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{houstingUnitId}/details")
  fun getHoustingUnitDetails(
    @PathVariable houstingUnitId: Long,
    @RequestParam(required = false) tenantId: String?
  ): ResponseEntity<Map<String, Any?>> {
    val result = housingUnitService.getHoustingUnitDetailsById(houstingUnitId, tenantId)
    return ResponseEntity.ok(result)
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/unoccupied")
  fun getUnoccupiedHoustingUnits(): ResponseEntity<List<HoustingUnit?>> {
    val unoccupiedUnits = housingUnitService.getUnoccupiedHoustingUnits()
    return ResponseEntity.ok(unoccupiedUnits)
  }

}
