package com.example.backend.services

import com.example.backend.models.Payment
import com.example.backend.models.SubscriptionOptions
import com.example.backend.models.Tenant
import com.example.backend.repositories.*
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@Service
class PdfService(
  private val subscriptionRepository: SubscriptionRepository,
  private val subscriptionServiceRepository: SubscriptionServiceRepository,
  private val subscriptionOptionRepository: SubscriptionOptionRepository,
  private val invoiceRepository: InvoiceRepository,
  private val invoiceCounterRepository: InvoiceCounterRepository,
  private val documentRepository: DocumentRepository,
  private val userRepository: UserRepository,
) {

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

  fun getDocumentsByTenant(userId: Long): List<Map<String, Any?>> {
    val user = userRepository.findById(userId).get()
    return documentRepository.findByUser(user)!!.map { document ->
      mapOf(
        "id" to document.id,
        "fileName" to document.name,
        "createdDate" to document.createdDate
      )
    }
  }

  fun downloadDocument(documentId: Long): ByteArray? {
    val document = documentRepository.findById(documentId)
      .orElseThrow { IllegalArgumentException("Document not found with ID $documentId") }
    return document.content
  }

  fun generateReceiptAndSave(
    tenant: Tenant,
    subscriptionsWithOptions: List<Map<String, Any>>,
    payment: Payment
  ): com.example.backend.models.Document {
    val outputStream = com.itextpdf.io.source.ByteArrayOutputStream()

    PdfWriter(outputStream).use { writer ->
      val pdfDocument = PdfDocument(writer)
      val document = com.itextpdf.layout.Document(pdfDocument)

      // Add receipt header
      document.add(Paragraph("Payment Receipt").setFontSize(18f))
      document.add(Paragraph("Date: ${payment.paymentDate}"))
      document.add(Paragraph("Receipt Number: ${payment.id}"))

      // Add tenant details
      document.add(Paragraph("\nTenant Details:"))
      document.add(Paragraph("Name: ${tenant.user?.firstName} ${tenant.user?.lastName}"))
      document.add(Paragraph("Email: ${tenant.user?.email}"))

      // Add subscriptions and options
      document.add(Paragraph("\nSubscriptions and Options:"))
      val columnWidths = floatArrayOf(4f, 2f, 2f)
      val table = Table(columnWidths)
      table.addCell("Subscription/Option")
      table.addCell("Quantity")
      table.addCell("Price")

      var totalAmount = BigDecimal.ZERO
      subscriptionsWithOptions.forEach { subscription ->
        table.addCell(subscription["subscriptionName"].toString())
        table.addCell("-")
        table.addCell(subscription["subscriptionPrice"].toString())
        totalAmount += subscription["subscriptionPrice"] as BigDecimal

        val options = subscription["options"] as List<Map<String, Any?>>
        options.forEach { option ->
          table.addCell("  - ${option["optionName"]}")
          table.addCell(option["quantity"].toString())
          table.addCell(option["price"].toString())
          totalAmount += (option["price"] as BigDecimal).multiply((option["quantity"] as Int).toBigDecimal())
        }
      }
      document.add(table)

      // Add payment details
      val remainingAmount = totalAmount - payment.totalAmount!!
      document.add(Paragraph("\nPayment Details:"))
      document.add(Paragraph("Total Amount: $${totalAmount}"))
      document.add(Paragraph("Amount Paid: $${payment.totalAmount}"))
      document.add(Paragraph("Remaining Amount: $${remainingAmount}"))
      document.add(Paragraph("Payment Method: ${payment.paymentMethod}"))

      document.close()
    }

    // Save the receipt in the database
    val receiptData = outputStream.toByteArray()
    return documentRepository.save(
      com.example.backend.models.Document(
        user = tenant.user!!,
        name = "receipt_${payment.id}.pdf",
        content = receiptData
      )
    )
  }

  fun downloadReceipt(
    tenant: Tenant,
    subscriptionsWithOptions: List<Map<String, Any>>,
    payment: Payment
  ): ByteArray {
    val outputStream = com.itextpdf.io.source.ByteArrayOutputStream()

    PdfWriter(outputStream).use { writer ->
      val pdfDocument = PdfDocument(writer)
      val document = com.itextpdf.layout.Document(pdfDocument)

      // Add receipt header
      document.add(Paragraph("Payment Receipt").setFontSize(18f))
      document.add(Paragraph("Date: ${payment.paymentDate}"))
      document.add(Paragraph("Receipt Number: ${payment.id}"))

      // Add tenant details
      document.add(Paragraph("\nTenant Details:"))
      document.add(Paragraph("Name: ${tenant.user?.firstName} ${tenant.user?.lastName}"))
      document.add(Paragraph("Email: ${tenant.user?.email}"))

      // Add subscriptions and options
      document.add(Paragraph("\nSubscriptions and Options:"))
      val columnWidths = floatArrayOf(4f, 2f, 2f)
      val table = Table(columnWidths)
      table.addCell("Subscription/Option")
      table.addCell("Quantity")
      table.addCell("Price")

      var totalAmount = BigDecimal.ZERO
      subscriptionsWithOptions.forEach { subscription ->
        table.addCell(subscription["subscriptionName"].toString())
        table.addCell("-")
        table.addCell(subscription["subscriptionPrice"].toString())
        totalAmount += subscription["subscriptionPrice"] as BigDecimal

        val options = subscription["options"] as List<Map<String, Any?>>
        options.forEach { option ->
          table.addCell("  - ${option["optionName"]}")
          table.addCell(option["quantity"].toString())
          table.addCell(option["price"].toString())
          totalAmount += (option["price"] as BigDecimal).multiply((option["quantity"] as Int).toBigDecimal())
        }
      }
      document.add(table)

      // Add payment details
      val remainingAmount = totalAmount - payment.totalAmount!!
      document.add(Paragraph("\nPayment Details:"))
      document.add(Paragraph("Total Amount: $${totalAmount}"))
      document.add(Paragraph("Amount Paid: $${payment.totalAmount}"))
      document.add(Paragraph("Remaining Amount: $${remainingAmount}"))
      document.add(Paragraph("Payment Method: ${payment.paymentMethod}"))

      document.close()
    }

    // Save the receipt in the database
    val receiptData = outputStream.toByteArray()
    return receiptData
  }


  fun downloadInvoiced(subscriptionId: Long): ByteArray {
    val subscription = subscriptionRepository.findById(subscriptionId)
      .orElseThrow { IllegalArgumentException("Subscription not found with ID $subscriptionId") }

    val tenant = subscription.tenant
      ?: throw IllegalArgumentException("Tenant not found for subscription ID $subscriptionId")

    val subscriptionServices = subscriptionServiceRepository.findBySubscription(subscription)
    val subscriptionOptions = subscriptionServices.flatMap { subscriptionService ->
      subscriptionOptionRepository.findBySubscriptionService(subscriptionService)
    }

    val outputStream = com.itextpdf.io.source.ByteArrayOutputStream()

    PdfWriter(outputStream).use { writer ->
      val pdfDocument = PdfDocument(writer)
      val document = com.itextpdf.layout.Document(pdfDocument)

      // Add receipt header
      document.add(Paragraph("Payment Receipt").setFontSize(18f))
      document.add(Paragraph("Date: ${subscription.createdDate}"))
      document.add(Paragraph("Subscription ID: ${subscription.id}"))

      // Add tenant details
      document.add(Paragraph("\nTenant Details:"))
      document.add(Paragraph("Name: ${tenant.user?.firstName} ${tenant.user?.lastName}"))
      document.add(Paragraph("Email: ${tenant.user?.email}"))

      // Add subscription and options
      document.add(Paragraph("\nSubscription Details:"))
      val columnWidths = floatArrayOf(4f, 2f, 2f)
      val table = Table(columnWidths)
      table.addCell("Subscription/Option")
      table.addCell("Quantity")
      table.addCell("Price")

      var totalAmount = subscription.totalPrice ?: BigDecimal.ZERO
      subscriptionServices.forEach { subscriptionService ->
        table.addCell(subscriptionService.service!!.name)
        table.addCell(subscriptionService.quantity.toString())
        table.addCell(subscriptionService.price.toString())
        totalAmount += subscriptionService.price!!
      }
      subscriptionOptions.forEach { option: SubscriptionOptions ->
        table.addCell("  - ${option.option?.name}")
        table.addCell(option.quantity.toString())
        table.addCell(option.price.toString())
        totalAmount += option.price!!
      }
      document.add(table)

      // Add total amount
      document.add(Paragraph("\nTotal Amount: $${totalAmount}"))
      document.add(Paragraph("Status: ${subscription.status}"))

      document.close()
    }

    return outputStream.toByteArray()
  }
}
