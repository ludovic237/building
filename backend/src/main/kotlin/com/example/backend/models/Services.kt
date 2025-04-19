package com.example.backend.models

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "services")
class Services {
  @Id
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @Column(name = "name", nullable = false, length = 100)
  var name: String? = null

  @Lob
  @Column(name = "description")
  var description: String? = null

  @Column(name = "monthly_price", precision = 10, scale = 2)
  var monthlyPrice: BigDecimal? = null
}
