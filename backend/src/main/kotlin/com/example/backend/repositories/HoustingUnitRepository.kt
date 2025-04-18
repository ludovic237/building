package com.example.backend.repositories

import com.example.backend.models.HoustingUnit
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal
import java.util.Optional

interface HoustingUnitRepository : JpaRepository<HoustingUnit, Long> {
  // Find housing units by floor
  fun findByFloor(floor: Int): List<HoustingUnit>

  // Find housing units by type
  fun findByType(type: String): List<HoustingUnit>

  // Find housing units by area greater than or equal to a specific value
  fun findByAreaGreaterThanEqual(area: BigDecimal): List<HoustingUnit>

  // Find housing units by address containing a specific keyword
  fun findByAddressContaining(keyword: String): List<HoustingUnit>

  // Check if a housing unit exists by its number
  fun existsByNumber(number: String): Boolean

  // Find a housing unit by its number
  fun findByNumber(number: String): Optional<HoustingUnit>
}
