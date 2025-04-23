package com.example.backend.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.math.BigDecimal

@Entity
@Table(name = "housting_units")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(value = ["hibernateLazyInitializer", "handler"], ignoreUnknown = true)
class HoustingUnit {
    @Id
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @Column(name = "number", nullable = false, length = 20)
    var number: String? = null

    @Column(name = "floor")
    var floor: Int? = null

    @Column(name = "area", precision = 6, scale = 2)
    var area: BigDecimal? = null

    @Lob
    @Column(name = "address")
    var address: String? = null

    @Column(name = "type", length = 50)
    var type: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    var tenant: Tenant? = null

    @ColumnDefault("0.00")
    @Column(name = "price", precision = 38, scale = 2)
    var price: BigDecimal? = null
}
