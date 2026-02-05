package com.debttracker.app.data.model

data class BackupData(
    val version: String = "2.0",
    val exportDate: Long = System.currentTimeMillis(),
    val persons: List<Person>,
    val transactions: List<Transaction>,
    val recurringCharges: List<RecurringCharge>
)
