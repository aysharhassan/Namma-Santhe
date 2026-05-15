package com.example.nammasantheledgerapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

class WhatsAppHelper(private val context: Context) {

    fun sendReminder(phoneNumber: String, customerName: String, dueAmount: Double) {
        val message = "Namaskara $customerName, your pending balance is ₹${String.format("%.0f", dueAmount)}. Kindly pay when possible. - Namma Santhe"

        val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // WhatsApp not installed
            val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")
            }
            context.startActivity(playStoreIntent)
        }
    }

    fun sendPaymentConfirmation(phoneNumber: String, customerName: String, amount: Double) {
        val message = "Thank you $customerName! Payment of ₹${String.format("%.0f", amount)} received successfully. - Namma Santhe"

        val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }

        context.startActivity(intent)
    }
}