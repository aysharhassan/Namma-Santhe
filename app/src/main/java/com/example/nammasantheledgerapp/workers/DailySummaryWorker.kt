package com.example.nammasantheledgerapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.nammasantheledgerapp.data.database.AppDatabase
import com.example.nammasantheledgerapp.data.database.Customer
import com.example.nammasantheledgerapp.data.database.DailySummary
import com.example.nammasantheledgerapp.data.database.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DailySummaryWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getDatabase(applicationContext)
            val yesterday = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
            val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesterday)

            val totalCredit = database.transactionDao().getTotalCreditForDate(yesterday) ?: 0.0
            val totalPayment = database.transactionDao().getTotalPaymentForDate(yesterday) ?: 0.0

            val allCustomersFlow = database.customerDao().getAllCustomers()
            val allCustomers = allCustomersFlow.first()
            val totalPending = allCustomers.sumOf { it.getBalance() }
            val activeCustomers = allCustomers.count { customer ->
                (Date().time - customer.lastTransactionDate.time) / (1000 * 60 * 60 * 24) < 7
            }
            val transactionCount = database.transactionDao().getTransactionCountForDate(yesterday)

            val summary = DailySummary(
                date = dateString,
                totalSales = totalCredit,
                totalCreditGiven = totalCredit,
                totalCollected = totalPayment,
                pendingDues = totalPending,
                profitEstimate = totalCredit * 0.3,
                activeCustomers = activeCustomers,
                totalTransactions = transactionCount
            )

            database.dailySummaryDao().insertOrUpdateSummary(summary)

            return@withContext Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Result.retry()
        }
    }
}