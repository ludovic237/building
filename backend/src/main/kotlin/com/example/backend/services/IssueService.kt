package com.example.backend.services

    import com.example.backend.models.Issue
    import com.example.backend.repositories.IssueRepository
    import org.springframework.stereotype.Service
    import java.util.*

    @Service
    class IssueService(
        private val issueRepository: IssueRepository
    ) {

        fun getAllIssues(): List<Issue> {
            return issueRepository.findAll()
        }

        fun getIssueById(id: Long): Optional<Issue> {
            return issueRepository.findById(id)
        }

        fun createIssue(issue: Issue): Issue {
            return issueRepository.save(issue)
        }

        fun updateIssue(id: Long, updatedIssue: Issue): Issue {
            val existingIssue = issueRepository.findById(id)
                .orElseThrow { IllegalArgumentException("Issue with ID $id not found") }

            existingIssue.title = updatedIssue.title
            existingIssue.description = updatedIssue.description
            existingIssue.declarationDate = updatedIssue.declarationDate
            existingIssue.status = updatedIssue.status
            existingIssue.tenant = updatedIssue.tenant
            // Update other fields as necessary

            return issueRepository.save(existingIssue)
        }

        fun deleteIssue(id: Long) {
            if (!issueRepository.existsById(id)) {
                throw IllegalArgumentException("Issue with ID $id not found")
            }
            issueRepository.deleteById(id)
        }
    }
