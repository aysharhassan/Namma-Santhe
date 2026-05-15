package com.example.nammasantheledgerapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
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
fun LoginScreen(
    viewModel: MainViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var isEmailLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(viewModel.isLoggedIn) {
        if (viewModel.isLoggedIn.value) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Decorative Neon Glows
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonGreen.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonBlue.copy(alpha = 0.15f), Color.Transparent)
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
            // App Logo
            Card(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(20.dp, RoundedCornerShape(50.dp), spotColor = NeonGreen),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(50.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📒",
                        fontSize = 50.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Namma Santhe Ledger",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeonGreen
            )

            Text(
                text = "Digital Udari Management",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NeonBlue
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Login Type Toggle
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = NeonBlue),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { isEmailLogin = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEmailLogin) NeonBlue.copy(alpha = 0.1f) else Color.Transparent,
                                contentColor = if (isEmailLogin) NeonBlue else Color.Gray
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = null
                        ) {
                            Text("Email", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { isEmailLogin = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isEmailLogin) NeonBlue.copy(alpha = 0.1f) else Color.Transparent,
                                contentColor = if (!isEmailLogin) NeonBlue else Color.Gray
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = null
                        ) {
                            Text("Phone", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Login Fields
                    OutlinedTextField(
                        value = if (isEmailLogin) email else phone,
                        onValueChange = { if (isEmailLogin) email = it else phone = it },
                        label = { Text(if (isEmailLogin) "Email Address" else "Phone Number") },
                        leadingIcon = {
                            Icon(
                                if (isEmailLogin) Icons.Default.Email else Icons.Default.Phone,
                                contentDescription = null,
                                tint = NeonBlue
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = NeonBlue.copy(alpha = 0.3f),
                            focusedTextColor = NeonBlue,
                            unfocusedTextColor = NeonBlue,
                            focusedLabelColor = NeonBlue,
                            unfocusedLabelColor = NeonBlue.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = pin,
                        onValueChange = { pin = it },
                        label = { Text("PIN Code") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonBlue) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = NeonBlue.copy(alpha = 0.7f)
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = NeonBlue.copy(alpha = 0.3f),
                            focusedTextColor = NeonBlue,
                            unfocusedTextColor = NeonBlue,
                            focusedLabelColor = NeonBlue,
                            unfocusedLabelColor = NeonBlue.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (isEmailLogin) {
                                viewModel.loginWithEmail(email, pin)
                            } else {
                                viewModel.loginWithPhone(phone, pin)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = NeonBlue),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonBlue
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.Black
                            )
                        } else {
                            Text("LOGIN", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Need an account? ", color = Color.Gray)
                        Text(
                            text = "Register Now",
                            color = NeonBlue,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.clickable {
                                onNavigateToRegister()
                            }
                        )
                    }
                }
            }

            // Error Message
            errorMessage?.let {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(12.dp), spotColor = NeonPink),
                    colors = CardDefaults.cardColors(containerColor = NeonPink.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink)
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(12.dp),
                        color = NeonPink,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}
