package com.example.backend.controllers

import com.example.backend.dtos.TenantDTO
import com.example.backend.dtos.TenantDetailsDTO
import com.example.backend.dtos.tenantCreateDTO
import com.example.backend.models.Tenant
import com.example.backend.services.TenantService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tenants")
class TenantController(
  private val tenantService: TenantService
) {

  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping
  fun getAllTenants(): ResponseEntity<List<TenantDTO>> {
    return ResponseEntity.ok(tenantService.getAllTenants())
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping("/details")
  fun getListTenantDetail(): ResponseEntity<List<TenantDetailsDTO>> {
    return ResponseEntity.ok(tenantService.getListTenantDetails())
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping("/{id}")
  fun getTenantById(@PathVariable id: Long): ResponseEntity<Tenant> {
    return ResponseEntity.ok(
      tenantService.getTenantById(id).orElseThrow { IllegalArgumentException("Tenant not found") })
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PostMapping
  fun createTenant(@RequestBody tenant: tenantCreateDTO): ResponseEntity<Tenant> {
    return ResponseEntity.ok(tenantService.createTenant(tenant))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PutMapping("/{id}")
  fun updateTenant(@PathVariable id: Long, @RequestBody updatedTenant: Tenant): ResponseEntity<Tenant> {
    return ResponseEntity.ok(tenantService.updateTenant(id, updatedTenant))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @DeleteMapping("/{id}")
  fun deleteTenant(@PathVariable id: Long): ResponseEntity<Void> {
    tenantService.deleteTenant(id)
    return ResponseEntity.noContent().build()
  }
}
