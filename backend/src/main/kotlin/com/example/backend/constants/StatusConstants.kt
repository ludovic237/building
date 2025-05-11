package com.example.backend.constants

object StatusConstants {
    // Statuts pour la table "invoices"
    const val INVOICE_STATUS_PAID = "PAID"
    const val INVOICE_STATUS_PENDING = "PENDING"
    const val INVOICE_STATUS_OVERDUE = "OVERDUE"

    // Statuts pour la table "subscriptions"
    const val SUBSCRIPTION_STATUS_ACTIVE = "ACTIVE"
    const val SUBSCRIPTION_STATUS_EXPIRED = "EXPIRED"
    const val SUBSCRIPTION_STATUS_CANCELED = "CANCELED"

    // Statuts pour la table "billing_cycles"
    const val BILLING_CYCLE_STATUS_PAID = "PAID"
    const val BILLING_CYCLE_STATUS_PARTIAL_PAID = "PARTIAL_PAID"
    const val BILLING_CYCLE_STATUS_OPEN = "OPEN"
    const val BILLING_CYCLE_STATUS_CLOSED = "CLOSED"
    const val BILLING_CYCLE_STATUS_PENDING = "PENDING"

    // Statuts pour la table "issues"
    const val ISSUE_STATUS_OPEN = "OPEN"
    const val ISSUE_STATUS_IN_PROGRESS = "IN_PROGRESS"
    const val ISSUE_STATUS_RESOLVED = "RESOLVED"
    const val ISSUE_STATUS_CLOSED = "CLOSED"
}
