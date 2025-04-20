package com.example.backend.services

import com.example.backend.dtos.TenantDTO
import com.example.backend.dtos.TenantDetailsDTO
import com.example.backend.dtos.tenantCreateDTO
import com.example.backend.models.Rent
import com.example.backend.models.Tenant
import com.example.backend.repositories.HoustingUnitRepository
import com.example.backend.repositories.RentRepository
import com.example.backend.repositories.TenantRepository
import com.example.backend.repositories.UserRepository
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.util.*

@Service
class TenantService(
  private val tenantRepository: TenantRepository,
  private val rentRepository: RentRepository,
  private val userRepository: UserRepository,
  private val housingUnitRepository: HoustingUnitRepository,
) {

  fun getListTenantDetails(): List<TenantDetailsDTO> {
      return tenantRepository.findAll().map { tenant ->
          TenantDetailsDTO(
              tenantId = tenant.id!!,
              tenantName = "${tenant.user?.firstName} ${tenant.user?.lastName}",
              tenantEmail = tenant.user?.email ?: "N/A",
              housingUnitId = tenant.housingUnit?.id,
              housingUnitName = tenant.housingUnit?.number,
              moveInDate = tenant.moveInDate?.toString(),
              moveOutDate = tenant.moveOutDate?.toString(),
              securityDeposit = tenant.securityDeposit,
              status = tenant.status!!
          )
      }
  }

  fun getAllTenants(): List<TenantDTO> {
    return tenantRepository.findAll().map { tenant ->
      println("tenant")
      println(tenant)
      println(tenant.user)
      TenantDTO(
        id = tenant.id,
        userId = tenant.user?.id,
        housingUnitId = tenant.housingUnit?.id,
        userName = tenant.user?.firstName + " " + tenant.user?.lastName,
        userEmail = tenant.user?.email,
        housingUnitName = tenant.housingUnit?.number,
        moveInDate = tenant.moveInDate,
        moveOutDate = tenant.moveOutDate,
        securityDeposit = tenant.securityDeposit,
        status = tenant.status,
        houstinUnitPrice = tenant.houstingPrice,
      )
    }
  }

  fun getTenantById(id: Long): Optional<Tenant> {
    return tenantRepository.findById(id)
  }

  fun createTenant(tenantCreate: tenantCreateDTO): Tenant {
    // Create a new Tenant object
    val tenant = Tenant()

    // Map fields from tenantCreateDTO to Tenant
    tenant.moveInDate = tenantCreate.moveInDate
    tenant.moveOutDate = tenantCreate.moveOutDate
    tenant.securityDeposit = tenantCreate.securityDeposit
    tenant.status = tenantCreate.status

    // Fetch and set the associated User
    val user = userRepository.findById(tenantCreate.userId.toLong())
      .orElseThrow { IllegalArgumentException("User not found with ID: ${tenantCreate.userId}") }
    tenant.user = user

    // Fetch and set the associated Housing Unit
    val housingUnit = housingUnitRepository.findById(tenantCreate.housingUnitId.toLong())
      .orElseThrow { IllegalArgumentException("Housing Unit not found with ID: ${tenantCreate.housingUnitId}") }
    tenant.housingUnit = housingUnit

    // Save the Tenant
    val savedTenant = tenantRepository.save(tenant)

    // Create a new Rent object
    val rent = Rent()
    rent.tenant = savedTenant
    rent.amount = tenantCreate.securityDeposit // Assuming `price` is a field in HousingUnit
    rent.houstingPrice = housingUnit.price
    rent.month = tenantCreate.moveInDate?.monthValue
    rent.year = tenantCreate.moveInDate?.year

    // Check if the deposited amount equals the housing unit price
    rent.status = if (tenantCreate.securityDeposit == housingUnit.price) "Complete" else "Pending"
    rent.paymentDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() // No payment yet

    // Save the Rent
    rentRepository.save(rent)

    return savedTenant
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
