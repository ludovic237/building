package com.example.backend.services

import com.example.backend.dtos.*
import com.example.backend.models.ServiceOption
import com.example.backend.models.Services
import com.example.backend.repositories.ServiceOptionRepository
import com.example.backend.repositories.ServiceRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class ServiceService(
  private val serviceRepository: ServiceRepository,
  private val serviceOptionRepository: ServiceOptionRepository,
) {

  fun getAllServices(): List<Services> {
    return serviceRepository.findAll()
  }

  fun getServiceById(id: Long): Optional<Services> {
    return serviceRepository.findById(id)
  }

  fun createService(services: Services): Services {
    return serviceRepository.save(services)
  }

  fun getServiceWithOptions(id: Long): ServiceDataNewDTO {
    val service = serviceRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Service with ID $id not found") }

    // Load associated options
    val options = serviceOptionRepository.findByService(service)

    val activeOptions = options.map { option ->
      ActiveOptionNewDTO(
        id = option.id!!,
        name = option.name!!,
        price = option.price!!.toInt(),
        quantity = option.maxQuantity!!,
        isSelected = option.isActive!!
      )
    }

    return ServiceDataNewDTO(
      id = service.id ?: 0L,
      name = service.name ?: "",
      code = service.code,
      price = service.price?.toInt(),
      type = service.type,
      description = service.description ?: "",
      billingMode = service.billingMode ?: "",
      addPrice = service.price?.let { it > BigDecimal.ZERO } ?: false,
      isActive = service.isActive ?: false,
      activeOptions = activeOptions,
      options = activeOptions.map { option ->
        OptionNewDTO(
          name = option.name,
          price = option.price,
          quantity = option.quantity
        )
      }
    )
  }

  fun createServiceData(serviceDataDTO: ServiceDataDTO): Services {
    // Create and save the Services entity
    val services = Services().apply {
      name = serviceDataDTO.name
      code = serviceDataDTO.code ?: throw IllegalArgumentException("Code cannot be null")
      description = serviceDataDTO.description
      billingMode = serviceDataDTO.billingMode
      isActive = serviceDataDTO.isActive
    }
    println("services")
    println(services)
    services.createdDate = LocalDateTime.now()
    services.updatedDate = LocalDateTime.now()
    val savedServices = serviceRepository.save(services)

    // Create and save the ServiceOption entities
    val serviceOptions = serviceDataDTO.activeOptions.map { optionDTO ->
      ServiceOption().apply {
        name = optionDTO.name
        isActive = optionDTO.isSelected
        price = optionDTO.price.toBigDecimal()
        maxQuantity = optionDTO.quantity
        createdDate = LocalDateTime.now()
        updatedDate = LocalDateTime.now()
        service = savedServices // Associate with the saved Services
      }
    }
    println("serviceOptions")
    println(serviceOptions)
    serviceOptionRepository.saveAll(serviceOptions)

    return savedServices
  }

  fun getServiceAllWithOptions(): List<ServiceDataNewDTO> {
    val services = serviceRepository.findAll()
    return services.map { service ->
      // Load associated options for each service
      val options = serviceOptionRepository.findByService(service)
      println("options")
      println(options)
      val activeOptions = options.map { option ->
        println(option.name)
        println(option.maxQuantity)
        println(option.isActive)
        ActiveOptionNewDTO(
          id = option.id!!,
          name = option.name!!,
          price = option.price!!.toInt(),
          quantity = option.maxQuantity!!,
          isSelected = option.isActive ?: false,
        )
      }

      ServiceDataNewDTO(
        id = service.id ?: 0L,
        type = service.type.toString(),
        addPrice = false,
        name = service.name ?: "",
        code = service.code,
        description = service.description ?: "",
        billingMode = service.billingMode ?: "",
        price = service.price?.toInt() ?: 0,
        isActive = service.isActive ?: false,
        activeOptions = activeOptions,
        options = emptyList()
      )
    }
  }

  fun updateServiceWithOptions(id: Long, updatedServiceDTO: ServiceDataDTO): Services {
    val existingService = serviceRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Service with ID $id not found") }

    // Mettre à jour les informations du service
    existingService.apply {
      name = updatedServiceDTO.name
      code = updatedServiceDTO.code ?: throw IllegalArgumentException("Code cannot be null")
      description = updatedServiceDTO.description
      billingMode = updatedServiceDTO.billingMode.toLowerCase()
      isActive = updatedServiceDTO.isActive
      updatedDate = LocalDateTime.now()
    }
    val updatedService = serviceRepository.save(existingService)

    // Mettre à jour les options
    val existingOptions = serviceOptionRepository.findByService(existingService)

    // Supprimer les options qui ne sont plus présentes
    val optionsToDelete = existingOptions.filter { existingOption ->
      updatedServiceDTO.activeOptions.none { it.id == existingOption.id }
    }
    serviceOptionRepository.deleteAll(optionsToDelete)

    // Ajouter ou mettre à jour les options existantes
    val updatedOptions = updatedServiceDTO.activeOptions.map { optionDTO ->
      val option = existingOptions.find { it.id == optionDTO.id } ?: ServiceOption()
      option.apply {
        name = optionDTO.name
        price = optionDTO.price.toBigDecimal()
        maxQuantity = optionDTO.quantity
        isActive = optionDTO.isSelected
        service = updatedService
        updatedDate = LocalDateTime.now()
      }
    }
    serviceOptionRepository.saveAll(updatedOptions)

    return updatedService
  }

  fun updateService(id: Long, updatedServices: Services): Services {
    val existingServices = serviceRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Services with ID $id not found") }

    existingServices.name = updatedServices.name
    existingServices.updatedDate = LocalDateTime.now()
    existingServices.description = updatedServices.description
    // Update other fields as necessary

    return serviceRepository.save(existingServices)
  }

  fun deleteService(id: Long) {
    val service = serviceRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Services with ID $id not found") }

    // Supprimer les options associées au service
    val associatedOptions = serviceOptionRepository.findByService(service)
    serviceOptionRepository.deleteAll(associatedOptions)

    // Supprimer le service
    serviceRepository.deleteById(id)
  }

  fun createServiceData(serviceDataDTO: ServiceDataNewDTO): Services {
    val services = Services().apply {
      name = serviceDataDTO.name
      code = serviceDataDTO.code ?: throw IllegalArgumentException("Code cannot be null")
      description = serviceDataDTO.description
      billingMode = serviceDataDTO.billingMode
      isActive = serviceDataDTO.isActive
      type = serviceDataDTO.type
      price =
        if (serviceDataDTO.addPrice!!) serviceDataDTO.price!!.toBigDecimal() ?: BigDecimal.ZERO else BigDecimal.ZERO
      createdDate = LocalDateTime.now()
      updatedDate = LocalDateTime.now()
    }
    val savedServices = serviceRepository.save(services)

    val serviceOptions = serviceDataDTO.options!!.map { optionDTO ->
      ServiceOption().apply {
        name = optionDTO.name
        price = optionDTO.price.toBigDecimal()
        maxQuantity = optionDTO.quantity
        isActive = true
        service = savedServices
        createdDate = LocalDateTime.now()
        updatedDate = LocalDateTime.now()
      }
    }
    serviceOptionRepository.saveAll(serviceOptions)

    return savedServices
  }

  fun updateServiceWithOptions(id: Long, updatedServiceDTO: ServiceDataNewDTO): Services {
    val existingService = serviceRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Service with ID $id not found") }
    println("existingService");
    println(existingService)
    existingService.apply {
      name = updatedServiceDTO.name
      code = updatedServiceDTO.code ?: throw IllegalArgumentException("Code cannot be null")
      description = updatedServiceDTO.description
      billingMode = updatedServiceDTO.billingMode
      isActive = updatedServiceDTO.isActive
      type = updatedServiceDTO.type
      price =
        if (updatedServiceDTO.addPrice!!) updatedServiceDTO.price!!.toBigDecimal()
          ?: BigDecimal.ZERO else BigDecimal.ZERO
      updatedDate = LocalDateTime.now()
    }
    val updatedService = serviceRepository.save(existingService)

    val existingOptions = serviceOptionRepository.findByService(existingService)

    val optionsToDelete = existingOptions.filter { existingOption ->
      updatedServiceDTO.options!!.none { it.name == existingOption.name }
    }
    serviceOptionRepository.deleteAll(optionsToDelete)

    val updatedOptions = updatedServiceDTO.options!!.map { optionDTO ->
      val option = existingOptions.find { it.name == optionDTO.name } ?: ServiceOption()
      option.apply {
        name = optionDTO.name
        price = optionDTO.price.toBigDecimal()
        maxQuantity = optionDTO.quantity
        isActive = true
        service = updatedService
        updatedDate = LocalDateTime.now()
      }
    }
    serviceOptionRepository.saveAll(updatedOptions)

    return updatedService
  }
}
