package com.example.backend.models

import jakarta.persistence.*
import lombok.Data
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDate

@Data
@Entity
@Table(name = "issues")
class Issue {
  @Id
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
  var declarationDate: LocalDate? = null

  @Lob
  @Column(name = "status", nullable = false)
  var status: String? = null
}
