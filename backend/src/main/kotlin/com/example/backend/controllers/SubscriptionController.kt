package com.example.backend.controllers

import com.example.backend.dtos.SubscriptionDTO
import com.example.backend.dtos.SubscriptionDetailsDTO
import com.example.backend.models.Payment
import com.example.backend.models.Subscription
import com.example.backend.services.SubscriptionService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/subscriptions")
class SubscriptionController(
  private val subscriptionService: SubscriptionService
) {

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping
  fun getAllSubscriptions(): ResponseEntity<List<SubscriptionDTO>> {
    return ResponseEntity.ok(subscriptionService.getAllSubscriptions())
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}")
  fun getSubscriptionById(@PathVariable id: Long): ResponseEntity<Subscription> {
    return ResponseEntity.ok(
      subscriptionService.getSubscriptionById(id).orElseThrow { IllegalArgumentException("Subscription not found") })
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @PostMapping
  fun createSubscription(@RequestBody subscription: Subscription): ResponseEntity<Subscription> {
    return ResponseEntity.ok(subscriptionService.createSubscription(subscription))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @PutMapping("/{id}")
  fun updateSubscription(
    @PathVariable id: Long,
    @RequestBody updatedSubscription: Subscription
  ): ResponseEntity<Subscription> {
    return ResponseEntity.ok(subscriptionService.updateSubscription(id, updatedSubscription))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{id}")
  fun deleteSubscription(@PathVariable id: Long): ResponseEntity<Void> {
    subscriptionService.deleteSubscription(id)
    return ResponseEntity.noContent().build()
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}/details")
  fun getSubscriptionDetails(@PathVariable id: Long): ResponseEntity<SubscriptionDetailsDTO> {
    val subscriptionDetails = subscriptionService.getSubscriptionDetails(id)
    return ResponseEntity.ok(subscriptionDetails)
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/tenant/{tenantId}/info")
  fun getSubscriptionByTenant(@PathVariable tenantId: Long): ResponseEntity<List<Map<String, Any>>> {
    val subscriptionDetails = subscriptionService.getSubscriptionsByTenantId(tenantId)
    return ResponseEntity.ok(subscriptionDetails)
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/process-payment")
  fun processPayment(@RequestBody paymentRequest: Map<String, Any>): ResponseEntity<Map<String, Any?>> {
    val tenantId = (paymentRequest["tenantId"] as Number).toLong()
    val paymentMode = paymentRequest["paymentMode"] as String
    val subscriptionId = (paymentRequest["subscriptionId"] as Number).toLong()
    val paymentAmount = (paymentRequest["paymentAmount"] as Int).toBigDecimal()

    var data = subscriptionService.processPayment(tenantId, paymentMode, subscriptionId, paymentAmount)
//      return ResponseEntity.ok("Payment processed successfully")
    return ResponseEntity.ok(data)
  }


}
