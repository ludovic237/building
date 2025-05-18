package com.example.backend.services

import com.example.backend.models.Document
import com.example.backend.repositories.DocumentRepository
import org.springframework.stereotype.Service
import java.security.MessageDigest
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
    val pdfDocument = Document(hash = hash, content = content)
    documentRepository.save(pdfDocument)
    return true
  }

  private fun computeHash(content: ByteArray): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(content).joinToString("") { "%02x".format(it) }
  }
}
