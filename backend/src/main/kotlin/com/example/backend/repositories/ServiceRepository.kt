package com.example.backend.repositories

import com.example.backend.models.Services
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal
import java.util.Optional

interface ServiceRepository : JpaRepository<Services, Long> {

  // Find services by name
  fun findByName(name: String): Optional<Services>

  // Find services by description containing a specific keyword
  fun findByDescriptionContaining(keyword: String): List<Services>

  // Check if a services exists by name
  fun existsByName(name: String): Boolean
}
