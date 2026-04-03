package com.example.cardify_mobileapplication.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cardify_mobileapplication.ui.components.*
import com.example.cardify_mobileapplication.ui.navigation.Routes
import com.example.cardify_mobileapplication.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.cardify_mobileapplication.ui.viewmodels.AppViewModelFactory
import com.example.cardify_mobileapplication.ui.viewmodels.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderRequestScreen(
    navController: NavController, 
    cardId: String?,
    viewModel: OrderViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    var amount by remember { mutableStateOf("") }
    val commissionRate = 0.025 // 2.5%
    val amountValue = amount.toDoubleOrNull() ?: 0.0
    val commissionValue = amountValue * commissionRate

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NeoLightGray)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
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
                Text("REQUEST ORDER", fontWeight = FontWeight.Black, style = MaterialTheme.typography.headlineMedium, color = NeoBlack)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoLightGray)
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            NeoCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .border(3.dp, Color.Black, RectangleShape)
                            .background(NeoBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = NeoWhite, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("HDFC CREDIT CARD", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = NeoBlack)
                        Text("OWNER: JANE DOE", style = MaterialTheme.typography.labelLarge, color = NeoBlack, fontWeight = FontWeight.Bold)
                        Text("COMMISSION: 2.5%", style = MaterialTheme.typography.labelMedium, color = NeoRed, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            NeoTextField(
                value = amount,
                onValueChange = { amount = it },
                label = "ORDER AMOUNT ($)",
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Commission Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .neoShadow(offsetY = 6f, offsetX = 6f)
                    .border(3.dp, Color.Black, RectangleShape)
                    .background(NeoWhite)
                    .padding(24.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ORDER VALUE", color = NeoBlack, fontWeight = FontWeight.Bold)
                        Text("$${String.format("%.2f", amountValue)}", fontWeight = FontWeight.Black, color = NeoBlack)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("OWNER COMMISSION", color = NeoBlack, fontWeight = FontWeight.Bold)
                        Text("$${String.format("%.2f", commissionValue)}", fontWeight = FontWeight.Black, color = NeoRed)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.Black, thickness = 3.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TOTAL COST", fontWeight = FontWeight.Black, color = NeoBlack, style = MaterialTheme.typography.titleLarge)
                        Text("$${String.format("%.2f", amountValue + commissionValue)}", fontWeight = FontWeight.Black, color = NeoGreen, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            NeoButton(
                text = "SUBMIT REQUEST",
                onClick = {
                    val finalCardId = cardId?.toIntOrNull() ?: 1
                    viewModel.requestOrder(finalCardId, amountValue)
                    navController.navigate("${Routes.ORDER_DETAIL}/new_order")
                },
                enabled = amountValue > 0,
                backgroundColor = NeoBlue
            )
        }
    }
}
