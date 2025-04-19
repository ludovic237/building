package com.example.backend.models

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "subscriptions")
class Subscription {
@Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  var user: com.example.backend.models.User? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "service_id")
  var service: Services? = null

  @Column(name = "start_date", nullable = false)
  var startDate: LocalDate? = null

  @Column(name = "end_date")
  var endDate: LocalDate? = null

  @Lob
  @Column(name = "status", nullable = false)
  var status: String? = null
}
