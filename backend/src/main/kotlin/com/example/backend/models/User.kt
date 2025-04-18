package com.example.backend.models

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @Column(name = "first_name", nullable = false, length = 100)
  var firstName: String? = null

  @Column(name = "last_name", nullable = false, length = 100)
  var lastName: String? = null

  @Column(name = "email", nullable = false, length = 150)
  var email: String? = null

  @Column(name = "password", nullable = false)
  var password: String? = null

  @Column(name = "phone", length = 20)
  var phone: String? = null

  @Lob
  @Column(name = "role", nullable = false)
  var role: String? = null

  @Column(name = "username", nullable = false)
  var username: String? = null
}
