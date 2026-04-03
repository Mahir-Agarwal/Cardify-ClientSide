package com.example.cardify_mobileapplication.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersListScreen(
    navController: NavController,
    viewModel: OrderViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchOrders()
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NeoLightGray)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
                Spacer(modifier = Modifier.width(24.dp))
                Text("MY ORDERS", fontWeight = FontWeight.Black, style = MaterialTheme.typography.displaySmall, color = NeoBlack)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoLightGray)
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        items(7) {
                            Box(modifier = Modifier.fillMaxWidth().height(100.dp).border(3.dp, Color.Black).background(Color.LightGray))
                        }
                    }
                }
                is UiState.Success -> {
                    val orders = state.data
                    if (orders.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .neoShadow(offsetY = 8f, offsetX = 8f)
                                    .border(3.dp, Color.Black, RectangleShape)
                                    .background(NeoBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart, 
                                    contentDescription = null, 
                                    tint = NeoWhite, 
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                "NO ORDERS YET", 
                                color = NeoBlack, 
                                fontWeight = FontWeight.Black, 
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Start shopping to see your orders here!", 
                                color = Color.Gray, 
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(40.dp))
                            NeoButton(
                                text = "BROWSE MARKETPLACE",
                                onClick = { navController.navigate(Routes.HOME) },
                                backgroundColor = NeoGreen
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(24.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(orders.size) { index ->
                                val order = orders[index]
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .neoShadow(offsetY = 8f, offsetX = 8f)
                                        .border(3.dp, Color.Black, RectangleShape)
                                        .background(NeoWhite)
                                        .clickable { navController.navigate("${Routes.ORDER_DETAIL}/${order.id}") }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                "ORDER #${order.id}",
                                                fontWeight = FontWeight.Black,
                                                color = NeoBlack,
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                "AMOUNT: ${order.amount}",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = NeoBlack
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .border(2.dp, Color.Black, RectangleShape)
                                                .neoShadow(offsetY = 4f, offsetX = 4f)
                                                .background(if (order.status == "COMPLETED") NeoGreen else NeoBlue)
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(order.status, color = NeoBlack, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "FAILED TO LOAD ORDERS", 
                            color = NeoRed, 
                            fontWeight = FontWeight.Black, 
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(state.message, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(32.dp))
                        NeoButton(
                            text = "RETRY",
                            onClick = { viewModel.fetchOrders() },
                            backgroundColor = NeoWhite
                        )
                    }
                }
                else -> {}
            }
        }
    }
}
