package com.example.backend.dtos

import java.math.BigDecimal
import java.time.LocalDate

data class DashboardDTO(
    val totalActiveSubscriptions: Int,
    val totalAmountDue: BigDecimal,
    val totalPaymentsPending: Int,
    val tenantsWithOverduePayments: List<String>,
    val monthlyRevenue: Map<String, BigDecimal>
) {

}
