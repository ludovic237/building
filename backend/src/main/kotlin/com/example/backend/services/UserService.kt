package com.example.backend.services

    import com.example.backend.models.User
    import com.example.backend.repositories.UserRepository
    import org.springframework.stereotype.Service
    import java.util.*

    @Service
    class UserService(
        private val userRepository: UserRepository
    ) {

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

            existingUser.firstName = updatedUser.firstName
            existingUser.lastName = updatedUser.lastName
            existingUser.email = updatedUser.email
            // Update other fields as necessary

            return userRepository.save(existingUser)
        }

        fun deleteUser(id: Long) {
            if (!userRepository.existsById(id)) {
                throw IllegalArgumentException("User with ID $id not found")
            }
            userRepository.deleteById(id)
        }
    }
