package com.example.backend.repositories

import com.example.backend.models.Subscription
import com.example.backend.models.Tenant
import com.example.backend.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

interface SubscriptionRepository : JpaRepository<Subscription, Long> {

  // Find subscriptions by status
  fun findByStatus(status: String): List<Subscription>

  fun findByTenant(tenant: Tenant): List<Subscription>

  fun findByTenantAndStatus(tenant: Tenant,status: String): List<Subscription>

  fun findByStatusAndStartDateBetween(
    status: String,
    startDate: LocalDateTime?,
    endDate: LocalDateTime?
  ): List<Subscription>

  // Find subscriptions by start date
  fun findByStartDate(startDate: LocalDateTime): List<Subscription>

  // Find subscriptions by end date
  fun findByEndDate(endDate: LocalDateTime): List<Subscription>

  // Find subscriptions by end date
  fun findByEndDateBeforeAndStatusNotIn(date: LocalDateTime, statuses: List<String>): List<Subscription>

  // Find active subscriptions (end date is null or in the future)
  fun findByEndDateIsNullOrEndDateAfter(date: LocalDateTime): List<Subscription>

  @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = 'Active' AND s.startDate BETWEEN :startDate AND :endDate")
  fun countActiveSubscriptionsBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): Int

  @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = 'Expired' AND s.endDate BETWEEN :startDate AND :endDate")
  fun countExpiredSubscriptionsBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): Int

  @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = 'Canceled' AND s.endDate BETWEEN :startDate AND :endDate")
  fun countCanceledSubscriptionsBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): Int

  @Query("SELECT s FROM Subscription s WHERE s.status = 'Active' AND s.startDate BETWEEN :startDate AND :endDate")
  fun activeSubscriptionsBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): List<Subscription>

  @Query("SELECT s FROM Subscription s WHERE s.status = 'Expired' AND s.endDate BETWEEN :startDate AND :endDate")
  fun expiredSubscriptionsBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): List<Subscription>

  @Query("SELECT s FROM Subscription s WHERE s.status = 'Canceled' AND s.endDate BETWEEN :startDate AND :endDate")
  fun canceledSubscriptionsBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): List<Subscription>

  @Query(
    """
    SELECT s
    FROM Subscription s
    WHERE (:status IS NULL OR :status = '' OR s.status = :status)
      AND (s.startDate BETWEEN :startDate AND :endDate OR :startDate IS NULL OR :endDate IS NULL)
"""
  )
  fun subscriptionsByStatusAndDates(
    status: String?,
    startDate: LocalDateTime?,
    endDate: LocalDateTime?
  ): List<Subscription>

  fun findByEndDateBeforeAndStatusNot(dateFin: LocalDateTime, status: String): List<Subscription>

}
