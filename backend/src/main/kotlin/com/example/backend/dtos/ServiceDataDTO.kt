package com.example.backend.dtos

data class ServiceDataDTO(
  val id: Long,
  val type: String,
  val addPrice: Boolean,
  val activeOptions: List<ActiveOptionNewDTO>,
  val billingMode: String,
  val code: String,
  val description: String,
  val isActive: Boolean,
  val name: String,
  val price: Int,
  val options: List<OptionNewDTO>,
  val validatedOptions: List<Any>
) {
  constructor(
    id: Long,
    type: String,
    addPrice: Boolean,
    name: String,
    code: String,
    description: String,
    billingMode: String,
    price: Int,
    isActive: Boolean,
    activeOptions: List<ActiveOptionNewDTO>
  ) : this(
    id,
    type,
    addPrice,
    activeOptions,
    billingMode,
    code,
    description,
    isActive,
    name,
    price,
    emptyList(),
    emptyList()
  )

  constructor(
    id: Long,
    name: String,
    code: String,
    description: String,
    billingMode: String,
    isActive: Boolean,
    activeOptions: List<ActiveOptionNewDTO>
  ) : this(
    id,
    type = "",
    addPrice = false,
    activeOptions = activeOptions,
    billingMode = billingMode,
    code = code,
    description = description,
    isActive = isActive,
    name = name,
    price = 0,
    options = emptyList(),
    validatedOptions = emptyList()
  )
}
