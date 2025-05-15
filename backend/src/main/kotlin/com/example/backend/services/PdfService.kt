package com.example.backend.services

import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.*

@Service
class PdfService {

  fun generateInvoicePdf(): ByteArray {
    val outputStream = ByteArrayOutputStream()

    PdfWriter(outputStream).use { writer ->
      val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(writer)
      val document = Document(pdfDocument)

      // Add Invoice Header
      document.add(Paragraph("Invoice").setFontSize(18f))
      document.add(Paragraph("Invoice Number: INV-001"))
      document.add(Paragraph("Date: ${LocalDate.now()}"))

      // Add Customer Details
      document.add(Paragraph("\nCustomer Details:"))
      document.add(Paragraph("Name: John Doe"))
      document.add(Paragraph("Address: 123 Main Street, City, Country"))
      document.add(Paragraph("Email: john.doe@example.com"))

      // Add Table of Items
      val columnWidths = floatArrayOf(4f, 2f, 2f, 2f)
      val table = Table(columnWidths)
      table.addCell("Item")
      table.addCell("Quantity")
      table.addCell("Unit Price")
      table.addCell("Total")

      table.addCell("Product A")
      table.addCell("2")
      table.addCell("$50")
      table.addCell("$100")

      table.addCell("Product B")
      table.addCell("1")
      table.addCell("$30")
      table.addCell("$30")

      table.addCell("Product C")
      table.addCell("3")
      table.addCell("$20")
      table.addCell("$60")

      document.add(table)

      // Add Total Amount
      document.add(Paragraph("\nTotal: $190"))

      document.close()
    }

    return outputStream.toByteArray()
  }
}
