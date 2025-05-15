package com.example.backend.models

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
data class Document(
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0,

  @Column(nullable = false, unique = true)
  val hash: String,

  @Lob
  @Column(nullable = false)
  val content: ByteArray
)
