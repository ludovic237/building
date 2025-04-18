package com.example.backend.services

import com.example.backend.models.Subscription
import com.example.backend.repositories.SubscriptionRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class SubscriptionService(
    private val subscriptionRepository: SubscriptionRepository
) {

    fun getAllSubscriptions(): List<Subscription> {
        return subscriptionRepository.findAll()
    }

    fun getSubscriptionById(id: Long): Optional<Subscription> {
        return subscriptionRepository.findById(id)
    }

    fun createSubscription(subscription: Subscription): Subscription {
        return subscriptionRepository.save(subscription)
    }

    fun updateSubscription(id: Long, updatedSubscription: Subscription): Subscription {
        val existingSubscription = subscriptionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Subscription with ID $id not found") }

        existingSubscription.startDate = updatedSubscription.startDate
        existingSubscription.endDate = updatedSubscription.endDate
        existingSubscription.status = updatedSubscription.status
        // Update other fields as necessary

        return subscriptionRepository.save(existingSubscription)
    }

    fun deleteSubscription(id: Long) {
        if (!subscriptionRepository.existsById(id)) {
            throw IllegalArgumentException("Subscription with ID $id not found")
        }
        subscriptionRepository.deleteById(id)
    }
}
