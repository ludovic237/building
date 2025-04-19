package com.example.backend.controllers

import com.example.backend.models.Issue
import com.example.backend.services.IssueService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/issues")
class IssueController(
  private val issueService: IssueService
) {

  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping
  fun getAllIssues(): ResponseEntity<List<Issue>> {
    return ResponseEntity.ok(issueService.getAllIssues())
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping("/{id}")
  fun getIssueById(@PathVariable id: Long): ResponseEntity<Issue> {
    return ResponseEntity.ok(issueService.getIssueById(id).orElseThrow { IllegalArgumentException("Issue not found") })
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PostMapping
  fun createIssue(@RequestBody issue: Issue): ResponseEntity<Issue> {
    return ResponseEntity.ok(issueService.createIssue(issue))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PutMapping("/{id}")
  fun updateIssue(@PathVariable id: Long, @RequestBody updatedIssue: Issue): ResponseEntity<Issue> {
    return ResponseEntity.ok(issueService.updateIssue(id, updatedIssue))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @DeleteMapping("/{id}")
  fun deleteIssue(@PathVariable id: Long): ResponseEntity<Void> {
    issueService.deleteIssue(id)
    return ResponseEntity.noContent().build()
  }
}
