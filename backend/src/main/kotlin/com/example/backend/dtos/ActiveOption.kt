package com.example.backend.dtos

import java.math.BigDecimal

data class ActiveOption(
    val id: Long,
    val isSelected: Boolean,
    val name: String,
    val price: BigDecimal,
    val quantity: Int
)
