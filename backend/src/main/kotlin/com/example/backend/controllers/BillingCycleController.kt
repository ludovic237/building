package com.example.backend.controllers

import com.example.backend.dtos.BillingCycleDetailsDTO
import com.example.backend.dtos.PaymentDTO
import com.example.backend.models.BillingCycleDetailsView
import com.example.backend.models.User
import com.example.backend.services.BillingCycleService
import com.example.backend.services.PaymentService
import com.example.backend.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/admin/billing-cycle")
class BillingCycleController(
  private val userService: UserService,
  private val billingCycleService:BillingCycleService? = null
) {

  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping
  fun getAllPayments(): ResponseEntity<List<PaymentDTO>> {
    val payments: List<PaymentDTO> = billingCycleService!!.getAllBillingCycles()
    return ResponseEntity.ok(payments)
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping("/details")
  fun getBillingCycleDetails(): ResponseEntity<List<BillingCycleDetailsView>> {
    val billingCycleDetails = billingCycleService!!.getBillingCycleDetailsView()
    return ResponseEntity.ok(billingCycleDetails)
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping("/filter")
  fun filterBillingCycles(
    @RequestParam(required = false) status: String?,
    @RequestParam(required = false) tenantId: Long?,
    @RequestParam(required = false) username: String?,
    @RequestParam(required = false) startDate: LocalDate?,
    @RequestParam(required = false) endDate: LocalDate?,
    @RequestParam(required = false) userFirstName: String?,
    @RequestParam(required = false) userLastName: String?
  ): ResponseEntity<List<BillingCycleDetailsView>> {
      val filteredBillingCycles = billingCycleService!!.filterBillingCycles(
          status, tenantId, username, startDate, endDate, userFirstName, userLastName
      )
      return ResponseEntity.ok(filteredBillingCycles)
  }
}
