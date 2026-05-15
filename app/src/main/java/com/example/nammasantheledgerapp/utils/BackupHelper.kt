package com.example.nammasantheledgerapp.utils

import android.content.Context
import com.example.nammasantheledgerapp.data.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class BackupHelper(private val context: Context) {

    suspend fun exportToExcel(): File = withContext(Dispatchers.IO) {
        val database = AppDatabase.getDatabase(context)
        val customers = database.customerDao().getAllCustomers().first()
        val workbook: Workbook = XSSFWorkbook()

        // Customers Sheet
        val customerSheet = workbook.createSheet("Customers")
        val customerHeaderRow = customerSheet.createRow(0)
        arrayOf("ID", "Name", "Phone", "Address", "Total Credit", "Total Paid", "Balance", "Trust Score").forEachIndexed { index, header ->
            customerHeaderRow.createCell(index).setCellValue(header)
        }

        customers.forEachIndexed { index, customer ->
            val row = customerSheet.createRow(index + 1)
            row.createCell(0).setCellValue(customer.customerId.toDouble())
            row.createCell(1).setCellValue(customer.name)
            row.createCell(2).setCellValue(customer.phone)
            row.createCell(3).setCellValue(customer.address)
            row.createCell(4).setCellValue(customer.totalCredit)
            row.createCell(5).setCellValue(customer.totalPayments)
            row.createCell(6).setCellValue(customer.getBalance())
            row.createCell(7).setCellValue(customer.trustScore.toDouble())
        }

        val fileName = "NammaSanthe_Backup_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.xlsx"
        val file = File(context.getExternalFilesDir(null), fileName)

        workbook.write(FileOutputStream(file))
        workbook.close()

        return@withContext file
    }

    suspend fun createLocalBackup(): File = withContext(Dispatchers.IO) {
        val database = AppDatabase.getDatabase(context)
        val backupFile = File(context.filesDir, "namma_santhe_backup.db")

        // Here you would copy the database file
        // For now, return the database file reference

        return@withContext backupFile
    }
}