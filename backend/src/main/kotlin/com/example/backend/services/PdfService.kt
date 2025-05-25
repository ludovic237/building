package com.example.backend.services

import com.example.backend.models.Payment
import com.example.backend.models.SubscriptionOptions
import com.example.backend.models.Tenant
import com.example.backend.repositories.*
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
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

      // Logo and Title
//      val logo =
//        Image(ImageDataFactory.create("C:\\IdeaProjects\\buildingGesh\\public\\images\\avatars\\avatar-1.png"))
//          .setWidth(50f)
//          .setHeight(50f)
//
//      document.add(logo)
      document.add(
        Paragraph("FACTURE DE SERVICES").setFontSize(20f).setBold().setTextAlignment(TextAlignment.RIGHT)
          .setTextAlignment(TextAlignment.RIGHT)
      )
      document.add(
        Paragraph("FACTURE : ${subscription.id}").setTextAlignment(TextAlignment.RIGHT)
          .setTextAlignment(TextAlignment.RIGHT)

      )
      document.add(
        Paragraph("DATE : ${subscription.createdDate}").setTextAlignment(TextAlignment.RIGHT)
          .setTextAlignment(TextAlignment.RIGHT)
      )
      document.add(Paragraph("\n"))

      // Fournisseur et Client Details
      val tableDetails = Table(floatArrayOf(1f, 1f)).useAllAvailableWidth()
      tableDetails.addCell(
        Cell().add(Paragraph("FOURNISSEUR DE SERVICES").setBold()).setBackgroundColor(ColorConstants.BLACK)
          .setFontColor(ColorConstants.WHITE)
      )
      tableDetails.addCell(
        Cell().add(Paragraph("CLIENT").setBold()).setBackgroundColor(ColorConstants.BLACK)
          .setFontColor(ColorConstants.WHITE)
      )
      tableDetails.addCell(Cell().add(Paragraph("François Durand\nHV Service\n210 avenue des Lys\n54 000 Nancy\nTéléphone : (33) 06 65 78 21 34\nFax : (33) 09 87 32 21\ninfo@hvservice.com")))
      tableDetails.addCell(Cell().add(Paragraph("Jean Dupont\nEntreprise SL\n107 avenue du Port\n13 000 Marseille\nTéléphone : (33) 11 55 66 77\nFax : (33) 22 55 65 79\ninfo@companylimitedinc.com")))
      document.add(tableDetails)
      document.add(Paragraph("\n"))

      // Demandes du Client
      document.add(
        Paragraph("DEMANDES DU CLIENT").setBold().setBackgroundColor(ColorConstants.BLACK)
          .setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER)
      )
      document.add(
        Paragraph("Lorem ipsum dolor sit amet, in porttitor. Donec laoreet nonummy augue. Suspendisse dui purus, scelerisque at, vulputate vitae, pretium mattis, nunc. Mauris eget neque at sem venenatis eleifend. Fusce est. Vivamus a tellus.").setTextAlignment(
          TextAlignment.JUSTIFIED
        )
      )
      document.add(Paragraph("\n"))

      // Tableau des Services
      val columnWidths = floatArrayOf(4f, 1f, 1f, 2f)
      val table = Table(columnWidths).useAllAvailableWidth()

      // En-têtes du tableau
      table.addHeaderCell(Cell().add(Paragraph("DESCRIPTION").setBold()))
      table.addHeaderCell(Cell().add(Paragraph("QTÉ").setBold()))
      table.addHeaderCell(Cell().add(Paragraph("PU").setBold()))
      table.addHeaderCell(Cell().add(Paragraph("MONTANT").setBold()))

      var grandTotal = BigDecimal.ZERO

      // Parcourir les services
      subscriptionServices.forEach { subscriptionService ->
        val numberOfSubscriptions = subscriptionService.quantity
        val servicePrice = subscriptionService.price ?: BigDecimal.ZERO
        var serviceTotal = servicePrice.multiply(numberOfSubscriptions.toBigDecimal())

        // Ligne pour le service
        table.addCell(Cell().add(Paragraph(subscriptionService.service!!.name).setBold()))
        table.addCell(Cell().add(Paragraph(numberOfSubscriptions.toString())))
        table.addCell(Cell().add(Paragraph(servicePrice.toString())))
        table.addCell(Cell().add(Paragraph(serviceTotal.toString())))

        // Parcourir les options du service
        val options = subscriptionOptions.filter { it.subscriptionService == subscriptionService }
        options.forEach { option ->
          val optionPrice = option.price ?: BigDecimal.ZERO
          val optionQuantity = option.quantity
          val optionTotal =
            optionPrice.multiply(optionQuantity.toBigDecimal()).multiply(numberOfSubscriptions.toBigDecimal())
          serviceTotal += optionTotal

          table.addCell(Cell().add(Paragraph("  - ${option.option?.name}")))
          table.addCell(Cell().add(Paragraph("${optionQuantity.toString()} * $numberOfSubscriptions")))
          table.addCell(Cell().add(Paragraph(optionPrice.toString())))
          table.addCell(Cell().add(Paragraph(optionTotal.toString())))
        }

        grandTotal += serviceTotal
      }


      // Sous-total, TVA et Total général
      val taxRate = BigDecimal("0.0")
//        val taxRate = BigDecimal("0.2")
      val tax = grandTotal.multiply(taxRate)
      val totalWithTax = grandTotal.add(tax)

      table.addCell(Cell(1, 3).add(Paragraph("SOUS-TOTAL").setBold()))
      table.addCell(Cell().add(Paragraph(grandTotal.toString()).setBold()))
      table.addCell(Cell(1, 3).add(Paragraph("TVA (0%)").setBold()))
//        table.addCell(Cell(1, 3).add(Paragraph("TVA (20%)").setBold()))
      table.addCell(Cell().add(Paragraph(tax.toString()).setBold()))
      table.addCell(Cell(1, 3).add(Paragraph("TOTAL").setBold()))
      table.addCell(Cell().add(Paragraph(totalWithTax.toString()).setBold().setFontSize(14f)))

      document.add(table)


      // Total Section
      document.add(Paragraph("\n"))
      val totalTable = Table(floatArrayOf(3f, 1f)).useAllAvailableWidth()
      totalTable.addCell(Cell().add(Paragraph("TOTAL HORS TAXE").setBold()))
      totalTable.addCell(Cell().add(Paragraph(grandTotal.toString())))
      totalTable.addCell(Cell().add(Paragraph("TAXE (0%)").setBold()))
      totalTable.addCell(Cell().add(Paragraph(tax.toString())))
      totalTable.addCell(Cell().add(Paragraph("TOTAL TTC").setBold()))
      totalTable.addCell(Cell().add(Paragraph(grandTotal.add(tax).toString()).setBold().setFontSize(14f)))
      document.add(totalTable)

      // Footer
      document.add(Paragraph("\nMERCI DE VOTRE CONFIANCE !").setBold().setTextAlignment(TextAlignment.CENTER))
      document.add(
        Paragraph("CONDITIONS GÉNÉRALES\nVeuillez effectuer le paiement par virement direct sur notre compte bancaire 10000-45856-222XX10 en indiquant le numéro de la facture.").setTextAlignment(
          TextAlignment.JUSTIFIED
        )
      )

      document.close()
    }

    return outputStream.toByteArray()
  }

}
