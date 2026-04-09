package com.example.cardify_mobileapplication.data.network

import com.example.cardify_mobileapplication.data.local.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.headers.StompConnectHeaders
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import java.util.concurrent.TimeUnit
import android.util.Log

class StompManager(private val tokenManager: TokenManager) {
    
    private val okHttpClient = OkHttpClient.Builder()
        .pingInterval(10, TimeUnit.SECONDS)
        .build()

    private val wsClient = OkHttpWebSocketClient(okHttpClient)
    private val stompClient = StompClient(wsClient)

    private var session: StompSession? = null

    // For emitting incoming raw payload strings
    private val _incomingMessages = MutableSharedFlow<String>()
    val incomingMessages = _incomingMessages.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun connect() {
        if (session != null) {
            Log.d("ChatApp", "STOMP Connection already active.")
            return
        }
        
        val token = tokenManager.jwtToken.firstOrNull()
        if (token == null) {
            Log.e("ChatApp", "STOMP Connect Failed: JWT Token is NULL!")
            return
        }
        
        Log.d("ChatApp", "Attempting STOMP Connection to ${RetrofitClient.WS_BASE_URL}...")
        try {
            session = stompClient.connect(
                url = RetrofitClient.WS_BASE_URL,
                customStompConnectHeaders = mapOf("Authorization" to "Bearer $token")
            )
            Log.d("ChatApp", "STOMP Connection SUCCESSFUL!")
        } catch (e: Exception) {
            Log.e("ChatApp", "STOMP Connection FAILED: ${e.message}")
        }
    }

    suspend fun subscribeToOrder(orderId: Long) {
        val currentSession = session
        if (currentSession == null) {
            Log.e("ChatApp", "Cannot subscribe to Order $orderId: Session is NULL!")
            return
        }
        
        scope.launch {
            Log.d("ChatApp", "Subscribing to STOMP Topic: /topic/order.$orderId")
            try {
                val subscription = currentSession.subscribeText("/topic/order.$orderId")
                subscription.collect { frame ->
                    Log.d("ChatApp", "Incoming STOMP Frame received: $frame")
                    _incomingMessages.emit(frame)
                }
            } catch (e: Exception) {
                Log.e("ChatApp", "STOMP Subscription FAILED: ${e.message}")
            }
        }
    }

    suspend fun sendMessage(orderId: Long, messageStr: String) {
        Log.d("ChatApp", "Sending STOMP Frame to /app/chat.sendMessage: $messageStr")
        session?.sendText(
            destination = "/app/chat.sendMessage",
            body = messageStr
        )
    }

    suspend fun sendSignal(orderId: Long, signalStr: String) {
        session?.sendText(
            destination = "/app/chat.signal",
            body = signalStr
        )
    }

    suspend fun disconnect() {
        session?.disconnect()
        session = null
    }
}
