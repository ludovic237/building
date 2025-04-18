package com.example.backend.controllers

import com.example.backend.models.Services
import com.example.backend.services.ServiceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/services")
class ServicesController(
  private val serviceService: ServiceService
) {

  @GetMapping
  fun getAllServices(): ResponseEntity<List<Services>> {
    return ResponseEntity.ok(serviceService.getAllServices())
  }

  @GetMapping("/{id}")
  fun getServiceById(@PathVariable id: Long): ResponseEntity<Services> {
    return ResponseEntity.ok(
      serviceService.getServiceById(id).orElseThrow { IllegalArgumentException("Service not found") })
  }

  @PostMapping
  fun createService(@RequestBody services: Services): ResponseEntity<Services> {
    return ResponseEntity.ok(serviceService.createService(services))
  }

  @PutMapping("/{id}")
  fun updateService(@PathVariable id: Long, @RequestBody updatedServices: Services): ResponseEntity<Services> {
    return ResponseEntity.ok(serviceService.updateService(id, updatedServices))
  }

  @DeleteMapping("/{id}")
  fun deleteService(@PathVariable id: Long): ResponseEntity<Void> {
    serviceService.deleteService(id)
    return ResponseEntity.noContent().build()
  }
}
