package com.example.backend.repositories

import com.example.backend.models.HoustingUnit
import com.example.backend.models.Tenant
import com.example.backend.models.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface TenantRepository : JpaRepository<Tenant, Long> {
    fun findByUser(user: User): Optional<Tenant>

    fun findByHousingUnit(houstingUnit: HoustingUnit): List<Tenant>

}
