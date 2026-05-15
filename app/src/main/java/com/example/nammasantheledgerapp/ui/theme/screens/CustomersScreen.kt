package com.example.nammasantheledgerapp.ui.theme.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import com.example.nammasantheledgerapp.data.database.Customer
import com.example.nammasantheledgerapp.ui.theme.*
import com.example.nammasantheledgerapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    viewModel: MainViewModel,
    onCustomerClick: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var filterByTrust by remember { mutableStateOf<String?>(null) }

    val customers by viewModel.customers.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val shopName = currentUser?.shopName ?: "Namma Santhe"
    val context = LocalContext.current

    val displayCustomers = if (searchQuery.isEmpty()) customers else searchResults
    val filteredCustomers = when (filterByTrust) {
        "TRUSTED" -> displayCustomers.filter { it.getTrustLevel().name == "TRUSTED" }
        "RISKY" -> displayCustomers.filter { it.getTrustLevel().name == "RISKY" }
        else -> displayCustomers
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Customers",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Customer", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.searchCustomers(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text("Search customers...", color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            }
                            innerTextField()
                        }
                    )
                }
            }

            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = filterByTrust == null,
                        onClick = { filterByTrust = null },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            labelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = filterByTrust == "TRUSTED",
                        onClick = { filterByTrust = "TRUSTED" },
                        label = { Text("🟢 Trusted") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonGreen,
                            labelColor = NeonGreen
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = filterByTrust == "RISKY",
                        onClick = { filterByTrust = "RISKY" },
                        label = { Text("🔴 Risky") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonPink,
                            labelColor = NeonPink
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Customers Count
            Text(
                text = "${filteredCustomers.size} Customers",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Customers List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredCustomers) { customer ->
                    CustomerDetailedCard(
                        customer = customer,
                        onClick = { onCustomerClick(customer.customerId) },
                        onCall = { phone ->
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$phone")
                            }
                            context.startActivity(intent)
                        },
                        onWhatsApp = { phone, amount ->
                            val message = "hey there, just a reminder of my paymet of $amount.rs please pay as soon as possible from $shopName"
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                val encodedMessage = Uri.encode(message)
                                val cleanPhone = if (phone.startsWith("+")) phone.substring(1) else "91$phone"
                                data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage")
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    // Add Customer Dialog
    if (showAddDialog) {
        AddCustomerDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, phone, email, address ->
                viewModel.addCustomer(name, phone, email, address)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun CustomerDetailedCard(
    customer: Customer,
    onClick: () -> Unit,
    onCall: (String) -> Unit,
    onWhatsApp: (String, Double) -> Unit
) {
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
            .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = trustColor)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Trust Badge
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(trustColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (trustLevel) {
                                com.example.nammasantheledgerapp.data.database.TrustLevel.TRUSTED -> "✓"
                                com.example.nammasantheledgerapp.data.database.TrustLevel.MODERATE -> "!"
                                else -> "⚠"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = trustColor
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

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
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Due",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "₹${String.format("%.0f", balance)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (balance > 0) NeonPink else NeonGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onCall(customer.phone) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text("Call", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { onWhatsApp(customer.phone, balance) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen)
                ) {
                    Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Ledger", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
