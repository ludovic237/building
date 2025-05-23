package com.example.backend.repositories

import com.example.backend.models.Subscription
import com.example.backend.models.SubscriptionServices
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionServiceRepository : JpaRepository<SubscriptionServices, Long> {

 fun findBySubscription(subscription: Subscription): List<SubscriptionServices>

}
