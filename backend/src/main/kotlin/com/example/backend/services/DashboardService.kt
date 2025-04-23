package com.example.backend.services

import com.example.backend.dtos.DashboardDTO
import com.example.backend.models.User
import com.example.backend.repositories.UserRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class DashboardService(
  private val userRepository: UserRepository
) {

  fun getDashboardData(): DashboardDTO {
    val totalActiveSubscriptions = 10 // Example value
    val totalAmountDue = BigDecimal("1000.00") // Example value
    val totalPaymentsPending = 5 // Example value
    val tenantsWithOverduePayments = listOf("Tenant A", "Tenant B") // Example value
    val monthlyRevenue = mapOf("January" to BigDecimal("500.00"), "February" to BigDecimal("600.00")) // Example value

    return DashboardDTO(
      totalActiveSubscriptions = totalActiveSubscriptions,
      totalAmountDue = totalAmountDue,
      totalPaymentsPending = totalPaymentsPending,
      tenantsWithOverduePayments = tenantsWithOverduePayments,
      monthlyRevenue = monthlyRevenue
    )
  }

  fun getAllUsers(): List<User> {
    return userRepository.findAll()
  }

  fun getUserById(id: Long): Optional<User> {
    return userRepository.findById(id)
  }

  fun createUser(user: User): User {
    return userRepository.save(user)
  }

  fun updateUser(id: Long, updatedUser: User): User {
    val existingUser = userRepository.findById(id)
      .orElseThrow { IllegalArgumentException("User with ID $id not found") }

    updatedUser.joinedDate = Date().toInstant()

    return userRepository.save(updatedUser)
  }

  fun deleteUser(id: Long) {
    if (!userRepository.existsById(id)) {
      throw IllegalArgumentException("User with ID $id not found")
    }
    userRepository.deleteById(id)
  }
}
