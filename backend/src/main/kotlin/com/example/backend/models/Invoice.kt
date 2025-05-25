package com.example.backend.models

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime

@Entity
@Table(name = "invoices")
class Invoice {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  var user: User? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "modify_id")
  var modify: User? = null

  @Lob
  @Column(name = "type", nullable = false)
  var type: String? = null

  @Column(name = "month")
  var month: Int? = null

  @Column(name = "year")
  var year: Int? = null

  @Column(name = "amount", precision = 10, scale = 2)
  var amount: BigDecimal? = null

  @Column(name = "payment_date")
  var paymentDate: LocalDateTime? = null

  @Lob
  @Column(name = "status", nullable = false)
  var status: String? = null

  @Lob
  @Column(name = "number")
  var number: String? = null

  @Column(name = "created_date", nullable = false)
  var createdDate: LocalDateTime? = null

  @Column(name = "updated_date", nullable = false)
  var updatedDate: LocalDateTime? = null

  @Column(name = "tenant_id")
  var tenantId: Long? = null
}
