package com.example.nammasantheledgerapp.ui.theme.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.nammasantheledgerapp.data.database.PaymentMethod
import com.example.nammasantheledgerapp.ui.theme.*
import com.example.nammasantheledgerapp.viewmodel.MainViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentCollectionScreen(
    viewModel: MainViewModel,
    customerId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val customer = viewModel.getCustomerById(customerId)
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var showQRCodeDialog by remember { mutableStateOf(false) }
    var upiReference by remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    if (customer == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NeonGreen)
        }
        return
    }

    val balance = customer.getBalance()
    val canPayFull = balance > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Record Payment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = NeonGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Customer Info
            Card(
                modifier = Modifier.fillMaxWidth().shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = NeonBlue
                ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Customer Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NeonBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(customer.name, fontWeight = FontWeight.Bold, color = NeonBlue)
                            Text(customer.phone, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Current Due", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            Text(
                                "₹${String.format("%.2f", balance)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonPink
                            )
                        }
                    }
                }
            }

            // Amount Input
            Card(
                modifier = Modifier.fillMaxWidth().shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = NeonGreen
                ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Payment Amount",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NeonGreen
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Enter amount", color = NeonGreen) },
                        leadingIcon = { Text("₹", color = NeonGreen, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = NeonGreen.copy(alpha = 0.5f),
                            focusedTextColor = NeonGreen,
                            unfocusedTextColor = NeonGreen
                        )
                    )

                    if (canPayFull) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { amount = balance.toString() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
                            ) {
                                Text("Full", color = NeonGreen, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { amount = (balance / 2).toString() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
                            ) {
                                Text("Half", color = NeonGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Payment Method
            Card(
                modifier = Modifier.fillMaxWidth().shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = NeonBlue
                ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Payment Method",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NeonBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PaymentMethodCard(
                            title = "Cash",
                            icon = Icons.Default.Money,
                            isSelected = selectedMethod == PaymentMethod.CASH,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedMethod = PaymentMethod.CASH }
                        )
                        PaymentMethodCard(
                            title = "UPI",
                            icon = Icons.Default.QrCode,
                            isSelected = selectedMethod == PaymentMethod.UPI,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedMethod = PaymentMethod.UPI
                                showQRCodeDialog = true
                            }
                        )
                    }
                }
            }

            // Note Input
            Card(
                modifier = Modifier.fillMaxWidth().shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = NeonBlue
                ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Note (Optional)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NeonBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = { Text("Add payment note...", color = NeonBlue.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = NeonBlue.copy(alpha = 0.5f),
                            focusedTextColor = NeonBlue,
                            unfocusedTextColor = NeonBlue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Submit Button
            Button(
                onClick = {
                    val paymentAmount = amount.toDoubleOrNull()
                    if (paymentAmount != null && paymentAmount > 0 && paymentAmount <= balance) {
                        viewModel.addPayment(
                            customerId = customerId,
                            amount = paymentAmount,
                            paymentMethod = selectedMethod,
                            upiReference = upiReference,
                            note = note
                        )
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(12.dp, RoundedCornerShape(12.dp), spotColor = NeonGreen),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = amount.toDoubleOrNull() != null && amount.toDouble() > 0 && amount.toDouble() <= balance
            ) {
                Text("Confirm Payment", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            }
        }
    }

    // QR Code Dialog for UPI
    if (showQRCodeDialog) {
        Dialog(onDismissRequest = { showQRCodeDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(16.dp, RoundedCornerShape(16.dp), spotColor = NeonBlue),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Scan QR Code to Pay",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Amount: ₹${amount.toDoubleOrNull() ?: 0}",
                        fontSize = 14.sp,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Generate UPI QR Code
                    LaunchedEffect(amount) {
                        scope.launch {
                            val upiIntent = "upi://pay?pa=merchant@upi&pn=NammaSanthe&am=${amount.toDoubleOrNull() ?: 0}&cu=INR"
                            qrBitmap = withContext(Dispatchers.IO) {
                                val writer = MultiFormatWriter()
                                val matrix = writer.encode(upiIntent, BarcodeFormat.QR_CODE, 300, 300)
                                BarcodeEncoder().createBitmap(matrix)
                            }
                        }
                    }

                    qrBitmap?.let {
                        Card(
                            modifier = Modifier.padding(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "UPI QR Code",
                                modifier = Modifier.size(200.dp).padding(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = upiReference,
                        onValueChange = { upiReference = it },
                        label = { Text("Transaction Reference", color = NeonBlue) },
                        placeholder = { Text("Enter UPI transaction ID", color = NeonBlue.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = NeonBlue.copy(alpha = 0.5f),
                            focusedTextColor = NeonBlue,
                            unfocusedTextColor = NeonBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (upiReference.isNotEmpty()) {
                                showQRCodeDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Verify & Close", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = if (isSelected) NeonGreen else Color.Transparent)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) NeonGreen.copy(alpha = 0.1f) else DarkButton
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, NeonGreen) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) NeonGreen else NeonBlue.copy(alpha = 0.5f),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) NeonGreen else NeonBlue.copy(alpha = 0.5f)
            )
        }
    }
}
