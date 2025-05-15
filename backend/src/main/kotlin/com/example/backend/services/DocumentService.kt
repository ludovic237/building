package com.example.backend.services

import com.example.backend.repositories.DocumentRepository
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.time.LocalDate
import java.util.*

@Service
class DocumentService (private val documentRepository: DocumentRepository) {

  fun savePdf(content: ByteArray): Boolean {
    // Compute hash of the PDF content
    val hash = computeHash(content)

    // Check if the PDF already exists
    if (documentRepository.findByHash(hash) != null) {
      return false // PDF already exists
    }

    // Save the PDF
    val pdfDocument = com.example.backend.models.Document(hash = hash, content = content)
    documentRepository.save(pdfDocument)
    return true
  }

  private fun computeHash(content: ByteArray): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(content).joinToString("") { "%02x".format(it) }
  }
}
