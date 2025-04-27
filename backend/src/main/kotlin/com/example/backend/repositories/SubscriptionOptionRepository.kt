package com.example.backend.repositories

import com.example.backend.models.*
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal
import java.util.Optional

interface SubscriptionOptionRepository : JpaRepository<SubscriptionOption, Long> {

  // Find services by name
  fun findBySubscription(subscription: Subscription): List<SubscriptionOption>

  fun findByOption(option: ServiceOption): SubscriptionOption

}
