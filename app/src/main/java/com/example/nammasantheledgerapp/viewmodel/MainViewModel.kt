package com.example.nammasantheledgerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammasantheledgerapp.data.database.Customer
import com.example.nammasantheledgerapp.data.database.PaymentMethod
import com.example.nammasantheledgerapp.data.database.Transaction
import com.example.nammasantheledgerapp.data.repository.SantheRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val repository: SantheRepository) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<com.example.nammasantheledgerapp.data.database.User?>(null)
    val currentUser: StateFlow<com.example.nammasantheledgerapp.data.database.User?> = _currentUser.asStateFlow()

    private val _dashboardData = MutableStateFlow(SantheRepository.DashboardData(0.0, 0.0, 0.0, 0.0, 0, 0, 0))
    val dashboardData: StateFlow<SantheRepository.DashboardData> = _dashboardData.asStateFlow()

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Customer>>(emptyList())
    val searchResults: StateFlow<List<Customer>> = _searchResults.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(repository.isDarkMode())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isLargeText = MutableStateFlow(repository.isLargeText())
    val isLargeText: StateFlow<Boolean> = _isLargeText.asStateFlow()

    private val _language = MutableStateFlow(repository.getLanguage())
    val language: StateFlow<String> = _language.asStateFlow()

    init {
        loadDashboardData()
        loadAllCustomers()
    }

    fun checkLoginStatus() {
        viewModelScope.launch {
            val user = repository.getLoggedInUser()
            _isLoggedIn.value = user != null
            _currentUser.value = user
        }
    }

    fun registerUser(username: String, email: String, phone: String, shopName: String, pin: String): Boolean {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.registerUser(username, email, phone, shopName, pin)
                _isLoggedIn.value = true
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
        return true
    }

    fun loginWithEmail(email: String, pin: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val user = repository.loginWithEmail(email, pin)
                if (user != null) {
                    _isLoggedIn.value = true
                    _currentUser.value = user
                } else {
                    _errorMessage.value = "Invalid email or PIN"
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun loginWithPhone(phone: String, pin: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val user = repository.loginWithPhone(phone, pin)
                if (user != null) {
                    _isLoggedIn.value = true
                    _currentUser.value = user
                } else {
                    _errorMessage.value = "Invalid phone or PIN"
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _isLoggedIn.value = false
            _currentUser.value = null
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _dashboardData.value = repository.getDashboardData()
        }
    }

    private fun loadAllCustomers() {
        viewModelScope.launch {
            repository.getAllCustomers().collect { customers ->
                _customers.value = customers
                loadDashboardData()
            }
        }
    }

    fun searchCustomers(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                _searchResults.value = emptyList()
            } else {
                repository.searchCustomers(query).collect { results ->
                    _searchResults.value = results
                }
            }
        }
    }

    fun addCustomer(name: String, phone: String, email: String, address: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.addCustomer(name, phone, email, address)
                loadAllCustomers()
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun addCredit(customerId: Long, amount: Double, note: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.addCredit(customerId, amount, note)
                loadDashboardData()
                loadCustomerTransactions(customerId)
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun addPayment(customerId: Long, amount: Double, paymentMethod: PaymentMethod, upiReference: String, note: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.addPayment(customerId, amount, paymentMethod, upiReference, note)
                loadDashboardData()
                loadCustomerTransactions(customerId)
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun loadCustomerTransactions(customerId: Long) {
        viewModelScope.launch {
            repository.getCustomerTransactions(customerId).collect { transactions ->
                _transactions.value = transactions
            }
        }
    }

    fun getCustomerById(customerId: Long): Customer? {
        return _customers.value.find { it.customerId == customerId }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun toggleDarkMode(enabled: Boolean) {
        repository.setDarkMode(enabled)
        _isDarkMode.value = enabled
    }

    fun toggleLargeText(enabled: Boolean) {
        repository.setLargeText(enabled)
        _isLargeText.value = enabled
    }

    fun setLanguage(lang: String) {
        repository.setLanguage(lang)
        _language.value = lang
    }
}

class MainViewModelFactory(private val repository: SantheRepository) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}