package com.example.backend.models

import jakarta.persistence.*
import lombok.Data
import java.math.BigDecimal
import java.time.LocalDate

@Data
@Entity
@Table(name = "rents")
class Rent {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tenant_id")
  var tenant: com.example.backend.models.Tenant? = null

  @Column(name = "month")
  var month: Int? = null

  @Column(name = "year")
  var year: Int? = null

  @Column(name = "amount", nullable = false, precision = 10, scale = 2)
  var amount: BigDecimal? = null

  @Column(name = "housting_price", nullable = false, precision = 10, scale = 2)
  var houstingPrice: BigDecimal? = null

  @Column(name = "payment_date")
  var paymentDate: LocalDate? = null

  @Lob
  @Column(name = "status", nullable = false)
  var status: String? = null
}
