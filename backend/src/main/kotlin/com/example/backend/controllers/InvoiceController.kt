package com.example.backend.controllers

import com.example.backend.models.Invoice
import com.example.backend.services.InvoiceService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/invoices")
class InvoiceController(
  private val invoiceService: InvoiceService
) {

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping
  fun getAllInvoices(): ResponseEntity<List<Invoice>> {
    return ResponseEntity.ok(invoiceService.getAllInvoices())
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}")
  fun getInvoiceById(@PathVariable id: Long): ResponseEntity<Invoice> {
    return ResponseEntity.ok(
      invoiceService.getInvoiceById(id).orElseThrow { IllegalArgumentException("Invoice not found") })
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @PostMapping
  fun createInvoice(@RequestBody invoice: Invoice): ResponseEntity<Invoice> {
    return ResponseEntity.ok(invoiceService.createInvoice(invoice))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @PutMapping("/{id}")
  fun updateInvoice(@PathVariable id: Long, @RequestBody updatedInvoice: Invoice): ResponseEntity<Invoice> {
    return ResponseEntity.ok(invoiceService.updateInvoice(id, updatedInvoice))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{id}")
  fun deleteInvoice(@PathVariable id: Long): ResponseEntity<Void> {
    invoiceService.deleteInvoice(id)
    return ResponseEntity.noContent().build()
  }
}
