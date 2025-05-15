package com.example.backend.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime


@Entity
@Table(name = "services")
class Services {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @Column(name = "code", nullable = false, length = 50)
  var code: String = "DEFAULT_CODE"

  @Column(name = "name", nullable = false, length = 100)
  var name: String? = null

  @Lob
  @Column(name = "description")
  var description: String? = null

  @Lob
  @Column(name = "billing_mode", nullable = false)
  var billingMode: String? = null

  @ColumnDefault("1")
  @Column(name = "is_active")
  var isActive: Boolean? = null

  @Column(name = "created_date")
  var createdDate: LocalDateTime? = null

  @Column(name = "updated_date")
  var updatedDate: LocalDateTime? = null

}
