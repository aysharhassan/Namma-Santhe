package com.example.nammasantheledgerapp.ui.theme.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammasantheledgerapp.data.database.Transaction
import com.example.nammasantheledgerapp.data.database.TransactionType
import com.example.nammasantheledgerapp.ui.theme.*
import com.example.nammasantheledgerapp.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerLedgerScreen(
    viewModel: MainViewModel,
    customerId: Long,
    onBack: () -> Unit,
    onPaymentClick: () -> Unit
) {
    val context = LocalContext.current
    val customer = viewModel.getCustomerById(customerId)
    val transactions by viewModel.transactions.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val shopName = currentUser?.shopName ?: "Namma Santhe"
    var showWhatsAppDialog by remember { mutableStateOf(false) }

    LaunchedEffect(customerId) {
        viewModel.loadCustomerTransactions(customerId)
    }

    if (customer == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NeonGreen)
        }
        return
    }

    val balance = customer.getBalance()
    val trustLevel = customer.getTrustLevel()
    val trustColor = when (trustLevel) {
        com.example.nammasantheledgerapp.data.database.TrustLevel.TRUSTED -> NeonGreen
        com.example.nammasantheledgerapp.data.database.TrustLevel.MODERATE -> NeonYellow
        else -> NeonPink
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = customer.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = NeonBlue
                        )
                        Text(
                            text = customer.phone,
                            fontSize = 12.sp,
                            color = NeonBlue.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${customer.phone}")
                        }
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = NeonGreen)
                    }
                    IconButton(onClick = { showWhatsAppDialog = true }) {
                        Icon(Icons.Default.Message, contentDescription = "WhatsApp", tint = NeonGreen)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onPaymentClick,
                containerColor = NeonGreen,
                contentColor = Color.Black,
                modifier = Modifier.shadow(12.dp, RoundedCornerShape(16.dp), spotColor = NeonGreen)
            ) {
                Icon(Icons.Default.Payment, contentDescription = "Record Payment")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Balance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = if (balance > 0) NeonPink else NeonGreen
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (balance > 0) "Total Due Amount" else "No Due",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "₹${String.format("%.2f", balance)}",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (balance > 0) NeonPink else NeonGreen
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Credit", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                Text(
                                    "₹${String.format("%.0f", customer.totalCredit)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonPink
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Paid", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                Text(
                                    "₹${String.format("%.0f", customer.totalPayments)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen
                                )
                            }
                        }
                    }
                }
            }

            // Trust Score Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = trustColor
                    ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Trust Score",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonBlue
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(trustColor, shape = RoundedCornerShape(5.dp))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = trustLevel.name,
                                    fontSize = 12.sp,
                                    color = trustColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = (customer.trustScore).toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = trustColor,
                            trackColor = Color(0xFF1A1A1A)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when (trustLevel) {
                                com.example.nammasantheledgerapp.data.database.TrustLevel.TRUSTED -> "✓ Reliable customer"
                                com.example.nammasantheledgerapp.data.database.TrustLevel.MODERATE -> "⚠ Monitor payments"
                                else -> "🔴 High risk customer"
                            },
                            fontSize = 11.sp,
                            color = trustColor
                        )
                    }
                }
            }

            // Transaction History
            item {
                Text(
                    text = "Transaction History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonBlue,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = NeonBlue.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No transactions yet",
                                color = NeonBlue.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            } else {
                items(transactions) { transaction ->
                    TransactionHistoryItem(transaction = transaction)
                }
            }
        }
    }

    // WhatsApp Reminder Dialog
    if (showWhatsAppDialog) {
        WhatsAppReminderDialog(
            customerPhone = customer.phone,
            dueAmount = balance,
            shopName = shopName,
            onDismiss = { showWhatsAppDialog = false }
        )
    }
}

@Composable
fun TransactionHistoryItem(transaction: Transaction) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val isCredit = transaction.type == TransactionType.CREDIT
    val accentColor = if (isCredit) NeonPink else NeonGreen

    Card(
        modifier = Modifier.fillMaxWidth().shadow(
            elevation = 4.dp,
            shape = RoundedCornerShape(12.dp),
            spotColor = accentColor.copy(alpha = 0.3f)
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(accentColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isCredit) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isCredit) "Credit Given" else "Payment Received",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = accentColor
                    )
                    Text(
                        text = dateFormat.format(transaction.timestamp),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                    )
                    if (transaction.note.isNotEmpty()) {
                        Text(
                            text = transaction.note,
                            fontSize = 11.sp,
                            color = NeonBlue.copy(alpha = 0.7f)
                        )
                    }
                    if (transaction.upiReference.isNotEmpty()) {
                        Text(
                            text = "Ref: ${transaction.upiReference}",
                            fontSize = 10.sp,
                            color = NeonBlue
                        )
                    }
                }
            }

            Text(
                text = "${if (isCredit) "-" else "+"}₹${String.format("%.0f", transaction.amount)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}
