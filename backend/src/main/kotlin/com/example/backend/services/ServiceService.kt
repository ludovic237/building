package com.example.backend.services

import com.example.backend.models.Services
import com.example.backend.repositories.ServiceRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ServiceService(
  private val serviceRepository: ServiceRepository
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

  fun updateService(id: Long, updatedServices: Services): Services {
    val existingServices = serviceRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Services with ID $id not found") }

    existingServices.name = updatedServices.name
    existingServices.description = updatedServices.description
    existingServices.monthlyPrice = updatedServices.monthlyPrice
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
