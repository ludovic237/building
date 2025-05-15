package com.example.backend.services

import com.example.backend.constants.PaymentTypeConstants
import com.example.backend.constants.StatusConstants
import com.example.backend.dtos.*
import com.example.backend.models.*
import com.example.backend.repositories.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
class TenantService(
  private val paymentsViewRepository: PaymentsViewRepository,
  private val serviceOptionRepository: ServiceOptionRepository,
  private val subscriptionOptionRepository: SubscriptionOptionRepository,
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
        .filter { subscription -> subscription.service?.name == "loyer" }
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
    val service = serviceRepository.findById(tenantData.serviceId)
      .orElseThrow { IllegalArgumentException("Service not found with ID: ${tenantData.serviceId}") }

    // Step 1: Create Tenant
    var tenant = Tenant()
    tenant.housingUnit = housingUnitRepository.findById(tenantData.housingUnitId).get()
    tenant.user = userRepository.findById(tenantData.userId).get()
    tenant.moveInDate = tenantData.startDate
    tenant.securityDeposit = tenantData.securityDeposit
    tenant.createdDate = LocalDateTime.now()
    tenant = tenantRepository.save(tenant)

    val housingUnit = housingUnitRepository.findById(tenantData.housingUnitId)
      .orElseThrow { IllegalArgumentException("Housing Unit not found with ID: ${tenantData.housingUnitId}") }
    housingUnit.tenant = tenant
    housingUnit.createdDate = LocalDateTime.now()
    housingUnitRepository.save(housingUnit)

    // Step 2: Create Subscription
    val subscription = Subscription()
    subscription.service = service
    subscription.price = tenantData.logementBasePrice
    subscription.startDate = tenantData.startDate
    subscription.tenant = tenant
    subscription.subscriptNumber = tenantData.numberOfSubscription
    subscription.status = "Active"
    subscription.createdDate = LocalDateTime.now()
    subscriptionRepository.save(subscription)

    // Step 3: Calculate billing cycles
    val logementBasePrice = tenantData.logementBasePrice
    val totalCycles = tenantData.securityDeposit / logementBasePrice
    val fullCycles = totalCycles.toInt()
    val partialCycleAmount = tenantData.securityDeposit % logementBasePrice
    val billingCycles = mutableListOf<BillingCycle>()

    var currentStartDate = tenantData.startDate
    var remainingDeposit = tenantData.securityDeposit

    val payment = createPayment(
      tenant = tenant,
      depositAmount = tenantData.securityDeposit,
      paymentMode = tenantData.paymentMode
    )

    for (i in 1..fullCycles) {
      val currentEndDate = when (service.billingMode!!.lowercase()) {
        "monthly" -> currentStartDate.plusMonths(1)
        "yearly" -> currentStartDate.plusYears(1)
        else -> throw IllegalArgumentException("Unsupported billing mode")
      }

      val billingCycle = BillingCycle()
      billingCycle.periodStart = currentStartDate
      billingCycle.periodEnd = currentEndDate
      billingCycle.amountDue = logementBasePrice
      billingCycle.subscription = subscription
      billingCycle.createdDate = LocalDateTime.now()
      billingCycle.status = StatusConstants.BILLING_CYCLE_STATUS_PAID
      billingCycleRepository.save(billingCycle)
      billingCycles.add(billingCycle)

      // Create PaymentLine

      createPaymentLine(
        paymentId = payment.id!!,
        billingCycleId = billingCycle.id!!,
        remainingAmount = logementBasePrice
      )

      // Update remaining deposit and start date
      remainingDeposit -= logementBasePrice
      currentStartDate = currentEndDate
    }

    // Handle partial cycle
    if (partialCycleAmount > BigDecimal.ZERO) {
      val currentEndDate = when (service.billingMode!!.lowercase()) {
        "monthly" -> currentStartDate.plusMonths(1)
        "yearly" -> currentStartDate.plusYears(1)
        else -> throw IllegalArgumentException("Unsupported billing mode")
      }

      val billingCycle = BillingCycle()
      billingCycle.periodStart = currentStartDate
      billingCycle.periodEnd = currentEndDate
      billingCycle.amountDue = logementBasePrice
      billingCycle.subscription = subscription

      // Set status to "Partial Paid" for partial payment
      billingCycle.status = "Partial Paid"
      billingCycleRepository.save(billingCycle)
      billingCycles.add(billingCycle)

      // Create PaymentLine for partial cycle
      createPaymentLine(
        paymentId = payment.id!!,
        billingCycleId = billingCycle.id!!,
        remainingAmount = partialCycleAmount
      )
    }

    // Update tenant and subscription end date

    val totalSubscriptions = subscription.subscriptNumber ?: 0
    val calculatedEndDate = when (service.billingMode!!.lowercase()) {
      "monthly" -> subscription.startDate?.plusMonths(totalSubscriptions.toLong())
      "yearly" -> subscription.startDate?.plusYears(totalSubscriptions.toLong())
      else -> throw IllegalArgumentException("Unsupported billing mode")
    }

    subscription.endDate = calculatedEndDate
    tenant.moveOutDate = calculatedEndDate
    tenant = tenantRepository.save(tenant)
    subscriptionRepository.save(subscription)

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
    val existingTenant = tenantRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Tenant with ID $id not found") }

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

    return mapOf(
      "billingCycles" to billingCycles.map { cycle ->
        mapOf(
          "id" to cycle.id,
          "amountDue" to cycle.amountDue,
          "periodStart" to cycle.periodStart,
          "periodEnd" to cycle.periodEnd,
          "status" to cycle.status
        )
      },
      "subscriptions" to subscriptions.map { subscription ->
        mapOf(
          "id" to subscription.id,
          "serviceName" to subscription.service?.name,
          "price" to subscription.price,
          "startDate" to subscription.startDate,
          "endDate" to subscription.endDate,
          "status" to subscription.status
        )
      },
      "totalPayments" to billingCycles.size,
      "completedPayments" to paidBillingCycles.size,
      "remainingPayments" to unpaidBillingCycles.size,
      "amountPaid" to paidBillingCycles.sumOf { it.amountDue ?: BigDecimal.ZERO },
      "outstandingDebt" to unpaidBillingCycles.sumOf { it.amountDue ?: BigDecimal.ZERO }
    )
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
    val invoices = invoiceRepository.findByTenant(tenant).orEmpty()
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
      },
      "userActivity" to mapOf(
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
    val tenant = tenantRepository.findById(tenantId)
      .orElseThrow { IllegalArgumentException("Tenant not found") }

    val subscriptions = subscriptionRepository.findByTenant(tenant).map { subscription ->
      val subscribedOptions = subscriptionOptionRepository.findBySubscription(subscription).map { option ->
        mapOf(
          "id" to option.id,
          "name" to option.option!!.name,
          "price" to option.option!!.price
        )
      }

      val totalSubscriptions = subscription.subscriptNumber ?: 0
      val completedSubscriptions = billingCycleRepository.findBySubscription(subscription)
        .count { it.status == StatusConstants.BILLING_CYCLE_STATUS_PAID }
      val remainingSubscriptions = totalSubscriptions - completedSubscriptions
      val remainingAmount = BigDecimal(remainingSubscriptions) * (subscription.price ?: BigDecimal.ZERO)

      mapOf(
        "id" to subscription.id,
        "serviceName" to subscription.service?.name,
        "price" to subscription.price,
        "startDate" to subscription.startDate,
        "endDate" to subscription.endDate,
        "status" to subscription.status,
        "options" to subscribedOptions,
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
      subscriptionsData.sumOf { BigDecimal(it.subscriptNumber ?: 0) * (it.price ?: BigDecimal.ZERO) }

    val totalRemaining = totalSubscription - totalAlreadyPaid

    val totalPayments = subscriptionsData.sumOf { it.subscriptNumber ?: 0 }

    val completedPayments = paymentLines.count { it.billingCycle?.status == StatusConstants.BILLING_CYCLE_STATUS_PAID }
    val partialPayments = paymentLines.count { it.billingCycle?.status == StatusConstants.BILLING_CYCLE_STATUS_PARTIAL_PAID }
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
          "serviceId" to cycle.subscription?.service?.id,
          "serviceName" to cycle.subscription?.service?.name,
          "amountDue" to cycle.amountDue,
          "periodStart" to cycle.periodStart,
          "periodEnd" to cycle.periodEnd,
          "status" to cycle.status
        )
      }
    }

    val financialSummary = mapOf(
      "totalPayments" to billingCycles.size,
      "completedPayments" to billingCycles.count { it["status"] == StatusConstants.BILLING_CYCLE_STATUS_PAID },
      "remainingPayments" to billingCycles.count { it["status"] != StatusConstants.BILLING_CYCLE_STATUS_PAID },
      "amountPaid" to billingCycles.filter { it["status"] == StatusConstants.BILLING_CYCLE_STATUS_PAID }.sumOf { it["amountDue"] as BigDecimal },
      "outstandingDebt" to billingCycles.filter { it["status"] != StatusConstants.BILLING_CYCLE_STATUS_PAID }.sumOf { it["amountDue"] as BigDecimal }
    )

    val issues = issueRepository.findByTenant(tenant).map { issue ->
      mapOf(
        "id" to issue.id,
        "title" to issue.title,
        "description" to issue.description,
        "declarationDate" to issue.declarationDate,
        "status" to issue.status
      )
    }

    val invoices = invoiceRepository.findByTenant(tenant).map { invoice ->
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

    val subscribedServices = subscriptionRepository.findByTenant(tenant).map { it.service?.id }
    val subscribedOptions = subscriptionRepository.findByTenant(tenant)
      .flatMap { subscription -> subscriptionOptionRepository.findBySubscription(subscription) }
      .map { it.option?.id }

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
