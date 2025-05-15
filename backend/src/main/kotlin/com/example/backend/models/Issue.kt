package com.example.backend.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime

@Entity
@Table(name = "issues")
class Issue {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tenant_id")
  var tenant: com.example.backend.models.Tenant? = null

  @Column(name = "title", nullable = false, length = 150)
  var title: String? = null

  @Lob
  @Column(name = "description")
  var description: String? = null

  @ColumnDefault("(curdate())")
  @Column(name = "declaration_date")
  var declarationDate: LocalDateTime? = null

  @Lob
  @Column(name = "status", nullable = false)
  var status: String? = null

  @Column(name = "created_date")
  var createdDate: LocalDateTime? = null

  @Column(name = "updated_date")
  var updatedDate: LocalDateTime? = null

  override fun toString(): String {
    return "Issue(id=$id, tenant=${tenant?.id}, title=$title, description=$description, declarationDate=$declarationDate, status='$status')"
  }
}
