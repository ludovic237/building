package com.example.backend.controllers

import com.example.backend.models.HoustingUnit
import com.example.backend.services.HoustingUnitService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/housing-units")
class HoustingUnitController(
  private val housingUnitService: HoustingUnitService
) {

  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping
  fun getAllHoustingUnits(): ResponseEntity<List<HoustingUnit>> {
    return ResponseEntity.ok(housingUnitService.getAllHoustingUnits())
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping("/{id}")
  fun getHoustingUnitById(@PathVariable id: Long): ResponseEntity<HoustingUnit> {
    return ResponseEntity.ok(
      housingUnitService.getHoustingUnitById(id).orElseThrow { IllegalArgumentException("Housting Unit not found") })
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PostMapping
  fun createHoustingUnit(@RequestBody housingUnit: HoustingUnit): ResponseEntity<HoustingUnit> {
    return ResponseEntity.ok(housingUnitService.createHoustingUnit(housingUnit))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PutMapping("/{id}")
  fun updateHoustingUnit(
    @PathVariable id: Long,
    @RequestBody updatedHoustingUnit: HoustingUnit
  ): ResponseEntity<HoustingUnit> {
    return ResponseEntity.ok(housingUnitService.updateHoustingUnit(id, updatedHoustingUnit))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @DeleteMapping("/{id}")
  fun deleteHoustingUnit(@PathVariable id: Long): ResponseEntity<Void> {
    housingUnitService.deleteHoustingUnit(id)
    return ResponseEntity.noContent().build()
  }
}
