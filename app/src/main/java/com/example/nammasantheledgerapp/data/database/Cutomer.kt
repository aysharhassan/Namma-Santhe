package com.example.nammasantheledgerapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val customerId: Long = 0,
    val name: String,
    val phone: String,
    val email: String = "",
    val address: String = "",
    val createdDate: Date = Date(),
    var totalCredit: Double = 0.0,
    var totalPayments: Double = 0.0,
    var lastTransactionDate: Date = Date(),
    var trustScore: Double = 0.5,
    var paymentDelayDays: Int = 0,
    var paymentFrequency: Int = 0,
    var repaymentConsistency: Double = 0.0
) {
    fun getBalance(): Double = totalCredit - totalPayments

    fun getTrustLevel(): TrustLevel {
        return when {
            trustScore >= 0.7 -> TrustLevel.TRUSTED
            trustScore >= 0.4 -> TrustLevel.MODERATE
            else -> TrustLevel.RISKY
        }
    }
}

enum class TrustLevel {
    TRUSTED, MODERATE, RISKY
}