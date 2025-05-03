package com.example.backend.repositories

import com.example.backend.models.ServiceOption
import com.example.backend.models.Services
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal
import java.util.Optional

interface ServiceOptionRepository : JpaRepository<ServiceOption, Long> {

  // Find services by name
  fun findByName(name: String): Optional<ServiceOption>
  // Find services by name
  fun findByService(services: Services): List<ServiceOption>
  fun findByServiceIn(service: MutableCollection<Services>): List<ServiceOption>

  // Check if a services exists by name
  fun existsByName(name: String): Boolean
}
