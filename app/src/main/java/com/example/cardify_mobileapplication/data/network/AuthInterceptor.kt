package com.example.cardify_mobileapplication.data.network

import com.example.cardify_mobileapplication.data.local.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val token = runBlocking { tokenManager.jwtToken.firstOrNull() }
        
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        
        val response = chain.proceed(requestBuilder.build())
        
        // Auto-logout strategy when token is expired or unauthorized
        if (response.code == 401 || response.code == 403) {
            runBlocking {
                tokenManager.clearToken()
            }
        }
        
        return response
    }
}
