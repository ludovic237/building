package com.example.backend.repositories

import com.example.backend.models.HoustingUnit
import com.example.backend.models.Tenant
import com.example.backend.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.Optional

interface TenantRepository : JpaRepository<Tenant, Long> {
  fun findByUser(user: User): Optional<Tenant>

  fun findByHousingUnit(houstingUnit: HoustingUnit): List<Tenant>

  @Query("SELECT t FROM Tenant t JOIN FETCH t.user WHERE t.id = :tenantId")
  fun findByIdWithUser(@Param("tenantId") tenantId: Long): Optional<Tenant>

  fun findByHousingUnitAndMoveInDateBetween(
    houstingUnit: HoustingUnit,
    startDate: LocalDateTime?,
    endDate: LocalDateTime?
  ): List<Tenant>

  fun findAllByMoveInDateBetween(
    startDate: LocalDateTime?,
    endDate: LocalDateTime?
  ): List<Tenant>

  fun findAllByMoveInDateBetweenOrMoveOutDateBetweenOrMoveInDateLessThanEqualAndMoveOutDateGreaterThanEqual(
    moveInDate: LocalDateTime?,
    moveInDate2: LocalDateTime?,
    moveOutDate: LocalDateTime?,
    moveOutDate2: LocalDateTime?,
    moveInDate3: LocalDateTime?,
    moveOutDate3: LocalDateTime?
  ): List<Tenant>

  @Query("SELECT COUNT(t) FROM Tenant t WHERE t.moveInDate BETWEEN :startDate AND :endDate AND t.moveOutDate IS NULL")
  fun countActiveTenantsBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): Int

  @Query("SELECT COUNT(t) FROM Tenant t WHERE t.moveOutDate BETWEEN :startDate AND :endDate")
  fun countInactiveTenantsBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): Int
}
