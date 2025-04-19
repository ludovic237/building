package com.example.backend.repositories

          import com.example.backend.models.Subscription
          import com.example.backend.models.User
          import org.springframework.data.jpa.repository.JpaRepository
          import java.time.LocalDate

          interface SubscriptionRepository : JpaRepository<Subscription, Long> {

              // Find subscriptions by user
              fun findByUser(user: User): List<Subscription>

              // Find subscriptions by status
              fun findByStatus(status: String): List<Subscription>

              // Find subscriptions by start date
              fun findByStartDate(startDate: LocalDate): List<Subscription>

              // Find subscriptions by end date
              fun findByEndDate(endDate: LocalDate): List<Subscription>

              // Find active subscriptions (end date is null or in the future)
              fun findByEndDateIsNullOrEndDateAfter(date: LocalDate): List<Subscription>

          }
