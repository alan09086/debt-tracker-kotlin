package com.debttracker.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("personId")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personId: Long,
    val amount: Double,
    val description: String,
    val type: TransactionType,
    val isRecurring: Boolean = false,
    val date: Long = System.currentTimeMillis()
)

enum class TransactionType {
    DEBT,    // They owe you (positive)
    PAYMENT  // You owe them or they paid back (negative)
}
