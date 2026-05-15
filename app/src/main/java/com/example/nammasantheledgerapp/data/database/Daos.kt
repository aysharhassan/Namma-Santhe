package com.example.nammasantheledgerapp.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email AND pinCode = :pin")
    suspend fun loginWithEmail(email: String, pin: String): User?

    @Query("SELECT * FROM users WHERE phoneNumber = :phone AND pinCode = :pin")
    suspend fun loginWithPhone(phone: String, pin: String): User?

    @Insert
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInUser(): User?

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAll()
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): Flow<List<Customer>>

    @Insert
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Query("SELECT * FROM customers WHERE customerId = :id")
    suspend fun getCustomerById(id: Long): Customer?

    @Query("DELETE FROM customers WHERE customerId = :id")
    suspend fun deleteCustomer(id: Long)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getCustomerTransactions(customerId: Long): Flow<List<Transaction>>

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'CREDIT' AND customerId = :customerId")
    suspend fun getTotalCredit(customerId: Long): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'PAYMENT' AND customerId = :customerId")
    suspend fun getTotalPayment(customerId: Long): Double?

    @Query("SELECT * FROM transactions WHERE timestamp >= datetime('now', '-7 days')")
    suspend fun getLast7DaysTransactions(): List<Transaction>

    @Query("SELECT COUNT(*) FROM transactions WHERE type = 'PAYMENT' AND customerId = :customerId")
    suspend fun getPaymentCount(customerId: Long): Int

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'CREDIT' AND date(timestamp/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    suspend fun getTotalCreditForDate(date: Date): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'PAYMENT' AND date(timestamp/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    suspend fun getTotalPaymentForDate(date: Date): Double?

    @Query("SELECT COUNT(*) FROM transactions WHERE date(timestamp/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    suspend fun getTransactionCountForDate(date: Date): Int
}

@Dao
interface DailySummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSummary(summary: DailySummary)

    @Query("SELECT * FROM daily_summary ORDER BY date DESC")
    fun getAllSummaries(): Flow<List<DailySummary>>
}