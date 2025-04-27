package com.example.backend.repositories

import com.example.backend.models.ServiceUsage
import com.example.backend.models.Services
import com.example.backend.models.Subscription
import com.example.backend.models.SubscriptionOption
import com.example.backend.services.SubscriptionService
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal
import java.util.Optional

interface ServiceUsageRepository : JpaRepository<ServiceUsage, Long> {

  // Find services by name
  fun findByOption(option: SubscriptionOption): ServiceUsage
  fun findBySubscription(subscription: Subscription): List<ServiceUsage>

}
