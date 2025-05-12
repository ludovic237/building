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
  @PutMapping ("/canceled/{id}")
  fun canceledSubscription(@PathVariable id: Long): ResponseEntity<Subscription> {
    return ResponseEntity.ok(subscriptionService.canceledSubscription(id))
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

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/add-subscription")
  fun saveSubscription(@RequestBody subscriptionRequest: Map<String, Any>): ResponseEntity<Subscription> {
      val tenantId = (subscriptionRequest["tenantId"] as Number).toLong()
      val serviceId = (subscriptionRequest["serviceId"] as Number).toLong()
      val dateDebut = subscriptionRequest["dateDebut"] as String
      val dateFin = subscriptionRequest["dateFin"] as String
      val status = subscriptionRequest["status"] as String
      val options = subscriptionRequest["options"] as List<Map<String, Any>>

      val subscription = subscriptionService.createSubscriptionWithDetails(
          tenantId, serviceId, dateDebut, dateFin, status, options
      )
      return ResponseEntity.ok(subscription)
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}/get-subscription")
  fun getSubscriptionFormattedData(@PathVariable id: Long): ResponseEntity<Map<String, Any?>> {
      val formattedData = subscriptionService.getSubscriptionWithDetails(id)
      return ResponseEntity.ok(formattedData)
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @PutMapping("/{id}/status")
  fun updateSubscriptionStatus(
      @PathVariable id: Long,
      @RequestBody statusRequest: Map<String, String>
  ): ResponseEntity<Subscription> {
      val newStatus = statusRequest["status"]
          ?: throw IllegalArgumentException("Status is required in the request body")
      val updatedSubscription = subscriptionService.updateSubscriptionStatus(id, newStatus)
      return ResponseEntity.ok(updatedSubscription)
  }
}
