package com.example.backend.services

    import com.example.backend.models.Rent
    import com.example.backend.repositories.RentRepository
    import org.springframework.stereotype.Service
    import java.util.*

    @Service
    class RentService(
        private val rentRepository: RentRepository
    ) {

        fun getAllRents(): List<Rent> {
            return rentRepository.findAll()
        }

        fun getRentById(id: Long): Optional<Rent> {
            return rentRepository.findById(id)
        }

        fun createRent(rent: Rent): Rent {
            return rentRepository.save(rent)
        }

        fun updateRent(id: Long, updatedRent: Rent): Rent {
            val existingRent = rentRepository.findById(id)
                .orElseThrow { IllegalArgumentException("Rent with ID $id not found") }

            existingRent.amount = updatedRent.amount
            existingRent.paymentDate = updatedRent.paymentDate
            existingRent.status = updatedRent.status
            existingRent.tenant = updatedRent.tenant
            // Update other fields as necessary

            return rentRepository.save(existingRent)
        }

        fun deleteRent(id: Long) {
            if (!rentRepository.existsById(id)) {
                throw IllegalArgumentException("Rent with ID $id not found")
            }
            rentRepository.deleteById(id)
        }
    }
