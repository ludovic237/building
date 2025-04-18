package com.example.backend.controllers

  import com.example.backend.models.Invoice
  import com.example.backend.services.InvoiceService
  import org.springframework.http.ResponseEntity
  import org.springframework.web.bind.annotation.*

  @RestController
  @RequestMapping("/api/invoices")
  class InvoiceController(
      private val invoiceService: InvoiceService
  ) {

      @GetMapping
      fun getAllInvoices(): ResponseEntity<List<Invoice>> {
          return ResponseEntity.ok(invoiceService.getAllInvoices())
      }

      @GetMapping("/{id}")
      fun getInvoiceById(@PathVariable id: Long): ResponseEntity<Invoice> {
          return ResponseEntity.ok(invoiceService.getInvoiceById(id).orElseThrow { IllegalArgumentException("Invoice not found") })
      }

      @PostMapping
      fun createInvoice(@RequestBody invoice: Invoice): ResponseEntity<Invoice> {
          return ResponseEntity.ok(invoiceService.createInvoice(invoice))
      }

      @PutMapping("/{id}")
      fun updateInvoice(@PathVariable id: Long, @RequestBody updatedInvoice: Invoice): ResponseEntity<Invoice> {
          return ResponseEntity.ok(invoiceService.updateInvoice(id, updatedInvoice))
      }

      @DeleteMapping("/{id}")
      fun deleteInvoice(@PathVariable id: Long): ResponseEntity<Void> {
          invoiceService.deleteInvoice(id)
          return ResponseEntity.noContent().build()
      }
  }
