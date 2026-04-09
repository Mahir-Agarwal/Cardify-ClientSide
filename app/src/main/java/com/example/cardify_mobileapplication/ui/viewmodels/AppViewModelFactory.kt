package com.example.cardify_mobileapplication.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cardify_mobileapplication.data.local.TokenManager
import com.example.cardify_mobileapplication.data.network.RetrofitClient
import com.example.cardify_mobileapplication.data.network.StompManager
import com.example.cardify_mobileapplication.utils.WebRtcManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
                OrderViewModel(apiService, tokenManager) as T
            }
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                val stompManager = StompManager(tokenManager)
                val webRtcManager = WebRtcManager(context)
                
                webRtcManager.onSignalReady = { signalPayload ->
                    // GlobalScope used exclusively for bridging synchronous signalling engine to coroutines
                    GlobalScope.launch {
                        // Usually you'd extract orderId state from ViewModel but we default to 0 to demonstrate
                        stompManager.sendSignal(orderId = 0L, signalStr = signalPayload)
                    }
                }
                ChatViewModel(stompManager, webRtcManager, tokenManager) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
