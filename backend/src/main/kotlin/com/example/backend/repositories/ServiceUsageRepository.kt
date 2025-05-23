package com.example.backend.repositories

import com.example.backend.models.ServiceUsage
import com.example.backend.models.Subscription
import com.example.backend.models.SubscriptionOptions
import org.springframework.data.jpa.repository.JpaRepository

interface ServiceUsageRepository : JpaRepository<ServiceUsage, Long> {

  // Find services by name
  fun findByOption(option: SubscriptionOptions): ServiceUsage
  fun findBySubscription(subscription: Subscription): List<ServiceUsage>

}
