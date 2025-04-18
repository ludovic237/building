package com.example.backend.controllers

    import com.example.backend.models.Tenant
    import com.example.backend.services.TenantService
    import org.springframework.http.ResponseEntity
    import org.springframework.web.bind.annotation.*

    @RestController
    @RequestMapping("/api/tenants")
    class TenantController(
        private val tenantService: TenantService
    ) {

        @GetMapping
        fun getAllTenants(): ResponseEntity<List<Tenant>> {
            return ResponseEntity.ok(tenantService.getAllTenants())
        }

        @GetMapping("/{id}")
        fun getTenantById(@PathVariable id: Long): ResponseEntity<Tenant> {
            return ResponseEntity.ok(tenantService.getTenantById(id).orElseThrow { IllegalArgumentException("Tenant not found") })
        }

        @PostMapping
        fun createTenant(@RequestBody tenant: Tenant): ResponseEntity<Tenant> {
            return ResponseEntity.ok(tenantService.createTenant(tenant))
        }

        @PutMapping("/{id}")
        fun updateTenant(@PathVariable id: Long, @RequestBody updatedTenant: Tenant): ResponseEntity<Tenant> {
            return ResponseEntity.ok(tenantService.updateTenant(id, updatedTenant))
        }

        @DeleteMapping("/{id}")
        fun deleteTenant(@PathVariable id: Long): ResponseEntity<Void> {
            tenantService.deleteTenant(id)
            return ResponseEntity.noContent().build()
        }
    }
