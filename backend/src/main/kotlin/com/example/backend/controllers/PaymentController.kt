package com.example.backend.controllers

import com.example.backend.dtos.PaymentDTO
import com.example.backend.models.User
import com.example.backend.services.PaymentService
import com.example.backend.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/payments")
class PaymentController(
  private val userService: UserService,
  private val paymentService: PaymentService? = null,
) {


  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping
  fun getAllPayments(): ResponseEntity<List<Map<String, Any?>>> {
    val payments: List<Map<String, Any?>> = paymentService!!.getAllPayments()
    return ResponseEntity.ok(payments)
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/delete/{id}")
  fun deletePaymentWithValidationAndUpdate(@PathVariable id: String): ResponseEntity<Map<String, String>> {
    val payments = paymentService!!.deletePaymentWithValidationAndUpdate(id.toLong())
    return ResponseEntity.ok(payments)
  }


}
