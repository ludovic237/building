package com.example.backend.repositories

      import com.example.backend.models.Services
      import org.springframework.data.jpa.repository.JpaRepository
      import java.math.BigDecimal
      import java.util.Optional

      interface ServiceRepository : JpaRepository<Services, Long> {

          // Find services by name
          fun findByName(name: String): Optional<Services>

          // Find services with a monthly price greater than or equal to a specific value
          fun findByMonthlyPriceGreaterThanEqual(price: BigDecimal): List<Services>

          // Find services with a monthly price less than or equal to a specific value
          fun findByMonthlyPriceLessThanEqual(price: BigDecimal): List<Services>

          // Find services by description containing a specific keyword
          fun findByDescriptionContaining(keyword: String): List<Services>

          // Check if a services exists by name
          fun existsByName(name: String): Boolean
      }
