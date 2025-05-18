package com.example.backend.repositories

import com.example.backend.models.InvoiceCounter
import org.springframework.data.jpa.repository.JpaRepository

interface InvoiceCounterRepository : JpaRepository<InvoiceCounter, Long> {
    fun findByYear(year: Int): InvoiceCounter?
}
