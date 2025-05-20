package com.example.backend.dtos

data class ServiceDataNewDTO(
  val id: Long?,
  val activeOptions: List<ActiveOptionNewDTO>?,
  val addPrice: Boolean?,
  val billingMode: String?,
  val code: String?,
  val description: String?,
  val isActive: Boolean?,
  val name: String?,
  val options: List<OptionNewDTO>?,
  val price: Int?,
  val type: String?
)
