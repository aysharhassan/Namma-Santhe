package com.example.nammasantheledgerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val customerId: Long = 0,
    val name: String,
    val phone: String,
    val address: String = "",
    val photoUri: String? = null,
    val trustScore: Int = 100, // 0-100: 0-30 Red, 31-70 Yellow, 71-100 Green
    val createdDate: Long = System.currentTimeMillis()
)
