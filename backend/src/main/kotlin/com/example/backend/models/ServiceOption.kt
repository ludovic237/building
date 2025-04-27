package com.example.backend.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal

@Entity
@Table(name = "service_options")
class ServiceOption {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "service_id", nullable = false)
  var service: Services? = null

  @Column(name = "name", nullable = false)
  var name: String? = null

  @Column(name = "price", nullable = false, precision = 10, scale = 2)
  var price: BigDecimal? = null

  @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
  var quantity: Int? = null

  @ColumnDefault("1")
  @Column(name = "is_active")
  var isActive: Boolean? = null
}
