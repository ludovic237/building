package com.example.backend.services

import com.example.backend.dtos.*
import com.example.backend.models.*
import com.example.backend.repositories.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
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
        paymentStatus = subscription[0].status,
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
    rent.paymentDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() // No payment yet

    // Save the Rent
    rentRepository.save(rent)

    return savedTenant
  }

  fun createTenantNew(tenantData: TenantCreateDataDTO): Tenant {
    tenantData.startDate = tenantData.startDate.plusDays(1)
    var service = serviceRepository.findById(tenantData.serviceId)
      .orElseThrow { IllegalArgumentException("Services not found with ID: ${tenantData.serviceId}") }


    // Step 1: Create Tenant
    var tenant = Tenant()
    tenant.housingUnit = housingUnitRepository.findById(tenantData.housingUnitId).get()
    tenant.user = userRepository.findById(tenantData.userId).get()
    tenant.moveInDate = tenantData.startDate
    tenant.moveOutDate = tenantData.endDate
    tenant.securityDeposit = tenantData.securityDeposit
    tenant = tenantRepository.save(tenant)

    var houstingUnit = housingUnitRepository.findById(tenantData.housingUnitId)
      .orElseThrow { IllegalArgumentException("Housing Unit not found with ID: ${tenantData.housingUnitId}") }
    houstingUnit.tenant = tenant
    houstingUnit = housingUnitRepository.save(houstingUnit)

    // Step : Create Subscription
    val subscription = Subscription()
    subscription.service = serviceRepository.findById(tenantData.serviceId).get()
    subscription.price = tenantData.securityDeposit
    subscription.startDate = tenantData.startDate
    subscription.endDate = tenantData.endDate
    subscription.tenant = tenant
    subscription.status = "Active"
    subscriptionRepository.save(subscription)

    // Step 2: Create Billing Cycles
    val billingCycles = mutableListOf<BillingCycle>()
    var currentStartDate = tenantData.startDate

    var remainingDeposit = tenantData.securityDeposit
    var totalAmount = tenantData.logementBasePrice * tenantData.numberOfSubscription.toBigDecimal()

    var tenantPrice: BigDecimal = 0.0.toBigDecimal()

    var currentEndDate: LocalDate? = null;
    for (i in 1..tenantData.numberOfSubscription) {
      currentEndDate = when (service.billingMode!!.lowercase()) {
        "monthly" -> currentStartDate.plusMonths(1)
        "yearly" -> currentStartDate.plusYears(1)
        else -> throw IllegalArgumentException("Unsupported billing mode")
      }

      val billingCycle = BillingCycle()
      billingCycle.periodStart = currentStartDate
      billingCycle.periodEnd = currentEndDate
      billingCycle.amountDue = tenantData.logementBasePrice
      billingCycle.subscription = subscription

      tenantPrice = tenantPrice + tenantData.logementBasePrice
      // Determine the status based on the remaining deposit
      if (remainingDeposit >= tenantPrice) {
        billingCycle.status = "Paid"
      } else {
        billingCycle.status = "Due"
      }

      billingCycleRepository.save(billingCycle)

      if (remainingDeposit >= tenantPrice) {
        val payment = createPayment(
          tenant = tenant,
          depositAmount = tenantData.logementBasePrice,
          paymentMode = tenantData.paymentMode
        )
        createPaymentLine(
          paymentId = payment.id!!,
          billingCycleId = billingCycle.id!!,
          remainingAmount = tenantData.logementBasePrice
        )
      } else {
        createPaymentLine(
          paymentId = null,
          billingCycleId = billingCycle.id!!,
          remainingAmount = 0.toBigDecimal()
        )
      }

      billingCycles.add(billingCycle)

      // Increment start date for the next cycle
      currentStartDate = currentEndDate.plusDays(1)

      currentStartDate = when (service.billingMode!!.lowercase()) {
        "monthly" -> currentEndDate.plusDays(1)
        "yearly" -> currentEndDate.plusDays(1)
        else -> throw IllegalArgumentException("Unsupported billing mode")
      }
    }

    tenant.moveOutDate = currentEndDate
    tenant = tenantRepository.save(tenant)

    subscription.endDate = currentEndDate
    subscriptionRepository.save(subscription)

    return tenant
  }

  fun createPayment(tenant: Tenant?, depositAmount: BigDecimal, paymentMode: String): Payment {
    val payment = Payment()
    payment.tenant = tenant
    payment.totalAmount = depositAmount
    payment.paymentMethod = paymentMode
    payment.paymentDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    return paymentRepository.save(payment)
  }

  fun createPaymentLine(paymentId: Long?, billingCycleId: Long, remainingAmount: BigDecimal): PaymentLine {
    val paymentLine = PaymentLine()
    paymentLine.payment = paymentRepository.findById(paymentId!!).get()
    paymentLine.billingCycle = billingCycleRepository.findById(billingCycleId).get()
    paymentLine.amountPaid = remainingAmount
    return paymentLineRepository.save(paymentLine)
  }

  fun updateTenant(id: Long, updatedTenant: Tenant): Tenant {
    val existingTenant = tenantRepository.findById(id)
      .orElseThrow { IllegalArgumentException("Tenant with ID $id not found") }

    existingTenant.user!!.firstName = updatedTenant.user!!.lastName
    existingTenant.user!!.lastName = updatedTenant.user!!.firstName
    existingTenant.user!!.email = updatedTenant.user!!.email
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
    val paidBillingCycles = billingCycles.filter { it.status == "Paid" }
    val unpaidBillingCycles = billingCycles.filter { it.status != "Paid" }

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

      mapOf(
        "id" to subscription.id,
        "serviceName" to subscription.service?.name,
        "price" to subscription.price,
        "startDate" to subscription.startDate,
        "endDate" to subscription.endDate,
        "status" to subscription.status,
        "options" to subscribedOptions
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
      "completedPayments" to billingCycles.count { it["status"] == "Paid" },
      "remainingPayments" to billingCycles.count { it["status"] != "Paid" },
      "amountPaid" to billingCycles.filter { it["status"] == "Paid" }.sumOf { it["amountDue"] as BigDecimal },
      "outstandingDebt" to billingCycles.filter { it["status"] != "Paid" }.sumOf { it["amountDue"] as BigDecimal }
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
      "financialSummary" to financialSummary,
      "issues" to issues,
      "invoices" to invoices,
      "userActivity" to userActivity,
      "services" to services
    )
  }

}
