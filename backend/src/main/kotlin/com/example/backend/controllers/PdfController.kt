package com.example.backend.controllers

import com.example.backend.models.Rent
import com.example.backend.services.DocumentService
import com.example.backend.services.PdfService
import com.example.backend.services.RentService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/pdf")
class PdfController(
  private val pdfService: PdfService,
  private val documentService: DocumentService,
) {

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/invoice/pdf")
  fun getInvoicePdf(): ResponseEntity<ByteArray> {
    val pdfBytes = pdfService.generateInvoicePdf()

    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
      .contentType(MediaType.APPLICATION_PDF)
      .body(pdfBytes)
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/invoice/download")
  fun downloadInvoicePdf(@RequestParam subscriptionId: Int): ResponseEntity<ByteArray> {
    val pdfBytes = pdfService.downloadInvoiced(subscriptionId.toLong())

    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
      .contentType(MediaType.APPLICATION_PDF)
      .body(pdfBytes)
  }

  @PostMapping("/upload")
  fun uploadPdf(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
    val content = file.bytes
    return if (documentService.savePdf(content)) {
      ResponseEntity.ok("PDF saved successfully.")
    } else {
      ResponseEntity.badRequest().body("Duplicate PDF. Not saved.")
    }
  }

  @GetMapping("/tenant/{tenantId}")
  fun listDocuments(@PathVariable tenantId: Long): List<Map<String, Any?>> {
    return pdfService.getDocumentsByTenant(tenantId)
  }

  @GetMapping("/{documentId}/download")
  fun downloadDocument(@PathVariable documentId: Long, response: HttpServletResponse) {
    val document = pdfService.downloadDocument(documentId)
    response.contentType = "application/pdf"
    response.setHeader("Content-Disposition", "attachment; filename=document_$documentId.pdf")
    response.outputStream.write(document!!)
  }

}
