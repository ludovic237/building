package com.example.backend.dtos

data class OptionDTO(
  val id: Long?,
  val name: String,
  val price: Double,
  val quantity: Int,
  val isSelected: Boolean
)
