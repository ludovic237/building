package com.example.backend.repositories

import com.example.backend.models.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionOptionRepository : JpaRepository<SubscriptionOptions, Long> {

  // Find services by name
  fun findBySubscriptionService(subscriptionServices: SubscriptionServices): List<SubscriptionOptions>
  fun findBySubscriptionServiceIn(subscriptionServices: List<SubscriptionServices>): List<SubscriptionOptions>

  fun findByOption(option: ServiceOption): SubscriptionOptions

}
