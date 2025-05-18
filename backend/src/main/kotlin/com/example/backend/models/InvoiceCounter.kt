package com.example.backend.models

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "invoice_counter")
class InvoiceCounter (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0,

  @Column(nullable = false)
  var year: Int,

  @Column(nullable = false)
  var counter: Int
)
