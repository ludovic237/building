package com.example.backend.services

import com.example.backend.models.Tenant
import com.example.backend.repositories.TenantRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class TenantService(
    private val tenantRepository: TenantRepository
) {

    fun getAllTenants(): List<Tenant> {
        return tenantRepository.findAll()
    }

    fun getTenantById(id: Long): Optional<Tenant> {
        return tenantRepository.findById(id)
    }

    fun createTenant(tenant: Tenant): Tenant {
        return tenantRepository.save(tenant)
    }

    fun updateTenant(id: Long, updatedTenant: Tenant): Tenant {
        val existingTenant = tenantRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Tenant with ID $id not found") }

      existingTenant.user!!.firstName = updatedTenant.user!!.lastName
        existingTenant.user!!.lastName = updatedTenant.user!!.firstName
      existingTenant.user!!.email = updatedTenant.user!!.email
        // Update other fields as necessary

        return tenantRepository.save(existingTenant)
    }

    fun deleteTenant(id: Long) {
        if (!tenantRepository.existsById(id)) {
            throw IllegalArgumentException("Tenant with ID $id not found")
        }
        tenantRepository.deleteById(id)
    }
}
