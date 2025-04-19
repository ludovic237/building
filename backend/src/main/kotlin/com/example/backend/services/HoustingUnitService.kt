package com.example.backend.services

    import com.example.backend.models.HoustingUnit
    import com.example.backend.repositories.HoustingUnitRepository
    import org.springframework.stereotype.Service
    import java.util.*

    @Service
    class HoustingUnitService(
        private val houstingUnitRepository: HoustingUnitRepository
    ) {

        fun getAllHoustingUnits(): List<HoustingUnit> {
            return houstingUnitRepository.findAll()
        }

        fun getHoustingUnitById(id: Long): Optional<HoustingUnit> {
            return houstingUnitRepository.findById(id)
        }

        fun createHoustingUnit(houstingUnit: HoustingUnit): HoustingUnit {
            return houstingUnitRepository.save(houstingUnit)
        }

        fun updateHoustingUnit(id: Long, updatedHoustingUnit: HoustingUnit): HoustingUnit {
            val existingHoustingUnit = houstingUnitRepository.findById(id)
                .orElseThrow { IllegalArgumentException("HoustingUnit with ID $id not found") }

            existingHoustingUnit.number = updatedHoustingUnit.number
            existingHoustingUnit.floor = updatedHoustingUnit.floor
            existingHoustingUnit.area = updatedHoustingUnit.area
            existingHoustingUnit.address = updatedHoustingUnit.address
            existingHoustingUnit.type = updatedHoustingUnit.type
            existingHoustingUnit.price = updatedHoustingUnit.price
            // Update other fields as necessary

            return houstingUnitRepository.save(existingHoustingUnit)
        }

        fun deleteHoustingUnit(id: Long) {
            if (!houstingUnitRepository.existsById(id)) {
                throw IllegalArgumentException("HoustingUnit with ID $id not found")
            }
            houstingUnitRepository.deleteById(id)
        }
    }
