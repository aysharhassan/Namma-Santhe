package com.example.nammasantheledgerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    CREDIT, PAYMENT
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Long = 0,
    val customerId: Long,
    val amount: Double,
    val type: TransactionType,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
