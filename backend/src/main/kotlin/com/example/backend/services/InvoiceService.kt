package com.example.backend.services

import com.example.backend.models.Invoice
import com.example.backend.models.InvoiceCounter
import com.example.backend.repositories.InvoiceCounterRepository
import com.example.backend.repositories.InvoiceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.Year
import java.util.*

@Service
class InvoiceService(
    private val invoiceRepository: InvoiceRepository,
    private val invoiceCounterRepository: InvoiceCounterRepository,
) {

    fun getAllInvoices(): List<Invoice> {
        return invoiceRepository.findAll()
    }

    fun getInvoiceById(id: Long): Optional<Invoice> {
        return invoiceRepository.findById(id)
    }

  @Transactional
    fun createInvoice(invoice: Invoice): Invoice {
      invoice.createdDate = LocalDateTime.now()
      invoice.number = generateInvoiceNumber()
        return invoiceRepository.save(invoice)
    }

    fun updateInvoice(id: Long, updatedInvoice: Invoice): Invoice {
        val existingInvoice = invoiceRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Invoice with ID $id not found") }

        existingInvoice.amount = updatedInvoice.amount
        existingInvoice.status = updatedInvoice.status
        existingInvoice.tenant = updatedInvoice.tenant
        existingInvoice.updatedDate = LocalDateTime.now()
        // Update other fields as necessary

        return invoiceRepository.save(existingInvoice)
    }

    fun deleteInvoice(id: Long) {
        if (!invoiceRepository.existsById(id)) {
            throw IllegalArgumentException("Invoice with ID $id not found")
        }
        invoiceRepository.deleteById(id)
    }

  @Transactional
  fun generateInvoiceNumber(): String {
    val currentYear = Year.now().value
    val today = LocalDate.now()
    val currentMonth = String.format("%02d",today.monthValue)
    val currentDay = String.format("%02d",today.dayOfMonth)
    val counter = invoiceCounterRepository.findByYear(currentYear)
      ?: invoiceCounterRepository.save(InvoiceCounter(year = currentYear, counter = 0))

    counter.counter += 1
    invoiceCounterRepository.save(counter)

    return "INV-${currentYear}-${currentMonth}-${String.format("%05d", counter.counter)}"
  }
}
