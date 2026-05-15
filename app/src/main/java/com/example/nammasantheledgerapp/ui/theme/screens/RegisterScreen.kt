package com.example.nammasantheledgerapp.ui.theme.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammasantheledgerapp.ui.theme.*
import com.example.nammasantheledgerapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: MainViewModel,
    onRegisterSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background Neon Glow
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopEnd)
                .offset(x = 150.dp, y = (-100).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonPink.copy(alpha = 0.1f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(16.dp, RoundedCornerShape(40.dp), spotColor = NeonGreen),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(40.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📒", fontSize = 40.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Join Namma Santhe",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeonGreen
            )
            
            Text(
                text = "Setup your digital ledger",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NeonBlue.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = NeonBlue),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Full Name", color = NeonBlue) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = NeonBlue.copy(alpha = 0.3f),
                            focusedTextColor = NeonBlue,
                            unfocusedTextColor = NeonBlue
                        )
                    )

                    OutlinedTextField(
                        value = shopName,
                        onValueChange = { shopName = it },
                        label = { Text("Shop Name", color = NeonBlue) },
                        leadingIcon = { Icon(Icons.Default.Store, contentDescription = null, tint = NeonBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = NeonBlue.copy(alpha = 0.3f),
                            focusedTextColor = NeonBlue,
                            unfocusedTextColor = NeonBlue
                        )
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address", color = NeonBlue) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NeonBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = NeonBlue.copy(alpha = 0.3f),
                            focusedTextColor = NeonBlue,
                            unfocusedTextColor = NeonBlue
                        )
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number", color = NeonBlue) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = NeonBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = NeonBlue.copy(alpha = 0.3f),
                            focusedTextColor = NeonBlue,
                            unfocusedTextColor = NeonBlue
                        )
                    )

                    OutlinedTextField(
                        value = pin,
                        onValueChange = { pin = it },
                        label = { Text("Set 4-Digit PIN", color = NeonBlue) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonBlue) },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = NeonBlue.copy(alpha = 0.3f),
                            focusedTextColor = NeonBlue,
                            unfocusedTextColor = NeonBlue
                        )
                    )

                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = { confirmPin = it },
                        label = { Text("Confirm PIN", color = if (confirmPin.isNotEmpty() && pin != confirmPin) NeonPink else NeonBlue) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = if (confirmPin.isNotEmpty() && pin != confirmPin) NeonPink else NeonBlue) },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (confirmPin.isNotEmpty() && pin != confirmPin) NeonPink else NeonBlue,
                            unfocusedBorderColor = (if (confirmPin.isNotEmpty() && pin != confirmPin) NeonPink else NeonBlue).copy(alpha = 0.3f),
                            focusedTextColor = if (confirmPin.isNotEmpty() && pin != confirmPin) NeonPink else NeonBlue,
                            unfocusedTextColor = if (confirmPin.isNotEmpty() && pin != confirmPin) NeonPink else NeonBlue
                        )
                    )

                    if (confirmPin.isNotEmpty() && pin != confirmPin) {
                        Text(
                            text = "PINs do not match",
                            color = NeonPink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = showPassword,
                            onCheckedChange = { showPassword = it },
                            colors = CheckboxDefaults.colors(checkedColor = NeonBlue, uncheckedColor = NeonBlue.copy(alpha = 0.5f))
                        )
                        Text(
                            text = "Show Password",
                            fontSize = 14.sp,
                            color = NeonBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showPassword = !showPassword }
                        )
                    }

                    Button(
                        onClick = {
                            if (pin == confirmPin && pin.isNotEmpty()) {
                                viewModel.registerUser(username, email, phone, shopName, pin)
                                onRegisterSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = NeonGreen),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading && username.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && pin.isNotEmpty() && pin == confirmPin
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                        } else {
                            Text("REGISTER", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
