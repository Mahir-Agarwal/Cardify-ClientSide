package com.example.cardify_mobileapplication.ui.screens.review

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun ReviewScreen(
    navController: NavController, 
    orderId: String?,
    viewModel: OrderViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    var showConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NeoLightGray)
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
                Text("LEAVE A REVIEW", fontWeight = FontWeight.Black, style = MaterialTheme.typography.headlineMedium, color = NeoBlack)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoLightGray)
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showConfirmation) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = NeoBlack,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("SUCCESS!", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = NeoBlack)
                Spacer(modifier = Modifier.height(16.dp))
                Text("YOUR FEEDBACK HAS BEEN RECORDED.", style = MaterialTheme.typography.bodyLarge, color = NeoBlack, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.weight(1f))
                NeoButton(
                    text = "BACK TO HOME",
                    onClick = { navController.navigate(Routes.HOME) { popUpTo(0) } },
                    backgroundColor = NeoGreen
                )
            } else {
                Spacer(modifier = Modifier.height(32.dp))
                NeoCard {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("RATE THE OWNER", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = NeoBlack)
                        
                        Spacer(modifier = Modifier.height(48.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (i in 1..5) {
                                Icon(
                                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Star $i",
                                    tint = NeoBlack,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clickable { rating = i }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            label = { Text("ENTER FEEDBACK", fontWeight = FontWeight.Black, color = NeoBlack) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .neoShadow(8f, 8f),
                            shape = RectangleShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black,
                                focusedContainerColor = NeoWhite,
                                unfocusedContainerColor = NeoWhite,
                                cursorColor = NeoBlack
                            ),
                            maxLines = 5
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                NeoButton(
                    text = "SUBMIT REVIEW",
                    onClick = { 
                        if (orderId != null) {
                            viewModel.submitReview(orderId, rating, reviewText)
                        }
                        showConfirmation = true 
                    },
                    enabled = rating > 0,
                    backgroundColor = NeoBlue
                )
            }
        }
    }
}
