package com.example.backend.dtos

data class ActiveOptionNewDTO(
  val id: Long,
  val name: String,
  val price: Int,
  val quantity: Int,
  val isSelected: Boolean,
)
