package com.example.backend.dtos

data class OptionNewDTO(
    val name: String,
    val pricingModel: String,
    val price: Int,
    val quantity: Int
)
