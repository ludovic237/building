package com.example.backend.models

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "documents")
class Document (
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null,

  @Column(name = "name")
  var name: String? = null,

  @Column(name = "type", length = 100)
  var type: String? = null,

  @Column(name = "size")
  var size: Int? = null,

  @Column(name = "hash")
  var hash: String? = null,

  @Column(name = "content")
  var content: ByteArray? = null,
//  var content: String? = null,

  @Column(name = "created_date")
  var createdDate: Instant? = null,

  @Column(name = "updated_date")
  var updatedDate: Instant? = null,

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  var user: User? = null
)
