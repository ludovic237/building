package com.example.backend.repositories

import com.example.backend.models.Subscription
import com.example.backend.models.Tenant
import com.example.backend.models.User
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface SubscriptionRepository : JpaRepository<Subscription, Long> {

  // Find subscriptions by status
  fun findByStatus(status: String): List<Subscription>

  fun findByTenant(tenant: Tenant): List<Subscription>

  // Find subscriptions by start date
  fun findByStartDate(startDate: LocalDateTime): List<Subscription>

  // Find subscriptions by end date
  fun findByEndDate(endDate: LocalDateTime): List<Subscription>

  // Find active subscriptions (end date is null or in the future)
  fun findByEndDateIsNullOrEndDateAfter(date: LocalDateTime): List<Subscription>

}
