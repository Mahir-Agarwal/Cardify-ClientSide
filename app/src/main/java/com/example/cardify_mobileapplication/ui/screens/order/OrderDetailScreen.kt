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
    REQUESTED, ACCEPTED, INFO_CONFIRMED, ESCROW_FUNDED, ORDER_PLACED, COMPLETED, DISPUTED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: String?,
    viewModel: OrderViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    var orderStatus by remember { mutableStateOf(OrderStatus.REQUESTED) }
    var orderRole by remember { mutableStateOf("USER") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchOrders()
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            val list = (uiState as UiState.Success<*>).data as? List<*>
            val currentOrder = list?.find { (it as? com.example.cardify_mobileapplication.ui.viewmodels.OrderInfo)?.id == orderId } as? com.example.cardify_mobileapplication.ui.viewmodels.OrderInfo
            currentOrder?.let {
                orderStatus = OrderStatus.valueOf(it.status)
                orderRole = it.role
            }
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
                            .clickable { navController.navigate("${Routes.CHAT}/${orderId ?: ""}") },
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
                        steps = listOf("REQ", "ACC", "INFO", "FUND", "PLC", "DONE")
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
                                    if (orderRole == "OWNER") {
                                        Text("ACTION REQUIRED: ACCEPT THIS REQUEST", color = NeoBlack, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(32.dp))
                                        NeoButton(text = "ACCEPT ORDER", onClick = { viewModel.acceptOrder(orderId ?: "") }, backgroundColor = NeoGreen)
                                    } else {
                                        Text("WAITING FOR OWNER TO ACCEPT.", color = NeoBlack, fontWeight = FontWeight.Bold)
                                    }
                                }
                                OrderStatus.ACCEPTED -> {
                                    if (orderRole == "BUYER") {
                                        Text("REQUEST ACCEPTED! PLEASE SHARE AMAZON LINKS & DELIVERY ADDRESS IN CHAT.", color = NeoBlack, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Waiting for Owner to confirm they understand the instructions.", color = NeoBlack, style = MaterialTheme.typography.bodyMedium)
                                    } else {
                                        Text("PLEASE REVIEW CHAT. ONCE YOU UNDERSTAND THE BUYER'S INSTRUCTIONS, CONFIRM BELOW.", color = NeoBlack, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(32.dp))
                                        NeoButton(text = "INFO CONFIRMED & UNDERSTOOD", onClick = { viewModel.confirmInfo(orderId ?: "") }, backgroundColor = NeoBlue)
                                    }
                                }
                                OrderStatus.INFO_CONFIRMED -> {
                                    if (orderRole == "BUYER") {
                                        Text("OWNER CONFIRMED INSTRUCTIONS. PLEASE FUND THE ESCROW.", color = NeoBlack, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(32.dp))
                                        NeoButton(text = "PAY ESCROW securely", onClick = { viewModel.simulatePayment(orderId ?: "") }, backgroundColor = NeoBlue)
                                    } else {
                                        Text("AWAITING BUYER ESCROW PAYMENT. DO NOT PURCHASE ANYTHING YET.", color = NeoBlack, fontWeight = FontWeight.Bold)
                                    }
                                }
                                OrderStatus.ESCROW_FUNDED -> {
                                    if (orderRole == "OWNER") {
                                        Text("ESCROW FUNDED 100%. PLEASE PURCHASE THE ITEM ON AMAZON NOW.", color = NeoBlack, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(32.dp))
                                        NeoButton(text = "MARK AS ORDERED & SECURED", onClick = { viewModel.placeOrder(orderId ?: "") }, backgroundColor = NeoGreen)
                                    } else {
                                        Text("ESCROW FUNDED. OWNER IS PLACING THE EXTERNAL ORDER.", color = NeoBlack, fontWeight = FontWeight.Bold)
                                    }
                                }
                                OrderStatus.ORDER_PLACED -> {
                                    if (orderRole == "BUYER") {
                                        Text("ORDER HAS BEEN PLACED. AWAITING DELIVERY AT YOUR ADDRESS.", color = NeoBlack, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(32.dp))
                                        NeoButton(text = "ITEM DELIVERED (RELEASE ESCROW)", onClick = { viewModel.markDelivered(orderId ?: "") }, backgroundColor = NeoGreen)
                                    } else {
                                        Text("ORDER PLACED. AWAITING BUYER TO VERIFY DELIVERY.", color = NeoBlack, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(32.dp))
                                        NeoButton(text = "RAISE DISPUTE (MISSING PAYMENT)", onClick = { viewModel.disputeOrder(orderId ?: "") }, backgroundColor = NeoRed)
                                    }
                                }
                                OrderStatus.COMPLETED -> {
                                    Text("ORDER COMPLETED! ESCROW FUNDS RELEASED.", color = NeoBlack, fontWeight = FontWeight.Black)
                                    Spacer(modifier = Modifier.height(32.dp))
                                    if (orderRole == "BUYER") {
                                        NeoButton(text = "LEAVE A REVIEW", onClick = { navController.navigate("${Routes.REVIEW}/${orderId ?: ""}") }, backgroundColor = NeoGreen)
                                    }
                                }
                                OrderStatus.DISPUTED -> {
                                    Text("ORDER DISPUTED. ADMIN INVESTIGATION REQUIRED.", color = NeoRed, fontWeight = FontWeight.Black)
                                    Spacer(modifier = Modifier.height(32.dp))
                                    Text("A Cardify moderator will review the tracking and chat logs to resolve this transaction.", color = NeoBlack, style = MaterialTheme.typography.bodyMedium)
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
