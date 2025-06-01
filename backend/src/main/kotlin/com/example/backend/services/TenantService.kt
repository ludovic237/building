package com.example.backend.services

import com.example.backend.constants.PaymentTypeConstants
import com.example.backend.constants.StatusConstants
import com.example.backend.constants.StatusConstants.SERVICE_BILLING_MODEL_MONTHLY
import com.example.backend.constants.StatusConstants.SERVICE_BILLING_MODEL_YEARLY
import com.example.backend.constants.StatusConstants.SUBSCRIPTION_STATUS_ACTIVE
import com.example.backend.dtos.*
import com.example.backend.models.*
import com.example.backend.repositories.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.ZoneId
import java.util.*

@Service
class TenantService(
  private var authenticationManager: AuthenticationManager? = null,
  private val subscriptionServiceRepository: SubscriptionServiceRepository,
  private val paymentsViewRepository: PaymentsViewRepository,
  private val serviceOptionRepository: ServiceOptionRepository,
  private val subscriptionOptionRepository: SubscriptionOptionRepository,
  private val invoiceService: InvoiceService,
  private val tenantRepository: TenantRepository,
  private val paymentRepository: PaymentRepository,
  private val billingCycleRepository: BillingCycleRepository,
  private val subscriptionRepository: SubscriptionRepository,
  private val paymentLineRepository: PaymentLineRepository,
  private val serviceRepository: ServiceRepository,
  private val rentRepository: RentRepository,
  private val userRepository: UserRepository,
  private val housingUnitRepository: HoustingUnitRepository,
  val issueRepository: IssueRepository,
  val invoiceRepository: InvoiceRepository,
  private val userService: UserService,
) {

  fun getListTenantDetails(): List<TenantDetailsDTO> {
    return tenantRepository.findAll().map { tenant ->
      TenantDetailsDTO(
        tenantId = tenant.id!!,
        tenantName = "${tenant.user?.firstName} ${tenant.user?.lastName}",
        tenantEmail = tenant.user?.email ?: "N/A",
        housingUnitId = tenant.housingUnit?.id,
        housingUnitName = tenant.housingUnit?.number,
        moveInDate = tenant.moveInDate?.toString(),
        moveOutDate = tenant.moveOutDate?.toString(),
        securityDeposit = tenant.securityDeposit,
      )
    }
  }

  fun getAllTenants(): List<TenantDTO> {
    return tenantRepository.findAll().map { tenant ->
      var subscription = subscriptionRepository.findByTenant(tenantRepository.findByIdOrNull(tenant.id!!)!!)
        .filter { subscription ->
          subscriptionServiceRepository.findBySubscription(subscription)
            .any { subscriptionServices -> subscriptionServices.service?.name == "loyer" }
        }
      println("tenant")
      println(tenant)
      println(tenant.user)
      TenantDTO(
        id = tenant.id,
        userId = tenant.user?.id,
        paymentStatus = "",
        housingUnitId = tenant.housingUnit?.id,
        userName = tenant.user?.firstName + " " + tenant.user?.lastName,
        userEmail = tenant.user?.email,
        housingUnitName = tenant.housingUnit?.number,
        moveInDate = tenant.moveInDate,
        moveOutDate = tenant.moveOutDate,
        securityDeposit = tenant.securityDeposit,
//        status = tenant.status,
        houstinUnitPrice = tenant.houstingPrice,
      )
    }
  }

  fun getTenantById(id: Long): Optional<Tenant> {
    return tenantRepository.findById(id)
  }

  fun createTenant(tenantCreate: TenantCreateDTO): Tenant {
    // Create a new Tenant object
    val tenant = Tenant()

    // Map fields from TenantCreateDTO to Tenant
    tenant.moveInDate = tenantCreate.moveInDate
    tenant.moveOutDate = tenantCreate.moveOutDate
    tenant.securityDeposit = tenantCreate.securityDeposit
//    tenant.status = tenantCreate.status

    // Fetch and set the associated User
    val user = userRepository.findById(tenantCreate.userId.toLong())
      .orElseThrow { IllegalArgumentException("User not found with ID: ${tenantCreate.userId}") }
    tenant.user = user

    // Fetch and set the associated Housing Unit
    val housingUnit = housingUnitRepository.findById(tenantCreate.housingUnitId.toLong())
      .orElseThrow { IllegalArgumentException("Housing Unit not found with ID: ${tenantCreate.housingUnitId}") }
    tenant.housingUnit = housingUnit

    // Save the Tenant
    val savedTenant = tenantRepository.save(tenant)

    // Create a new Rent object
    val rent = Rent()
    rent.tenant = savedTenant
    rent.amount = tenantCreate.securityDeposit // Assuming `price` is a field in HousingUnit
    rent.houstingPrice = housingUnit.price
    rent.month = tenantCreate.moveInDate?.monthValue
    rent.year = tenantCreate.moveInDate?.year

    // Check if the deposited amount equals the housing unit price
    rent.status = if (tenantCreate.securityDeposit == housingUnit.price) "Complete" else "Pending"
    rent.paymentDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() // No payment yet

    // Save the Rent
    rentRepository.save(rent)

    return savedTenant
  }

  fun createTenantNew(tenantData: TenantCreateDataDTO): Tenant {
    tenantData.startDate = tenantData.startDate.plusDays(1)
    var remainingAmount = tenantData.securityDeposit
    val service = serviceRepository.findById(tenantData.serviceId)
      .orElseThrow { IllegalArgumentException("Service not found with ID: ${tenantData.serviceId}") }

    val user = userRepository.findById(tenantData.userId)
      .orElseThrow { IllegalArgumentException("User not found with ID: ${tenantData.userId}") }
    var currentUser = userService.getCurrentUser()

    val currentYear = Year.now().value
    val today = LocalDate.now()
    val currentMonth = String.format("%02d", today.monthValue)
    val currentDay = String.format("%02d", today.dayOfMonth)

    var amountTotal = tenantData.logementBasePrice * tenantData.numberOfSubscription.toBigDecimal()


    // Step 1: Create Tenant
    var tenant = Tenant()
    tenant.housingUnit = housingUnitRepository.findById(tenantData.housingUnitId).get()
    tenant.user = userRepository.findById(tenantData.userId).get()
    tenant.moveInDate = tenantData.startDate
    tenant.moveOutDate = tenantData.startDate.plusMonths(tenantData.numberOfSubscription.toLong())
    tenant.securityDeposit = tenantData.securityDeposit
    tenant.createdDate = LocalDateTime.now()
    tenant = tenantRepository.save(tenant)

    var saveInvoice = Invoice().apply {
      type = "subscription"
      month = currentMonth.toInt()
      this.modify = currentUser
      this.user = user
      this.tenantId = tenant.id
      year = currentYear
      amount = amountTotal
      paymentDate = LocalDateTime.now()
      createdDate = LocalDateTime.now()
      this.status = when {
        tenantData.securityDeposit == amountTotal -> StatusConstants.INVOICE_STATUS_PAID
        tenantData.securityDeposit < amountTotal -> StatusConstants.INVOICE_STATUS_PARTIAL_PAID
        else -> StatusConstants.INVOICE_STATUS_PENDING
      }
      number = invoiceService.generateInvoiceNumber()
    }
    saveInvoice = invoiceRepository.save(saveInvoice)

    val housingUnit = housingUnitRepository.findById(tenantData.housingUnitId)
      .orElseThrow { IllegalArgumentException("Housing Unit not found with ID: ${tenantData.housingUnitId}") }
    housingUnit.tenant = tenant
    housingUnit.createdDate = LocalDateTime.now()
    housingUnitRepository.save(housingUnit)

    // Step 2: Create Subscription
    val subscription = Subscription()
    subscription.totalPrice = amountTotal
    subscription.startDate = tenantData.startDate
    subscription.endDate = tenantData.startDate.plusMonths(tenantData.numberOfSubscription.toLong())
    subscription.tenant = tenant
    subscription.modifyBy = currentUser
    subscription.invoice = saveInvoice
    subscription.subscriptNumber = tenantData.numberOfSubscription
    subscription.status = SUBSCRIPTION_STATUS_ACTIVE
    subscription.createdDate = LocalDateTime.now()
    subscription.updatedDate = LocalDateTime.now()
    val savedSubscription = subscriptionRepository.save(subscription)

// Create associated subscription services

    var serviceData = serviceRepository.findById(tenantData.serviceId)
      .orElseThrow { IllegalArgumentException("Service not found with ID: ${tenantData.serviceId}") }

    val subscriptionServices = SubscriptionServices()
    subscriptionServices.subscription = savedSubscription
//    subscriptionServices.billingCycle = savedBillingCycle
    subscriptionServices.service = serviceData
    subscriptionServices.totalPrice = amountTotal
    subscriptionServices.amountDue = amountTotal
    subscriptionServices.subscriptNumber = tenantData.numberOfSubscription
    subscriptionServices.quantity = tenantData.numberOfSubscription
    subscriptionServices.price = tenantData.logementBasePrice
    subscriptionServices.startDate = tenantData.startDate
    subscriptionServices.endDate = tenantData.startDate.plusMonths(tenantData.numberOfSubscription.toLong())
    subscriptionServices.createdDate = LocalDateTime.now()
    subscriptionServices.updatedDate = LocalDateTime.now()
    val savedSubscriptionService = subscriptionServiceRepository.save(subscriptionServices)

    var currentStartDate = tenantData.startDate

    val payment = Payment().apply {
      this.tenant = tenant
      this.totalAmount = tenantData.securityDeposit
      this.paymentMethod = when (tenantData.paymentMode.uppercase()) {
        PaymentTypeConstants.PAYMENT_METHOD_CASH -> PaymentTypeConstants.PAYMENT_METHOD_CASH
        PaymentTypeConstants.PAYMENT_METHOD_CREDIT_CARD -> PaymentTypeConstants.PAYMENT_METHOD_CREDIT_CARD
        PaymentTypeConstants.PAYMENT_METHOD_BANK_TRANSFER -> PaymentTypeConstants.PAYMENT_METHOD_BANK_TRANSFER
        PaymentTypeConstants.PAYMENT_METHOD_CHECK -> PaymentTypeConstants.PAYMENT_METHOD_CHECK
        PaymentTypeConstants.PAYMENT_METHOD_MOBILE_PAYMENT -> PaymentTypeConstants.PAYMENT_METHOD_MOBILE_PAYMENT
        else -> "OTHER"
      }
      this.paymentDate = LocalDateTime.now()
      this.createdDate = LocalDateTime.now()
      this.updatedDate = LocalDateTime.now()
    }
    val savedPayment = paymentRepository.save(payment)

    for (i in 1..tenantData.numberOfSubscription) {

      val currentEndDate = when (savedSubscriptionService.service?.billingMode?.lowercase()) {
        SERVICE_BILLING_MODEL_MONTHLY -> currentStartDate?.plusMonths(1)
        SERVICE_BILLING_MODEL_YEARLY -> currentStartDate?.plusYears(1)
        else -> throw IllegalArgumentException("Unsupported billing mode")
      }

      val billingPrice = savedSubscriptionService.price

      val amountToPay = amountTotal.min(billingPrice)
      val amountPay = remainingAmount.min(billingPrice)

      // Create billing cycle
      val billingCycle = BillingCycle().apply {
        this.subscription = savedSubscription
        this.subscriptionServices = savedSubscriptionService
        this.periodStart = currentStartDate
        this.periodEnd = currentEndDate
        this.amountDue = amountToPay
        this.status = when {
          amountPay == amountToPay -> StatusConstants.INVOICE_STATUS_PAID
          amountPay < amountToPay && amountPay >= BigDecimal.ZERO -> StatusConstants.INVOICE_STATUS_PARTIAL_PAID
          else -> StatusConstants.INVOICE_STATUS_PENDING
        }
        this.createdDate = LocalDateTime.now()
        this.updatedDate = LocalDateTime.now()
      }
      val savedBillingCycle = billingCycleRepository.save(billingCycle)


      // Create payment line
      if (amountPay > BigDecimal.ZERO) {
        val paymentLine = PaymentLine().apply {
          this.billingCycle = savedBillingCycle
          this.payment = savedPayment
          this.amountPaid = amountPay
          this.createdDate = LocalDateTime.now()
          this.updatedDate = LocalDateTime.now()
        }
        paymentLineRepository.save(paymentLine)
      }

      remainingAmount -= amountToPay
      currentStartDate = currentEndDate
    }
    // Calculate end date based on billing mode
    val totalSubscriptions = subscription.subscriptNumber ?: 0
    val calculatedEndDate = when (service.billingMode!!.lowercase()) {
      SERVICE_BILLING_MODEL_MONTHLY -> subscription.startDate?.plusMonths(totalSubscriptions.toLong())
      SERVICE_BILLING_MODEL_YEARLY -> subscription.startDate?.plusYears(totalSubscriptions.toLong())
      else -> throw IllegalArgumentException("Unsupported billing mode")
    }

    savedSubscription.endDate = calculatedEndDate
    tenant.moveOutDate = calculatedEndDate
    tenant = tenantRepository.save(tenant)
    subscriptionRepository.save(savedSubscription)

    return tenant
  }

  fun createPayment(tenant: Tenant?, depositAmount: BigDecimal, paymentMode: String): Payment {
    val payment = Payment()
    payment.createdDate = LocalDateTime.now()
    payment.tenant = tenant
    payment.totalAmount = depositAmount
    payment.paymentMethod = when (paymentMode.uppercase()) {
      PaymentTypeConstants.PAYMENT_METHOD_CASH -> PaymentTypeConstants.PAYMENT_METHOD_CASH
      PaymentTypeConstants.PAYMENT_METHOD_CREDIT_CARD -> PaymentTypeConstants.PAYMENT_METHOD_CREDIT_CARD
      PaymentTypeConstants.PAYMENT_METHOD_BANK_TRANSFER -> PaymentTypeConstants.PAYMENT_METHOD_BANK_TRANSFER
      PaymentTypeConstants.PAYMENT_METHOD_CHECK -> PaymentTypeConstants.PAYMENT_METHOD_CHECK
      PaymentTypeConstants.PAYMENT_METHOD_MOBILE_PAYMENT -> PaymentTypeConstants.PAYMENT_METHOD_MOBILE_PAYMENT
      else -> "OTHER"
    }
    payment.paymentDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    return paymentRepository.save(payment)
  }

  fun createPaymentLine(paymentId: Long?, billingCycleId: Long, remainingAmount: BigDecimal): PaymentLine {
    val paymentLine = PaymentLine()
    paymentLine.payment = paymentRepository.findById(paymentId!!).get()
    paymentLine.billingCycle = billingCycleRepository.findById(billingCycleId).get()
    paymentLine.amountPaid = remainingAmount
    paymentLine.createdDate = LocalDateTime.now()
    return paymentLineRepository.save(paymentLine)
  }

  fun updateTenant(id: Long, updatedTenant: Tenant): Tenant {
    val existingTenant =
      tenantRepository.findById(id).orElseThrow { IllegalArgumentException("Tenant with ID $id not found") }

    existingTenant.user!!.firstName = updatedTenant.user!!.lastName
    existingTenant.user!!.lastName = updatedTenant.user!!.firstName
    existingTenant.user!!.email = updatedTenant.user!!.email
    existingTenant.updatedDate = LocalDateTime.now()
    // Update other fields as necessary

    return tenantRepository.save(existingTenant)
  }

  fun deleteTenant(id: Long) {
    if (!tenantRepository.existsById(id)) {
      throw IllegalArgumentException("Tenant with ID $id not found")
    }
    tenantRepository.deleteById(id)
  }

  fun getTenantInformation(tenant: Tenant): Map<String, Any?> {
    return mapOf(
      "fullName" to "${tenant.user?.firstName ?: "Unknown"} ${tenant.user?.lastName ?: "Unknown"}",
      "email" to tenant.user?.email,
      "phone" to tenant.user?.phone,
      "username" to tenant.user?.username,
      "gender" to tenant.user?.gender,
      "birthday" to tenant.user?.birthday,
      "housingUnit" to mapOf(
        "id" to tenant.housingUnit?.id,
        "number" to tenant.housingUnit?.number,
        "address" to tenant.housingUnit?.address,
        "type" to tenant.housingUnit?.type,
        "floor" to tenant.housingUnit?.floor,
        "area" to tenant.housingUnit?.area
      ),
      "moveInDate" to tenant.moveInDate,
      "moveOutDate" to tenant.moveOutDate,
      "securityDeposit" to tenant.securityDeposit,
      "monthlyRent" to tenant.houstingPrice
    )
  }

  fun getFinancialInformation(tenant: Tenant): Map<String, Any?> {
    val subscriptions = subscriptionRepository.findByTenant(tenant).orEmpty()
    val billingCycles = subscriptions.flatMap { subscription ->
      billingCycleRepository.findBySubscription(subscription).orEmpty()
    }
    val paidBillingCycles = billingCycles.filter { it.status == StatusConstants.BILLING_CYCLE_STATUS_PAID }
    val unpaidBillingCycles = billingCycles.filter { it.status != StatusConstants.BILLING_CYCLE_STATUS_PAID }

    return mapOf("billingCycles" to billingCycles.map { cycle ->
      mapOf(
        "id" to cycle.id,
        "amountDue" to cycle.amountDue,
        "periodStart" to cycle.periodStart,
        "periodEnd" to cycle.periodEnd,
        "status" to cycle.status
      )
    },
      "subscriptions" to subscriptions.map { subscription ->
        val subscriptionServices =
          subscriptionServiceRepository.findBySubscription(subscription).map { subscriptionService ->
            mapOf(
              "id" to subscriptionService.id,
              "serviceName" to subscriptionService.service?.name,
              "quantity" to subscriptionService.quantity,
              "price" to subscriptionService.price,
              "startDate" to subscriptionService.startDate,
              "endDate" to subscriptionService.endDate
            )
          }

        mapOf(
          "id" to subscription.id,
          "price" to subscription.totalPrice,
          "startDate" to subscription.startDate,
          "endDate" to subscription.endDate,
          "status" to subscription.status,
          "subscriptionServices" to subscriptionServices
        )
      },
      "totalPayments" to billingCycles.size,
      "completedPayments" to paidBillingCycles.size,
      "remainingPayments" to unpaidBillingCycles.size,
      "amountPaid" to paidBillingCycles.sumOf { it.amountDue ?: BigDecimal.ZERO },
      "outstandingDebt" to unpaidBillingCycles.sumOf { it.amountDue ?: BigDecimal.ZERO })
  }

  fun getIssueTracking(tenant: Tenant): List<Map<String, Any?>> {
    val issues = issueRepository.findByTenant(tenant).orEmpty()
    return issues.map { issue ->
      mapOf(
        "id" to issue.id,
        "title" to issue.title,
        "description" to issue.description,
        "declarationDate" to issue.declarationDate,
        "status" to issue.status
      )
    }
  }

  fun getAdditionalInformation(tenant: Tenant): Map<String, Any?> {
    val invoices = invoiceRepository.findByUser(tenant.user!!).orEmpty()
    return mapOf(
      "invoices" to invoices.map { invoice ->
        mapOf(
          "id" to invoice.id,
          "type" to invoice.type,
          "month" to invoice.month,
          "year" to invoice.year,
          "amount" to invoice.amount,
          "paymentDate" to invoice.paymentDate,
          "status" to invoice.status
        )
      }, "userActivity" to mapOf(
        "registrationDate" to tenant.user?.registrationDate,
        "accountStatus" to if (tenant.user?.isActive == true) "Active" else "Inactive"
      )
    )
  }

  fun getAllTenantDetails(tenant: Tenant): Map<String, Any?> {
    return mapOf(
      "tenantInformation" to getTenantInformation(tenant),
      "financialInformation" to getFinancialInformation(tenant),
      "issueTracking" to getIssueTracking(tenant),
      "additionalInformation" to getAdditionalInformation(tenant)
    )
  }

  fun getTenantDetailsAsMap(tenantId: Long): Map<String, Any?> {
    val tenant = tenantRepository.findById(tenantId).orElseThrow { IllegalArgumentException("Tenant not found") }

    val subscriptions = subscriptionRepository.findByTenant(tenant)
      .map { subscription ->
        val subscriptionServices = subscriptionServiceRepository.findBySubscription(subscription)
        val subscribedOptions = subscriptionServices
          .flatMap { subscriptionService ->
            subscriptionOptionRepository.findBySubscriptionService(subscriptionService)
              .map { subscriptionOption ->
                mutableMapOf(
                  "id" to subscriptionOption.id as Any,
                  "name" to (subscriptionOption.option?.name ?: "Unknown") as Any,
                  "price" to (subscriptionOption.option?.price ?: BigDecimal.ZERO) as Any
                )
              }
          }

        val totalSubscriptions = subscription.subscriptNumber ?: 0
        val completedSubscriptions = billingCycleRepository.findBySubscription(subscription)
          .count { it.status == StatusConstants.BILLING_CYCLE_STATUS_PAID }
        val remainingSubscriptions = totalSubscriptions - completedSubscriptions
        val remainingAmount = BigDecimal(remainingSubscriptions) * (subscription.totalPrice ?: BigDecimal.ZERO)

        mapOf(
          "id" to subscription.id,
          "price" to subscription.totalPrice,
          "startDate" to subscription.startDate,
          "endDate" to subscription.endDate,
          "status" to subscription.status,
          "subscriptionServices" to subscriptionServices.map { subscriptionService ->
            mapOf(
              "id" to subscriptionService.id,
              "serviceName" to subscriptionService.service?.name,
              "quantity" to subscriptionService.quantity,
              "price" to subscriptionService.price,
              "startDate" to subscriptionService.startDate,
              "endDate" to subscriptionService.endDate
            )
          },
          "totalSubscriptions" to totalSubscriptions,
          "remainingSubscriptions" to remainingSubscriptions,
          "remainingAmount" to remainingAmount
        )
      }

    val subscriptionsSimple = subscriptionRepository.findByTenant(tenant)

    val payments = paymentsViewRepository.findByTenantId(tenantId).map { payment ->
      mapOf(
        "id" to payment.paymentId,
        "amountPaid" to payment.amountPaid,
        "paymentDate" to payment.paymentDate,
        "serviceName" to payment.serviceName,
        "serviceDescription" to payment.serviceDescription,
        "paymentMethod" to payment.paymentMethod,
        "billingCycleStatus" to payment.billingCycleStatus,
      )
    }

    val paymentsData = paymentRepository.findByTenantId(tenantId)
    val paymentLines = paymentLineRepository.findAllByPaymentIn(paymentsData)
    val totalAlreadyPaid = paymentLines.sumOf { it.amountPaid ?: BigDecimal.ZERO }

    val subscriptionsData = subscriptionRepository.findByTenant(tenantRepository.findByIdOrNull(tenantId)!!)
    val totalSubscription =
      subscriptionsData.sumOf { BigDecimal(it.subscriptNumber ?: 0) * (it.totalPrice ?: BigDecimal.ZERO) }

    val totalRemaining = totalSubscription - totalAlreadyPaid

    val totalPayments = subscriptionsData.sumOf { it.subscriptNumber ?: 0 }

    val completedPayments = paymentLines.count { it.billingCycle?.status == StatusConstants.BILLING_CYCLE_STATUS_PAID }
    val partialPayments =
      paymentLines.count { it.billingCycle?.status == StatusConstants.BILLING_CYCLE_STATUS_PARTIAL_PAID }
    val donePayments = completedPayments + partialPayments
    val remainingPayments = totalPayments - completedPayments

    val paymentSummary = mapOf(
      "totalPaid" to totalAlreadyPaid,
      "totalDue" to totalSubscription,
      "totalRemaining" to totalRemaining,
      "totalPayments" to totalPayments,
      "donePayments" to donePayments,
      "completedPayments" to completedPayments,
      "partialPayments" to partialPayments,
      "remainingPayments" to remainingPayments
    )

    val billingCycles = subscriptionsSimple.flatMap { subscription ->
      val subscriptionEntity = subscription // Explicitly cast to Subscription
      billingCycleRepository.findBySubscription(subscriptionEntity).orEmpty().map { cycle ->
        mapOf(
          "id" to cycle.id,
//          "serviceId" to cycle.subscription?.service?.id,
//          "serviceName" to cycle.subscription?.service?.name,
          "amountDue" to cycle.amountDue,
          "periodStart" to cycle.periodStart,
          "periodEnd" to cycle.periodEnd,
          "status" to cycle.status
        )
      }
    }

    val financialSummary = mapOf("totalPayments" to billingCycles.size,
      "completedPayments" to billingCycles.count { it["status"] == StatusConstants.BILLING_CYCLE_STATUS_PAID },
      "remainingPayments" to billingCycles.count { it["status"] != StatusConstants.BILLING_CYCLE_STATUS_PAID },
      "amountPaid" to billingCycles.filter { it["status"] == StatusConstants.BILLING_CYCLE_STATUS_PAID }
        .sumOf { it["amountDue"] as BigDecimal },
      "outstandingDebt" to billingCycles.filter { it["status"] != StatusConstants.BILLING_CYCLE_STATUS_PAID }
        .sumOf { it["amountDue"] as BigDecimal })

    val issues = issueRepository.findByTenant(tenant).map { issue ->
      mapOf(
        "id" to issue.id,
        "title" to issue.title,
        "description" to issue.description,
        "declarationDate" to issue.declarationDate,
        "status" to issue.status
      )
    }

    val invoices = invoiceRepository.findByUser(tenant.user!!).map { invoice ->
      mapOf(
        "id" to invoice.id,
        "type" to invoice.type,
        "month" to invoice.month,
        "year" to invoice.year,
        "amount" to invoice.amount,
        "paymentDate" to invoice.paymentDate,
        "status" to invoice.status
      )
    }

    val userActivity = mapOf(
      "registrationDate" to tenant.user?.registrationDate,
      "accountStatus" to if (tenant.user?.isActive == true) "Active" else "Inactive"
    )

    val subscribedServices = subscriptionRepository.findByTenant(tenant)
      .flatMap { subscription ->
        subscriptionServiceRepository.findBySubscription(subscription)
          .map { it.service?.id }
      }

    val subscribedOptions = subscriptionRepository.findByTenant(tenant)
      .flatMap { subscription ->
        subscriptionServiceRepository.findBySubscription(subscription)
          .flatMap { subscriptionService ->
            subscriptionOptionRepository.findBySubscriptionService(subscriptionService)
              .map { it.option?.id }
          }
      }

    val services = serviceRepository.findAll().map { service ->
      val serviceOptions = serviceOptionRepository.findByService(service).map { option ->
        mapOf(
          "id" to option.id,
          "name" to option.name,
          "price" to option.price,
          "isSubscribed" to (option.id in subscribedOptions)
        )
      }

      mapOf(
        "id" to service.id,
        "name" to service.name,
        "price" to service.billingMode,
        "billingMode" to service.billingMode,
        "isSubscribed" to (service.id in subscribedServices),
        "options" to serviceOptions
      )
    }

    return mapOf(
      "tenant" to mapOf(
        "id" to tenant.id,
        "fullName" to "${tenant.user?.firstName} ${tenant.user?.lastName}",
        "email" to tenant.user?.email,
        "phone" to tenant.user?.phone,
        "username" to tenant.user?.username,
        "gender" to tenant.user?.gender,
        "birthday" to tenant.user?.birthday,
        "housingUnit" to mapOf(
          "id" to tenant.housingUnit?.id,
          "number" to tenant.housingUnit?.number,
          "address" to tenant.housingUnit?.address,
          "type" to tenant.housingUnit?.type,
          "floor" to tenant.housingUnit?.floor,
          "area" to tenant.housingUnit?.area
        ),
        "moveInDate" to tenant.moveInDate,
        "moveOutDate" to tenant.moveOutDate,
        "securityDeposit" to tenant.securityDeposit,
        "monthlyRent" to tenant.houstingPrice
      ),
      "subscriptions" to subscriptions,
      "payments" to payments,
      "billingCycles" to billingCycles,
      "paymentSummary" to paymentSummary,
      "financialSummary" to financialSummary,
      "issues" to issues,
      "invoices" to invoices,
      "userActivity" to userActivity,
      "services" to services
    )
  }

}
