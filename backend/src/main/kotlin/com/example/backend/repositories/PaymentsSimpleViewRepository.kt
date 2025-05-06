package com.example.backend.repositories

import com.example.backend.models.PaymentsSimpleView
import com.example.backend.models.PaymentsView
import com.example.backend.models.Subscription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
interface PaymentsSimpleViewRepository : JpaRepository<PaymentsSimpleView, Long> {

}
