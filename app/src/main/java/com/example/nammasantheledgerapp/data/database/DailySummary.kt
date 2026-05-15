package com.example.nammasantheledgerapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_summary")
data class DailySummary(
    @PrimaryKey
    val date: String,
    val totalSales: Double,
    val totalCreditGiven: Double,
    val totalCollected: Double,
    val pendingDues: Double,
    val profitEstimate: Double,
    val activeCustomers: Int,
    val totalTransactions: Int = 0
)