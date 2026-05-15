package com.example.nammasantheledgerapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Long = 0,
    val customerId: Long,
    val amount: Double,
    val type: TransactionType,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val upiReference: String = "",
    val note: String = "",
    val timestamp: Date = Date()
)

enum class TransactionType {
    CREDIT, PAYMENT
}

enum class PaymentMethod {
    CASH, UPI, QR_CODE
}