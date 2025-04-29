package com.example.backend.controllers

import com.example.backend.dtos.TenantDTO
import com.example.backend.dtos.TenantDetailsDTO
import com.example.backend.dtos.TenantCreateDTO
import com.example.backend.dtos.TenantCreateDataDTO
import com.example.backend.models.Tenant
import com.example.backend.services.TenantService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tenants")
class TenantController(
  private val tenantService: TenantService
) {

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping
  fun getAllTenants(): ResponseEntity<List<TenantDTO>> {
    return ResponseEntity.ok(tenantService.getAllTenants())
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/details")
  fun getListTenantDetail(): ResponseEntity<List<TenantDetailsDTO>> {
    return ResponseEntity.ok(tenantService.getListTenantDetails())
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}")
  fun getTenantById(@PathVariable id: Long): ResponseEntity<Tenant> {
    return ResponseEntity.ok(
      tenantService.getTenantById(id).orElseThrow { IllegalArgumentException("Tenant not found") })
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @PostMapping
  fun createTenant(@RequestBody tenant: TenantCreateDataDTO): ResponseEntity<Tenant> {
    return ResponseEntity.ok(tenantService.createTenantNew(tenant))
  }

  /* @CrossOrigin(origins = ["http://localhost:4200"])
 @PreAuthorize("isAuthenticated()")
   @PostMapping
   fun createTenant(@RequestBody tenant: TenantCreateDTO): ResponseEntity<Tenant> {
     return ResponseEntity.ok(tenantService.createTenant(tenant))
   }*/

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @PutMapping("/{id}")
  fun updateTenant(@PathVariable id: Long, @RequestBody updatedTenant: Tenant): ResponseEntity<Tenant> {
    return ResponseEntity.ok(tenantService.updateTenant(id, updatedTenant))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{id}")
  fun deleteTenant(@PathVariable id: Long): ResponseEntity<Void> {
    tenantService.deleteTenant(id)
    return ResponseEntity.noContent().build()
  }
}
