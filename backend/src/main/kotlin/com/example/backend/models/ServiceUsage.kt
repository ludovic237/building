package com.example.backend.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.Instant
import java.time.LocalDateTime

@Entity
@Table(name = "service_usage")
class ServiceUsage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "subscription_id", nullable = false)
  var subscription: Subscription? = null

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "option_id", nullable = false, referencedColumnName = "option_id")
  var option: com.example.backend.models.SubscriptionOption? = null

  @Column(name = "quantity_used", nullable = false)
  var quantityUsed: Int? = null

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "usage_date")
  var usageDate: Instant? = null

  @Column(name = "created_date")
  var createdDate: LocalDateTime? = null

  @Column(name = "updated_date")
  var updatedDate: LocalDateTime? = null
}
