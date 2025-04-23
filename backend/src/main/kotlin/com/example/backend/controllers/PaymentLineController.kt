package com.example.backend.controllers

import com.example.backend.dtos.PaymentDTO
import com.example.backend.models.User
import com.example.backend.services.PaymentLineService
import com.example.backend.services.PaymentService
import com.example.backend.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/payment-line")
class PaymentLineController(
  private val userService: UserService,
  private val paymentLineService: PaymentLineService? = null
) {


  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping
  fun getAllPayments(): ResponseEntity<List<PaymentDTO>> {
    val payments: List<PaymentDTO> = paymentLineService!!.getAllPaymentLines()
    return ResponseEntity.ok(payments)
  }
}
