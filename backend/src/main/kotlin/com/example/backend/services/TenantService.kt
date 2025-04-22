package com.example.backend.services

import com.example.backend.dtos.TenantDTO
import com.example.backend.dtos.TenantDetailsDTO
import com.example.backend.dtos.TenantCreateDTO
import com.example.backend.dtos.TenantCreateDataDTO
import com.example.backend.models.*
import com.example.backend.repositories.*
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Service
class TenantService(
  private val tenantRepository: TenantRepository,
  private val paymentRepository: PaymentRepository,
  private val billingCycleRepository: BillingCycleRepository,
  private val subscriptionRepository: SubscriptionRepository,
  private val paymentLineRepository: PaymentLineRepository,
  private val serviceRepository: ServiceRepository,
  private val rentRepository: RentRepository,
  private val userRepository: UserRepository,
  private val housingUnitRepository: HoustingUnitRepository,
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
      println("tenant")
      println(tenant)
      println(tenant.user)
      TenantDTO(
        id = tenant.id,
        userId = tenant.user?.id,
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
      .orElseThrow { IllegalArgumentException("Service not found with ID: ${tenantData.serviceId}") }


    // Step 1: Create Tenant
    var tenant = Tenant()
    tenant.housingUnit = housingUnitRepository.findById(tenantData.housingUnitId).get()
    tenant.user = userRepository.findById(tenantData.userId).get()
    tenant.moveInDate = tenantData.startDate
    tenant.moveOutDate = tenantData.endDate
    tenant.securityDeposit = tenantData.securityDeposit
    tenant = tenantRepository.save(tenant)

   var houstingUnit =  housingUnitRepository.findById(tenantData.housingUnitId)
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
    var totalAmount = tenantData.logementBasePrice*tenantData.numberOfSubscription.toBigDecimal()

    var tenantPrice :BigDecimal = 0.0.toBigDecimal()

    var currentEndDate:LocalDate? = null;
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

      tenantPrice =  tenantPrice + tenantData.logementBasePrice
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

  fun createPayment(tenant: Tenant?, depositAmount: BigDecimal,paymentMode:String): Payment {
    val payment = Payment()
    payment.tenant = tenant
    payment.totalAmount = depositAmount
    payment.paymentMethod = paymentMode
    payment.paymentDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    return paymentRepository.save(payment)
  }

  fun createPaymentLine(paymentId: Long?, billingCycleId: Long, remainingAmount: BigDecimal): PaymentLine {
    val paymentLine = PaymentLine()
    paymentLine.paymentId = paymentId
    paymentLine.billingCycleId = billingCycleId
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
}
