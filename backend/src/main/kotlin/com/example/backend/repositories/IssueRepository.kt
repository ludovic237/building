package com.example.backend.repositories

import com.example.backend.models.Issue
import com.example.backend.models.Tenant
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface IssueRepository : JpaRepository<Issue, Long> {

  // Find issues by tenant
  fun findByTenant(tenant: Tenant): List<Issue>

  // Find issues by title containing a specific keyword
  fun findByTitleContaining(keyword: String): List<Issue>

  // Find issues by status
  fun findByStatus(status: String): List<Issue>

  // Find issues declared on a specific date
  fun findByDeclarationDate(declarationDate: LocalDateTime): List<Issue>

  // Find issues declared after a specific date
  fun findByDeclarationDateAfter(date: LocalDateTime): List<Issue>

  // Find issues declared before a specific date
  fun findByDeclarationDateBefore(date: LocalDateTime): List<Issue>

}
