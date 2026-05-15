package com.example.nammasantheledgerapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nammasantheledgerapp.data.repository.SantheRepository
import com.example.nammasantheledgerapp.ui.theme.screens.*
import com.example.nammasantheledgerapp.ui.theme.NammaSantheTheme
import com.example.nammasantheledgerapp.viewmodel.MainViewModel
import com.example.nammasantheledgerapp.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // Handle permission denied
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check and request camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            val repository = SantheRepository(this)
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(repository)
            )

            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val isLargeText by viewModel.isLargeText.collectAsState()

            NammaSantheTheme(darkTheme = isDarkMode) {
                val baseTypography = MaterialTheme.typography
                val fontSizeMultiplier = if (isLargeText) 1.2f else 1.0f
                
                val customTypography = Typography(
                    bodyLarge = baseTypography.bodyLarge.copy(fontSize = baseTypography.bodyLarge.fontSize * fontSizeMultiplier),
                    bodyMedium = baseTypography.bodyMedium.copy(fontSize = baseTypography.bodyMedium.fontSize * fontSizeMultiplier),
                    bodySmall = baseTypography.bodySmall.copy(fontSize = baseTypography.bodySmall.fontSize * fontSizeMultiplier),
                    titleLarge = baseTypography.titleLarge.copy(fontSize = baseTypography.titleLarge.fontSize * fontSizeMultiplier),
                    titleMedium = baseTypography.titleMedium.copy(fontSize = baseTypography.titleMedium.fontSize * fontSizeMultiplier),
                    labelLarge = baseTypography.labelLarge.copy(fontSize = baseTypography.labelLarge.fontSize * fontSizeMultiplier)
                )

                MaterialTheme(
                    colorScheme = MaterialTheme.colorScheme,
                    typography = customTypography
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NammaSantheApp(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun NammaSantheApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkLoginStatus()
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToCustomers = { navController.navigate("customers") },
                onNavigateToQuickUdari = { navController.navigate("quick_udari") },
                onNavigateToSettings = { navController.navigate("settings") },
                onLogout = {
                    viewModel.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("customers") {
            CustomersScreen(
                viewModel = viewModel,
                onCustomerClick = { customerId ->
                    navController.navigate("customer_ledger/$customerId")
                }
            )
        }

        composable("customer_ledger/{customerId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId")?.toLongOrNull() ?: 0L
            CustomerLedgerScreen(
                viewModel = viewModel,
                customerId = customerId,
                onBack = { navController.popBackStack() },
                onPaymentClick = {
                    navController.navigate("payment_collection/$customerId")
                }
            )
        }

        composable("quick_udari") {
            QuickUdariScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("payment_collection/{customerId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId")?.toLongOrNull() ?: 0L
            PaymentCollectionScreen(
                viewModel = viewModel,
                customerId = customerId,
                onBack = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}