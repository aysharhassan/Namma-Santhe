package com.example.nammasantheledgerapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val shopName: String,
    val pinCode: String,
    val isLoggedIn: Boolean = false
)