package com.example.cardify_mobileapplication.ui.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.cardify_mobileapplication.data.local.TokenManager
import com.example.cardify_mobileapplication.data.network.ApiService
import com.example.cardify_mobileapplication.data.network.LoginRequest
import com.example.cardify_mobileapplication.data.network.RegisterRequest
import com.example.cardify_mobileapplication.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AuthViewModel(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : BaseViewModel<String>() {
    val userName = tokenManager.userName
    val userRating = tokenManager.userRating
    val totalOrders = tokenManager.totalOrders
    val earnedAmount = tokenManager.earnedAmount

    fun login(email: String, pass: String) {
        android.util.Log.d("AuthViewModel", "LOGIN ATTEMPT - Email: $email")
        executeUseCase {
            if (email.isBlank() || pass.isBlank()) {
                throw Exception("Provide email and password")
            }
            
            val response = apiService.login(LoginRequest(email, pass))
            tokenManager.saveToken(response.token)
            response.name?.let { tokenManager.saveUserName(it) }
            tokenManager.saveProfileStats(
                response.rating ?: 0.0,
                response.totalOrders ?: 0,
                response.earnedAmount ?: 0.0
            )
            "Login Success"
        }
    }
    
    fun register(name: String, email: String, pass: String, role: String) {
        executeUseCase {
            if (name.isBlank() || email.isBlank() || pass.isBlank()) {
                throw Exception("Provide all details")
            }
            val finalRole = if (role.uppercase() == "BUYER") listOf("BUYER") else listOf("BUYER", "OWNER")
            
            val response = apiService.register(RegisterRequest(name, email, pass, finalRole))
            tokenManager.saveToken(response.token)
            response.name?.let { tokenManager.saveUserName(it) }
            tokenManager.saveProfileStats(
                response.rating ?: 0.0,
                response.totalOrders ?: 0,
                response.earnedAmount ?: 0.0
            )
            "Registration Success"
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
        }
    }
}
