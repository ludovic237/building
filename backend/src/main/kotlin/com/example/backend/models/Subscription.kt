package com.example.backend.models

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "subscriptions")
class Subscription {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tenant_id")
  var tenant: com.example.backend.models.Tenant? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "service_id")
  var service: Services? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invoice_id")
  var invoice: Invoice? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "modify_by")
  var modifyBy: User? = null

  @Column(name = "price", precision = 10, scale = 2)
  var price: BigDecimal? = null

  @Column(name = "start_date", nullable = false)
  var startDate: LocalDateTime? = null

  @Column(name = "end_date")
  var endDate: LocalDateTime? = null

  @Lob
  @Column(name = "status", nullable = false)
  var status: String? = null

  @Lob
  @Column(name = "subscript_number", nullable = false)
  var subscriptNumber: Int? = null

  @Column(name = "created_date")
  var createdDate: LocalDateTime? = null

  @Column(name = "updated_date")
  var updatedDate: LocalDateTime? = null
}
