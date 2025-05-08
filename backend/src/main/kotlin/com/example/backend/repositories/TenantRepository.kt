package com.example.backend.repositories

import com.example.backend.models.HoustingUnit
import com.example.backend.models.Tenant
import com.example.backend.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.Optional

interface TenantRepository : JpaRepository<Tenant, Long> {
    fun findByUser(user: User): Optional<Tenant>

    fun findByHousingUnit(houstingUnit: HoustingUnit): List<Tenant>

    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.moveInDate BETWEEN :startDate AND :endDate AND t.moveOutDate IS NULL")
    fun countActiveTenantsBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): Int

    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.moveOutDate BETWEEN :startDate AND :endDate")
    fun countInactiveTenantsBetweenDates(startDate: LocalDateTime?, endDate: LocalDateTime?): Int
}
