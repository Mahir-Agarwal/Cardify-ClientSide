package com.example.cardify_mobileapplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardify_mobileapplication.data.network.StompManager
import com.example.cardify_mobileapplication.utils.WebRtcManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import com.google.gson.Gson
import com.example.cardify_mobileapplication.data.local.TokenManager
import android.util.Log

data class ChatMessageUi(val text: String, val isMe: Boolean, val isSecure: Boolean)
data class IncomingChatMessage(val senderId: Long?, val message: String?)

class ChatViewModel(
    private val stompManager: StompManager,
    private val webRtcManager: WebRtcManager,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessageUi>>(emptyList())
    val messages = _messages.asStateFlow()

    fun connectAndSubscribe(orderId: Long) {
        viewModelScope.launch {
            stompManager.connect()
            stompManager.subscribeToOrder(orderId)

            stompManager.incomingMessages.collect { payload ->
                // Inspect payload. If it's a signaling frame, route to WebRTC
                if (payload.contains("\"type\":\"offer\"") || 
                    payload.contains("\"type\":\"answer\"") || 
                    payload.contains("\"type\":\"candidate\"")) {
                    webRtcManager.handleIncomingSignal(payload)
                } else {
                    val currentUserId = tokenManager.userId.firstOrNull() ?: -1L
                    Log.d("ChatApp", "Parsing STOMP. Evaluating against my userId = $currentUserId")
                    
                    val parsed = try { 
                        Gson().fromJson(payload, IncomingChatMessage::class.java) 
                    } catch(e: Exception) { 
                        Log.e("ChatApp", "Gson parsing error: ${e.message}")
                        null 
                    }
                    val displayMsg = parsed?.message ?: payload
                    val isMe = parsed?.senderId == currentUserId
                    
                    Log.d("ChatApp", "Extracted senderId=${parsed?.senderId}, message=$displayMsg, isMe evaluates to: $isMe")
                    
                    // We optimistically render our own messages on send.
                    // Only render incoming STOMP frames if they originate from the other user!
                    if (!isMe) {
                        Log.d("ChatApp", "Appending INCOMING message to UI!")
                        val currentList = _messages.value.toMutableList()
                        currentList.add(ChatMessageUi(text = displayMsg, isMe = false, isSecure = false))
                        _messages.value = currentList
                    } else {
                        Log.d("ChatApp", "Ignoring own message echo from STOMP to prevent duplication.")
                    }
                }
            }
        }

        // Listen for deeply secure data payloads directly from the WebRTC data channel
        viewModelScope.launch {
            webRtcManager.secureMessages.collect { secureMsg ->
                secureMsg?.let {
                    val currentList = _messages.value.toMutableList()
                    currentList.add(ChatMessageUi(text = it, isMe = true, isSecure = true))
                    _messages.value = currentList
                }
            }
        }
    }

    fun sendTextMessage(orderId: Long, text: String) {
        viewModelScope.launch {
            val currentUserId = tokenManager.userId.firstOrNull() ?: -1L
            // Safely serialize with Gson to escape any multiline \r\n or quotes automatically
            val payloadMap = mapOf(
                "orderId" to orderId,
                "senderId" to currentUserId,
                "message" to text
            )
            val payload = Gson().toJson(payloadMap)
            stompManager.sendMessage(orderId, payload)
            
            // OPTIMISTIC UI APPEND: Guarantee the user's message appears on screen securely without relying purely on backend echoes.
            val currentList = _messages.value.toMutableList()
            currentList.add(ChatMessageUi(text = text, isMe = true, isSecure = false))
            _messages.value = currentList
        }
    }

    fun sendSecureCardData(data: String) {
        webRtcManager.sendSecureData(data)
    }

    fun startP2PHandshake(orderId: Long) {
        webRtcManager.startPeerConnection(isInitiator = true)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            stompManager.disconnect()
        }
        webRtcManager.dispose()
    }
}
