package com.example.backend.services

import com.example.backend.models.Invoice
import com.example.backend.repositories.InvoiceRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class InvoiceService(
    private val invoiceRepository: InvoiceRepository
) {

    fun getAllInvoices(): List<Invoice> {
        return invoiceRepository.findAll()
    }

    fun getInvoiceById(id: Long): Optional<Invoice> {
        return invoiceRepository.findById(id)
    }

    fun createInvoice(invoice: Invoice): Invoice {
        return invoiceRepository.save(invoice)
    }

    fun updateInvoice(id: Long, updatedInvoice: Invoice): Invoice {
        val existingInvoice = invoiceRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Invoice with ID $id not found") }

        existingInvoice.amount = updatedInvoice.amount
        existingInvoice.status = updatedInvoice.status
        existingInvoice.tenant = updatedInvoice.tenant
        // Update other fields as necessary

        return invoiceRepository.save(existingInvoice)
    }

    fun deleteInvoice(id: Long) {
        if (!invoiceRepository.existsById(id)) {
            throw IllegalArgumentException("Invoice with ID $id not found")
        }
        invoiceRepository.deleteById(id)
    }
}
