package com.example.cardify_mobileapplication.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
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
import com.example.cardify_mobileapplication.ui.navigation.Routes
import com.example.cardify_mobileapplication.ui.theme.*
import com.example.cardify_mobileapplication.ui.viewmodels.AppViewModelFactory
import com.example.cardify_mobileapplication.ui.viewmodels.OrderViewModel
import com.example.cardify_mobileapplication.utils.UiState

enum class OrderStatus {
    REQUESTED, ACCEPTED, PAID, ORDER_PLACED, COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: String?,
    viewModel: OrderViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    var orderStatus by remember { mutableStateOf(OrderStatus.REQUESTED) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success && (uiState as UiState.Success<*>).data is OrderStatus) {
            orderStatus = (uiState as UiState.Success<*>).data as OrderStatus
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NeoLightGray)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                        Text("ORDER #${orderId?.takeLast(5) ?: "00000"}", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge, color = NeoBlack)
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(3.dp, Color.Black, RectangleShape)
                            .neoShadow(offsetY = 4f, offsetX = 4f)
                            .background(NeoGreen)
                            .clickable { navController.navigate("${Routes.CHAT}/owner_123") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", tint = NeoBlack)
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NeoLightGray)
                    .padding(paddingValues),
                contentPadding = PaddingValues(24.dp)
            ) {
                item {
                    NeoStepper(
                        modifier = Modifier.fillMaxWidth(),
                        currentStep = orderStatus.ordinal,
                        steps = listOf(
                            "REQ",
                            "ACC",
                            "PAY",
                            "PLC",
                            "DONE"
                        )
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                }

                item {
                    Text("ACTION REQUIRED", fontWeight = FontWeight.Black, color = NeoBlack, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))

                    NeoCard {
                        Column {
                            when (orderStatus) {
                                OrderStatus.REQUESTED -> {
                                    Text("WAITING FOR OWNER TO ACCEPT YOUR REQUEST.", color = NeoBlack, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(32.dp))
                                    NeoButton(
                                        text = "SIMULATE OWNER ACCEPT",
                                        onClick = { viewModel.acceptOrder(orderId ?: "123") },
                                        backgroundColor = NeoBlue
                                    )
                                }
                                OrderStatus.ACCEPTED -> {
                                    Text("REQUEST ACCEPTED! PLEASE PAY THE ESCROW AMOUNT.", color = NeoBlack, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(32.dp))
                                    NeoButton(
                                        text = "PAY ESCROW ($1,250)",
                                        onClick = { viewModel.simulatePayment() },
                                        backgroundColor = NeoGreen
                                    )
                                }
                                OrderStatus.PAID -> {
                                    Text("PAYMENT CONFIRMED. WAITING FOR ORDER PLACEMENT.", color = NeoBlack, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(32.dp))
                                    NeoButton(
                                        text = "SIMULATE ORDER PLACE",
                                        onClick = { viewModel.placeOrder(orderId ?: "123") },
                                        backgroundColor = NeoBlue
                                    )
                                }
                                OrderStatus.ORDER_PLACED -> {
                                    Text("ORDER HAS BEEN PLACED SUCCESSFULLY.", color = NeoBlack, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(32.dp))
                                    NeoButton(
                                        text = "CONFIRM DELIVERY",
                                        onClick = { viewModel.confirmDelivery(orderId ?: "123") },
                                        backgroundColor = NeoGreen
                                    )
                                }
                                OrderStatus.COMPLETED -> {
                                    Text("ORDER COMPLETED! FUNDS RELEASED.", color = NeoBlack, fontWeight = FontWeight.Black)
                                    Spacer(modifier = Modifier.height(32.dp))
                                    NeoButton(
                                        text = "LEAVE A REVIEW",
                                        onClick = { navController.navigate("${Routes.REVIEW}/${orderId ?: "unknown"}") },
                                        backgroundColor = NeoBlue
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (uiState is UiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NeoLightGray.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = NeoBlack, strokeWidth = 8.dp, modifier = Modifier.size(80.dp).border(4.dp, Color.Black, RectangleShape))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("PROCESSING...", style = MaterialTheme.typography.displaySmall, color = NeoBlack, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
