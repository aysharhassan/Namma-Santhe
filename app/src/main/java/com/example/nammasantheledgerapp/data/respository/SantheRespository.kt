package com.example.nammasantheledgerapp.data.repository

import android.content.Context
import com.example.nammasantheledgerapp.data.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import java.util.*

class SantheRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val sharedPrefs = context.getSharedPreferences("santhe_prefs", Context.MODE_PRIVATE)

    // Settings
    fun isDarkMode(): Boolean = sharedPrefs.getBoolean("dark_mode", false)
    fun setDarkMode(enabled: Boolean) = sharedPrefs.edit().putBoolean("dark_mode", enabled).apply()

    fun isLargeText(): Boolean = sharedPrefs.getBoolean("large_text", false)
    fun setLargeText(enabled: Boolean) = sharedPrefs.edit().putBoolean("large_text", enabled).apply()

    fun getLanguage(): String = sharedPrefs.getString("language", "en") ?: "en"
    fun setLanguage(lang: String) = sharedPrefs.edit().putString("language", lang).apply()

    // User Operations
    suspend fun registerUser(username: String, email: String, phone: String, shopName: String, pin: String): Long {
        val user = User(
            username = username,
            email = email,
            phoneNumber = phone,
            shopName = shopName,
            pinCode = pin,
            isLoggedIn = true
        )
        return database.userDao().insertUser(user)
    }

    suspend fun loginWithEmail(email: String, pin: String): User? {
        return database.userDao().loginWithEmail(email, pin)
    }

    suspend fun loginWithPhone(phone: String, pin: String): User? {
        return database.userDao().loginWithPhone(phone, pin)
    }

    suspend fun getLoggedInUser(): User? {
        return database.userDao().getLoggedInUser()
    }

    suspend fun logout() {
        database.userDao().logoutAll()
    }

    // Customer Operations
    fun getAllCustomers(): Flow<List<Customer>> {
        return database.customerDao().getAllCustomers()
    }

    fun searchCustomers(query: String): Flow<List<Customer>> {
        return database.customerDao().searchCustomers(query)
    }

    suspend fun addCustomer(name: String, phone: String, email: String = "", address: String = ""): Long {
        val customer = Customer(
            name = name,
            phone = phone,
            email = email,
            address = address
        )
        return database.customerDao().insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: Customer) {
        database.customerDao().updateCustomer(customer)
    }

    // Transaction Operations
    suspend fun addCredit(customerId: Long, amount: Double, note: String = "") {
        val transaction = Transaction(
            customerId = customerId,
            amount = amount,
            type = TransactionType.CREDIT,
            note = note
        )
        database.transactionDao().insertTransaction(transaction)
        updateCustomerBalance(customerId)
        updateTrustScore(customerId)
    }

    suspend fun addPayment(customerId: Long, amount: Double, paymentMethod: PaymentMethod, upiReference: String = "", note: String = "") {
        val transaction = Transaction(
            customerId = customerId,
            amount = amount,
            type = TransactionType.PAYMENT,
            paymentMethod = paymentMethod,
            upiReference = upiReference,
            note = note
        )
        database.transactionDao().insertTransaction(transaction)
        updateCustomerBalance(customerId)
        updateTrustScore(customerId)
    }

    private suspend fun updateCustomerBalance(customerId: Long) {
        val customer = database.customerDao().getCustomerById(customerId) ?: return
        val totalCredit = database.transactionDao().getTotalCredit(customerId) ?: 0.0
        val totalPayment = database.transactionDao().getTotalPayment(customerId) ?: 0.0

        customer.totalCredit = totalCredit
        customer.totalPayments = totalPayment
        customer.lastTransactionDate = Date()
        database.customerDao().updateCustomer(customer)
    }

    private suspend fun updateTrustScore(customerId: Long) {
        val customer = database.customerDao().getCustomerById(customerId) ?: return
        val transactions = database.transactionDao().getCustomerTransactions(customerId).first()

        // Calculate trust score based on multiple factors
        var score = 0.5

        // Payment delay factor
        val daysSinceLastPayment = (Date().time - customer.lastTransactionDate.time) / (1000 * 60 * 60 * 24)
        if (daysSinceLastPayment < 7) score += 0.2
        else if (daysSinceLastPayment > 30) score -= 0.2

        // Payment frequency factor
        val paymentCount = database.transactionDao().getPaymentCount(customerId)
        if (paymentCount > 5) score += 0.15
        else if (paymentCount == 0) score -= 0.1

        // Pending amount factor
        val balance = customer.getBalance()
        if (balance < 1000) score += 0.15
        else if (balance > 5000) score -= 0.2

        // Repayment consistency
        if (customer.totalCredit > 0) {
            val repaymentRatio = customer.totalPayments / customer.totalCredit
            if (repaymentRatio > 0.7) score += 0.2
            else if (repaymentRatio < 0.3) score -= 0.2
        }

        customer.trustScore = score.coerceIn(0.0, 1.0)
        database.customerDao().updateCustomer(customer)
    }

    suspend fun getDashboardData(): DashboardData {
        val customers = database.customerDao().getAllCustomers().first()
        val totalPending = customers.sumOf { it.getBalance() }
        val totalCollected = customers.sumOf { it.totalPayments }
        val totalCredit = customers.sumOf { it.totalCredit }

        val today = Date()
        val todayStart = Date(today.time - (today.time % (24 * 60 * 60 * 1000)))
        val allTransactions = database.transactionDao().getCustomerTransactions(0).first()
        val todayTransactions = allTransactions.filter { it.timestamp > todayStart }

        val todayCollected = todayTransactions.filter { it.type == TransactionType.PAYMENT }.sumOf { it.amount }

        return DashboardData(
            pendingPayment = totalPending,
            totalCollected = totalCollected,
            yetToReceive = totalCredit - totalCollected,
            todayCollection = todayCollected,
            totalCustomers = customers.size,
            trustedCustomers = customers.count { it.getTrustLevel() == TrustLevel.TRUSTED },
            riskyCustomers = customers.count { it.getTrustLevel() == TrustLevel.RISKY }
        )
    }

    fun getCustomerTransactions(customerId: Long): Flow<List<Transaction>> {
        return database.transactionDao().getCustomerTransactions(customerId)
    }

    data class DashboardData(
        val pendingPayment: Double,
        val totalCollected: Double,
        val yetToReceive: Double,
        val todayCollection: Double,
        val totalCustomers: Int,
        val trustedCustomers: Int,
        val riskyCustomers: Int
    )
}