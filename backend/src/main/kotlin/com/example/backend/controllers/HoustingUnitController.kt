package com.example.backend.controllers

      import com.example.backend.models.HoustingUnit
      import com.example.backend.services.HoustingUnitService
      import org.springframework.http.ResponseEntity
      import org.springframework.web.bind.annotation.*

      @RestController
      @RequestMapping("/api/housing-units")
      class HoustingUnitController(
          private val houstingUnitService: HoustingUnitService
      ) {

          @GetMapping
          fun getAllHoustingUnits(): ResponseEntity<List<HoustingUnit>> {
              return ResponseEntity.ok(houstingUnitService.getAllHoustingUnits())
          }

          @GetMapping("/{id}")
          fun getHoustingUnitById(@PathVariable id: Long): ResponseEntity<HoustingUnit> {
              return ResponseEntity.ok(houstingUnitService.getHoustingUnitById(id).orElseThrow { IllegalArgumentException("Housting Unit not found") })
          }

          @PostMapping
          fun createHoustingUnit(@RequestBody housingUnit: HoustingUnit): ResponseEntity<HoustingUnit> {
              return ResponseEntity.ok(houstingUnitService.createHoustingUnit(housingUnit))
          }

          @PutMapping("/{id}")
          fun updateHoustingUnit(@PathVariable id: Long, @RequestBody updatedHoustingUnit: HoustingUnit): ResponseEntity<HoustingUnit> {
              return ResponseEntity.ok(houstingUnitService.updateHoustingUnit(id, updatedHoustingUnit))
          }

          @DeleteMapping("/{id}")
          fun deleteHoustingUnit(@PathVariable id: Long): ResponseEntity<Void> {
              houstingUnitService.deleteHoustingUnit(id)
              return ResponseEntity.noContent().build()
          }
      }
