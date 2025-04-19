package com.example.backend.models

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "housting_units")
class HoustingUnit {
  @Id
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @Column(name = "number", nullable = false, length = 20)
  var number: String? = null

  @Column(name = "floor")
  var floor: Int? = null

  @Column(name = "area", precision = 6, scale = 2)
  var area: BigDecimal? = null

  @Lob
  @Column(name = "address")
  var address: String? = null

  @Column(name = "type", length = 50)
  var type: String? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tenant_id")
  var tenant: com.example.backend.models.Tenant? = null
}
