package com.example.cardify_mobileapplication.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cardify_mobileapplication.ui.components.NeoTextField
import com.example.cardify_mobileapplication.ui.components.neoShadow
import com.example.cardify_mobileapplication.ui.theme.*
import com.example.cardify_mobileapplication.ui.viewmodels.AppViewModelFactory
import com.example.cardify_mobileapplication.ui.viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    orderId: Long,
    viewModel: ChatViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.connectAndSubscribe(orderId)
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, Color.Black, RectangleShape)
                    .background(NeoLightGray)
                    .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ArrowBack, 
                    contentDescription = "Back", 
                    tint = NeoBlack,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("ORDER #$orderId CHAT", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.weight(1f))
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NeoWhite)
                    .border(3.dp, Color.Black, RectangleShape)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    NeoTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        label = "TYPE MESSAGE",
                        isError = false
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .neoShadow(offsetY = 4f, offsetX = 4f)
                        .border(3.dp, Color.Black, RectangleShape)
                        .background(NeoBlue)
                        .clickable {
                            if (messageText.isNotBlank()) {
                                // If they type SECURE:, route via WebRTC zero-leak channel
                                if (messageText.startsWith("SECURE:", ignoreCase = true)) {
                                    viewModel.sendSecureCardData(messageText.drop(7).trim())
                                } else {
                                    viewModel.sendTextMessage(orderId, messageText)
                                }
                                messageText = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = NeoWhite)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoLightGray)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .neoShadow(offsetY = 12f, offsetX = 12f)
                                    .border(4.dp, NeoBlack, RectangleShape)
                                    .background(Color(0xFFFFD500)) // Brutalist Core Yellow
                                    .padding(32.dp)
                            ) {
                                Text(
                                    "💬",
                                    style = MaterialTheme.typography.displayLarge
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    "DEAD SILENT.",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Black,
                                    color = NeoBlack
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Send a message below to securely start the conversation about this order.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NeoBlack,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
                
                items(messages) { msg ->
                    val isSecure = msg.isSecure
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (msg.isMe) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .neoShadow(offsetY = 6f, offsetX = 6f)
                                .border(3.dp, Color.Black, RectangleShape)
                                .background(if (msg.isMe) NeoGreen else if (isSecure) NeoBlue else NeoWhite)
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSecure) {
                                    Icon(Icons.Default.Lock, contentDescription = "Secure", tint = NeoWhite)
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = msg.text, 
                                    color = if (isSecure) NeoWhite else NeoBlack, 
                                    fontWeight = if (isSecure) FontWeight.Black else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
