package com.example.backend.models

import jakarta.persistence.*
import lombok.Data
import org.hibernate.annotations.ColumnDefault
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Data
@Table(name = "payments")
class Payment {
  @Id
  @Column(name = "id", nullable = false)
  var id: Long? = null

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tenant_id", nullable = false)
  var tenant: com.example.backend.models.Tenant? = null

  @ColumnDefault("(curdate())")
  @Column(name = "payment_date")
  var paymentDate: LocalDate? = null

  @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
  var totalAmount: BigDecimal? = null

  @Column(name = "payment_method", length = 50)
  var paymentMethod: String? = null
}
