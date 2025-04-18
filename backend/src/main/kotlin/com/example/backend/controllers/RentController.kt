package com.example.backend.controllers

import com.example.backend.models.Rent
import com.example.backend.services.RentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rents")
class RentController(
  private val rentService: RentService
) {

  @GetMapping
  fun getAllRents(): ResponseEntity<List<Rent>> {
    return ResponseEntity.ok(rentService.getAllRents())
  }

  @GetMapping("/{id}")
  fun getRentById(@PathVariable id: Long): ResponseEntity<Rent> {
    return ResponseEntity.ok(rentService.getRentById(id).orElseThrow { IllegalArgumentException("Rent not found") })
  }

  @PostMapping
  fun createRent(@RequestBody rent: Rent): ResponseEntity<Rent> {
    return ResponseEntity.ok(rentService.createRent(rent))
  }

  @PutMapping("/{id}")
  fun updateRent(@PathVariable id: Long, @RequestBody updatedRent: Rent): ResponseEntity<Rent> {
    return ResponseEntity.ok(rentService.updateRent(id, updatedRent))
  }

  @DeleteMapping("/{id}")
  fun deleteRent(@PathVariable id: Long): ResponseEntity<Void> {
    rentService.deleteRent(id)
    return ResponseEntity.noContent().build()
  }
}
