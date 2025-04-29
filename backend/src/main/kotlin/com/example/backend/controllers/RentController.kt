package com.example.backend.controllers

import com.example.backend.models.Rent
import com.example.backend.services.RentService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rents")
class RentController(
  private val rentService: RentService
) {

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping
  fun getAllRents(): ResponseEntity<List<Rent>> {
    return ResponseEntity.ok(rentService.getAllRents())
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}")
  fun getRentById(@PathVariable id: Long): ResponseEntity<Rent> {
    return ResponseEntity.ok(rentService.getRentById(id).orElseThrow { IllegalArgumentException("Rent not found") })
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @PostMapping
  fun createRent(@RequestBody rent: Rent): ResponseEntity<Rent> {
    return ResponseEntity.ok(rentService.createRent(rent))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @PutMapping("/{id}")
  fun updateRent(@PathVariable id: Long, @RequestBody updatedRent: Rent): ResponseEntity<Rent> {
    return ResponseEntity.ok(rentService.updateRent(id, updatedRent))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{id}")
  fun deleteRent(@PathVariable id: Long): ResponseEntity<Void> {
    rentService.deleteRent(id)
    return ResponseEntity.noContent().build()
  }
}
