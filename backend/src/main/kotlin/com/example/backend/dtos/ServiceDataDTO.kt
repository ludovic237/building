package com.example.backend.dtos

data class ServiceDataDTO(
  val id: Long,
  val activeOptions: List<ActiveOption>,
  val billingMode: String,
  val code: String,
  val description: String,
  val isActive: Boolean,
  val name: String,
  val options: List<OptionDTO>,
  val validatedOptions: List<Any>
) {
  constructor(
    id: Long,
    name: String,
    code: String,
    description: String,
    billingMode: String,
    isActive: Boolean,
    activeOptions: List<ActiveOption>
  ) : this(
    id,
    activeOptions,
    billingMode,
    code,
    description,
    isActive,
    name,
    emptyList(),
    emptyList()
  )
}
