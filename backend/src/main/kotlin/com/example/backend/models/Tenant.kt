package com.example.backend.models

import jakarta.persistence.*
import lombok.Data
import org.hibernate.annotations.ColumnDefault
import java.math.BigDecimal
import java.time.LocalDate

@Data
@Entity
@Table(name = "tenants")
class Tenant {
@Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  var user: com.example.backend.models.User? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "housing_unit_id")
  var housingUnit: HoustingUnit? = null

  @Column(name = "move_in_date", nullable = false)
  var moveInDate: LocalDate? = null

  @Column(name = "move_out_date")
  var moveOutDate: LocalDate? = null

  @ColumnDefault("0.00")
  @Column(name = "security_deposit", precision = 10, scale = 2)
  var securityDeposit: BigDecimal? = null

  @ColumnDefault("0.00")
  @Column(name = "housting_price", precision = 10, scale = 2)
  var houstingPrice: BigDecimal? = null
}
