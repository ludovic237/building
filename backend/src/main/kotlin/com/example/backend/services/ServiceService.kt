package com.example.backend.services

import com.example.backend.dtos.ActiveOption
import com.example.backend.dtos.OptionDTO
import com.example.backend.dtos.ServiceDataDTO
import com.example.backend.models.ServiceOption
import com.example.backend.models.Services
import com.example.backend.repositories.ServiceOptionRepository
import com.example.backend.repositories.ServiceRepository
import org.springframework.stereotype.Service
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
    val savedServices = serviceRepository.save(services)

    // Create and save the ServiceOption entities
    val serviceOptions = serviceDataDTO.activeOptions.map { optionDTO ->
      ServiceOption().apply {
        name = optionDTO.name
        isActive = optionDTO.isSelected
        price = optionDTO.price
        quantity = optionDTO.quantity
        service = savedServices // Associate with the saved Services
      }
    }
    println("serviceOptions")
    println(serviceOptions)
    serviceOptionRepository.saveAll(serviceOptions)

    return savedServices
  }

  fun getServiceWithOptions(id: Long): ServiceDataDTO {
    val service = serviceRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Service with ID $id not found") }

    // Load associated options
    val options = serviceOptionRepository.findByService(service)

    val activeOptions = options.map { option ->
      ActiveOption(
        id = option.id!!,
        name = option.name!!,
        price = option.price!!,
        quantity = option.quantity!!,
        isSelected = option.isActive!!
      )
    }
    return ServiceDataDTO(
      id = service.id ?: 0L,
      name = service.name ?: "",
      code = service.code,
      description = service.description ?: "",
      billingMode = service.billingMode ?: "",
      isActive = service.isActive ?: false,
      activeOptions = activeOptions
    )
  }

  fun updateServiceWithOptions(id: Long, updatedServiceDTO: ServiceDataDTO): Services {
    val existingService = serviceRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Service with ID $id not found") }

    // Mettre à jour les informations du service
    existingService.apply {
      name = updatedServiceDTO.name
      code = updatedServiceDTO.code ?: throw IllegalArgumentException("Code cannot be null")
      description = updatedServiceDTO.description
      billingMode = updatedServiceDTO.billingMode
      isActive = updatedServiceDTO.isActive
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
        price = optionDTO.price
        quantity = optionDTO.quantity
        isActive = optionDTO.isSelected
        service = updatedService
      }
    }
    serviceOptionRepository.saveAll(updatedOptions)

    return updatedService
  }

  fun updateService(id: Long, updatedServices: Services): Services {
    val existingServices = serviceRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Services with ID $id not found") }

    existingServices.name = updatedServices.name
    existingServices.description = updatedServices.description
    // Update other fields as necessary

    return serviceRepository.save(existingServices)
  }

  fun deleteService(id: Long) {
    if (!serviceRepository.existsById(id)) {
      throw IllegalArgumentException("Services with ID $id not found")
    }
    serviceRepository.deleteById(id)
  }
}
