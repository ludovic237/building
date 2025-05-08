package com.example.backend.controllers

import com.example.backend.dtos.DashboardDTO
import com.example.backend.services.DashboardService
import com.example.backend.services.PaymentService
import com.example.backend.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/admin/dashboard")
class DashboardController(
  private val userService: UserService,
  private val dashboardService: DashboardService? = null,
  private val paymentService: PaymentService? = null
) {


  @CrossOrigin(origins = ["http://localhost:4200"])
  @PreAuthorize("isAuthenticated()")
  @GetMapping
  fun getDashboardData(): ResponseEntity<DashboardDTO> {
    val dashboardData: DashboardDTO = dashboardService!!.getDashboardData();
    return ResponseEntity.ok(dashboardData);
  }

  @GetMapping("/info")
  fun getAdminDashboardData(
    @RequestParam(required = false) startDate: LocalDateTime?,
    @RequestParam(required = false) endDate: LocalDateTime?
  ): Map<String, Any> {
    return dashboardService!!.getAdminDashboardData(startDate, endDate)
  }

}
