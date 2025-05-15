package com.example.backend.repositories

  import com.example.backend.models.Document
  import com.itextpdf.kernel.pdf.PdfDocument
  import org.springframework.data.jpa.repository.JpaRepository


  interface DocumentRepository : JpaRepository<Document, Long> {

    fun findByHash(hash: String): Document?

  }
