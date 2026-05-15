package com.example.nammasantheledgerapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammasantheledgerapp.data.database.Customer
import com.example.nammasantheledgerapp.ui.theme.*
import com.example.nammasantheledgerapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickUdariScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val customers by viewModel.customers.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    val displayCustomers = if (searchQuery.isEmpty()) customers else searchResults

    LaunchedEffect(searchQuery) {
        viewModel.searchCustomers(searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Quick Udari Entry 🚀",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    IconButton(onClick = { showAddCustomerDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Customer", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (selectedCustomer == null) {
            // Customer Selection Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search customer...", color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Customers List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayCustomers) { customer ->
                        CustomerSelectionCard(
                            customer = customer,
                            onClick = { selectedCustomer = customer }
                        )
                    }
                }
            }
        } else {
            // Amount Entry Screen
            AmountEntryScreen(
                customer = selectedCustomer!!,
                amount = amount,
                note = note,
                onAmountChange = { amount = it },
                onNoteChange = { note = it },
                onSubmit = {
                    if (amount.isNotEmpty()) {
                        viewModel.addCredit(
                            selectedCustomer!!.customerId,
                            amount.toDouble(),
                            note
                        )
                        selectedCustomer = null
                        amount = ""
                        note = ""
                        onBack()
                    }
                },
                onBack = { selectedCustomer = null }
            )
        }
    }

    // Add Customer Dialog
    if (showAddCustomerDialog) {
        AddCustomerDialog(
            onDismiss = { showAddCustomerDialog = false },
            onAdd = { name, phone, email, address ->
                viewModel.addCustomer(name, phone, email, address)
                showAddCustomerDialog = false
            }
        )
    }
}

@Composable
fun CustomerSelectionCard(customer: Customer, onClick: () -> Unit) {
    val balance = customer.getBalance()
    val trustLevel = customer.getTrustLevel()
    val trustColor = when (trustLevel) {
        com.example.nammasantheledgerapp.data.database.TrustLevel.TRUSTED -> NeonGreen
        com.example.nammasantheledgerapp.data.database.TrustLevel.MODERATE -> NeonYellow
        else -> NeonPink
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(12.dp), spotColor = MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = customer.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = customer.phone,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(trustColor, shape = RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = trustLevel.name,
                        fontSize = 10.sp,
                        color = trustColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                text = "₹${String.format("%.0f", balance)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (balance > 0) NeonPink else NeonGreen
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountEntryScreen(
    customer: Customer,
    amount: String,
    note: String,
    onAmountChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enter Amount", fontWeight = FontWeight.Bold, color = NeonYellow) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonYellow)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Scrollable content area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Customer Info
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = NeonYellow),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = customer.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonYellow
                        )
                        Text(
                            text = "Current Due: ₹${String.format("%.0f", customer.getBalance())}",
                            fontSize = 14.sp,
                            color = NeonPink,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Amount Display
                Text(
                    text = if (amount.isEmpty()) "₹0" else "₹$amount",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonGreen,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Quick Amount Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(100, 200, 500, 1000).forEach { quickAmount ->
                        FilterChip(
                            onClick = { onAmountChange(quickAmount.toString()) },
                            label = { Text("₹$quickAmount", fontSize = 12.sp, color = NeonBlue) },
                            selected = amount == quickAmount.toString(),
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonBlue.copy(alpha = 0.2f),
                                labelColor = NeonBlue
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = amount == quickAmount.toString(),
                                borderColor = NeonBlue.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Note Input
                OutlinedTextField(
                    value = note,
                    onValueChange = onNoteChange,
                    label = { Text("Note (Optional)", color = NeonBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 1,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonBlue,
                        unfocusedBorderColor = NeonBlue.copy(alpha = 0.5f),
                        focusedTextColor = NeonBlue,
                        unfocusedTextColor = NeonBlue
                    )
                )
            }

            // Numeric Keypad - Fixed at the bottom
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 12.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("C", "0", "✓")
                    ).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { key ->
                                Button(
                                    onClick = {
                                        when (key) {
                                            "C" -> onAmountChange("")
                                            "✓" -> onSubmit()
                                            else -> if (amount.length < 9) onAmountChange(amount + key)
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                    .height(64.dp)
                                    .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = when(key) {
                                        "✓" -> NeonGreen
                                        "C" -> NeonPink
                                        else -> NeonBlue
                                    }),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when (key) {
                                            "✓" -> NeonGreen.copy(alpha = 0.1f)
                                            "C" -> NeonPink.copy(alpha = 0.1f)
                                            else -> DarkButton
                                        },
                                        contentColor = when (key) {
                                            "✓" -> NeonGreen
                                            "C" -> NeonPink
                                            else -> NeonBlue
                                        }
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, when(key) {
                                        "✓" -> NeonGreen
                                        "C" -> NeonPink
                                        else -> NeonBlue.copy(alpha = 0.3f)
                                    })
                                ) {
                                    Text(
                                        text = key,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
