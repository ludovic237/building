package com.example.backend.controllers

import com.example.backend.dtos.BillingCycleDetailsDTO
import com.example.backend.dtos.PaymentDTO
import com.example.backend.models.User
import com.example.backend.services.BillingCycleService
import com.example.backend.services.PaymentService
import com.example.backend.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
  fun getBillingCycleDetails(): ResponseEntity<List<BillingCycleDetailsDTO>> {
    val billingCycleDetails = billingCycleService!!.getBillingCycleDetails()
    return ResponseEntity.ok(billingCycleDetails)
  }
}
