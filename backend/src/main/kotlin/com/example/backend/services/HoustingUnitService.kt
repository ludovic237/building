package com.example.backend.services

    import com.example.backend.models.HoustingUnit
    import com.example.backend.repositories.HoustingUnitRepository
    import org.springframework.stereotype.Service
    import java.util.*

    @Service
    class HoustingUnitService(
        private val housingUnitRepository: HoustingUnitRepository
    ) {

        fun getAllHoustingUnits(): List<HoustingUnit> {
            return housingUnitRepository.findAll()
        }

        fun getHoustingUnitById(id: Long): Optional<HoustingUnit> {
            return housingUnitRepository.findById(id)
        }

        fun createHoustingUnit(housingUnit: HoustingUnit): HoustingUnit {
            return housingUnitRepository.save(housingUnit)
        }

        fun updateHoustingUnit(id: Long, updatedHoustingUnit: HoustingUnit): HoustingUnit {
            val existingHoustingUnit = housingUnitRepository.findById(id)
                .orElseThrow { IllegalArgumentException("HoustingUnit with ID $id not found") }

            existingHoustingUnit.number = updatedHoustingUnit.number
            existingHoustingUnit.floor = updatedHoustingUnit.floor
            existingHoustingUnit.area = updatedHoustingUnit.area
            existingHoustingUnit.address = updatedHoustingUnit.address
            existingHoustingUnit.type = updatedHoustingUnit.type
            existingHoustingUnit.price = updatedHoustingUnit.price
            // Update other fields as necessary

            return housingUnitRepository.save(existingHoustingUnit)
        }

        fun deleteHoustingUnit(id: Long) {
            if (!housingUnitRepository.existsById(id)) {
                throw IllegalArgumentException("HoustingUnit with ID $id not found")
            }
            housingUnitRepository.deleteById(id)
        }
    }
