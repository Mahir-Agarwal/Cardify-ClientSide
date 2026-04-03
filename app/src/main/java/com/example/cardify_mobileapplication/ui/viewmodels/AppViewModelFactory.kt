package com.example.cardify_mobileapplication.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cardify_mobileapplication.data.local.TokenManager
import com.example.cardify_mobileapplication.data.network.RetrofitClient

class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val apiService = RetrofitClient.getApiService(context)
        val tokenManager = TokenManager(context)

        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(apiService, tokenManager) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(apiService) as T
            }
            modelClass.isAssignableFrom(OrderViewModel::class.java) -> {
                OrderViewModel(apiService) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
