package com.example.backend.controllers

import com.example.backend.models.Subscription
import com.example.backend.services.SubscriptionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/subscriptions")
class SubscriptionController(
  private val subscriptionService: SubscriptionService
) {

  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping
  fun getAllSubscriptions(): ResponseEntity<List<Subscription>> {
    return ResponseEntity.ok(subscriptionService.getAllSubscriptions())
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping("/{id}")
  fun getSubscriptionById(@PathVariable id: Long): ResponseEntity<Subscription> {
    return ResponseEntity.ok(
      subscriptionService.getSubscriptionById(id).orElseThrow { IllegalArgumentException("Subscription not found") })
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PostMapping
  fun createSubscription(@RequestBody subscription: Subscription): ResponseEntity<Subscription> {
    return ResponseEntity.ok(subscriptionService.createSubscription(subscription))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PutMapping("/{id}")
  fun updateSubscription(
    @PathVariable id: Long,
    @RequestBody updatedSubscription: Subscription
  ): ResponseEntity<Subscription> {
    return ResponseEntity.ok(subscriptionService.updateSubscription(id, updatedSubscription))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @DeleteMapping("/{id}")
  fun deleteSubscription(@PathVariable id: Long): ResponseEntity<Void> {
    subscriptionService.deleteSubscription(id)
    return ResponseEntity.noContent().build()
  }
}
