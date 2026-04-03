package com.example.cardify_mobileapplication.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.cardify_mobileapplication.ui.components.*
import com.example.cardify_mobileapplication.ui.theme.*
import com.example.cardify_mobileapplication.ui.viewmodels.AppViewModelFactory
import com.example.cardify_mobileapplication.ui.viewmodels.OrderViewModel

data class ChatMessage(val text: String, val isMine: Boolean, val timestamp: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController, 
    userId: String?,
    viewModel: OrderViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage("Hi, I want to request your HDFC card for an order.", isMine = true, timestamp = "10:00 AM"),
            ChatMessage("Sure! The order amount should be less than $12,000.", isMine = false, timestamp = "10:05 AM"),
            ChatMessage("Sounds good. I'll place the request now.", isMine = true, timestamp = "10:06 AM")
        )
    }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NeoLightGray)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(3.dp, Color.Black, RectangleShape)
                            .neoShadow(offsetY = 4f, offsetX = 4f)
                            .background(NeoWhite)
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeoBlack)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier.size(48.dp).border(3.dp, Color.Black, RectangleShape).background(NeoBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("J", color = NeoWhite, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("JANE DOE", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = NeoBlack)
                        Text("ONLINE", style = MaterialTheme.typography.labelSmall, color = NeoGreen, fontWeight = FontWeight.Black)
                    }
                }
                HorizontalDivider(thickness = 3.dp, color = Color.Black)
            }
        },
        bottomBar = {
            Column {
                HorizontalDivider(thickness = 3.dp, color = Color.Black)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NeoWhite)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = { Text("TYPE MESSAGE...", color = NeoBlack, fontWeight = FontWeight.Bold) },
                            modifier = Modifier
                                .weight(1f)
                                .neoShadow(4f, 4f),
                            shape = RectangleShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black,
                                focusedContainerColor = NeoWhite,
                                unfocusedContainerColor = NeoWhite,
                                cursorColor = NeoBlack
                            )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .neoShadow(offsetY = 4f, offsetX = 4f)
                                .border(3.dp, Color.Black, RectangleShape)
                                .background(NeoGreen)
                                .clickable {
                                    if (messageText.isNotBlank()) {
                                        viewModel.sendChatMessage("123", messageText)
                                        messages.add(ChatMessage(messageText, true, "NOW"))
                                        messageText = ""
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = NeoBlack)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoLightGray)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .neoShadow(offsetY = 4f, offsetX = if (message.isMine) -4f else 4f)
                    .border(3.dp, Color.Black, RectangleShape)
                    .background(if (message.isMine) NeoBlue else NeoWhite)
                    .padding(16.dp)
            ) {
                Text(
                    text = message.text.uppercase(),
                    color = if (message.isMine) NeoWhite else NeoBlack,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.labelMedium,
                color = NeoBlack,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
