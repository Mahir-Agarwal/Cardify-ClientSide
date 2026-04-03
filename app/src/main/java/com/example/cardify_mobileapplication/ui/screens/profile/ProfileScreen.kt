package com.example.cardify_mobileapplication.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.cardify_mobileapplication.ui.navigation.Routes
import com.example.cardify_mobileapplication.ui.theme.*
import com.example.cardify_mobileapplication.ui.viewmodels.AppViewModelFactory
import com.example.cardify_mobileapplication.ui.viewmodels.AuthViewModel
import com.example.cardify_mobileapplication.ui.viewmodels.HomeViewModel
import com.example.cardify_mobileapplication.ui.screens.home.CardOwnerItem
import com.example.cardify_mobileapplication.ui.components.*
import com.example.cardify_mobileapplication.utils.UiState
import androidx.compose.runtime.*
import com.example.cardify_mobileapplication.ui.viewmodels.CardInfo

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current)),
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val userName by authViewModel.userName.collectAsState(initial = "...")
    val userRating by authViewModel.userRating.collectAsState(initial = 0.0)
    val totalOrders by authViewModel.totalOrders.collectAsState(initial = 0)
    val earnedAmount by authViewModel.earnedAmount.collectAsState(initial = 0.0)
    val cardsUiState by homeViewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        homeViewModel.fetchMyCards()
    }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(NeoWhite)
                    .border(3.dp, Color.Black, RectangleShape),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val navItems = listOf(
                    Pair("HOME", Routes.HOME),
                    Pair("ORDERS", Routes.ORDERS_LIST),
                    Pair("PROFILE", Routes.PROFILE)
                )
                navItems.forEachIndexed { index, (label, route) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (label == "PROFILE") NeoGreen else NeoWhite)
                            .clickable { if (label != "PROFILE") navController.navigate(route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, fontWeight = FontWeight.Black, color = NeoBlack)
                    }
                    if (index < navItems.size - 1) {
                        Box(modifier = Modifier.width(3.dp).fillMaxHeight().background(Color.Black))
                    }
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, Color.Black, RectangleShape)
                    .neoShadow(offsetY = 8f, offsetX = 0f)
                    .background(NeoBlue)
                    .statusBarsPadding()
                    .padding(vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .border(3.dp, Color.Black, RectangleShape)
                            .neoShadow(offsetY = 8f, offsetX = 8f)
                            .background(NeoGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = NeoBlack, modifier = Modifier.size(60.dp))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(userName?.uppercase() ?: "USER", style = MaterialTheme.typography.displaySmall, color = NeoWhite, fontWeight = FontWeight.Black)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard(if (userRating == null || userRating == 0.0) "NEW" else String.format("%.1f", userRating), "RATING", Icons.Default.Star)
                StatCard("${totalOrders ?: 0}", "ORDERS", Icons.Default.ShoppingCart)
                StatCard("$${String.format("%.1f", earnedAmount ?: 0.0)}", "EARNED", Icons.Default.ShoppingCart)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable { navController.navigate(Routes.MY_CARDS) },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("MY CARDS", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = NeoBlack)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("SEE ALL", fontWeight = FontWeight.Black, color = NeoBlue, style = MaterialTheme.typography.labelLarge)
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = NeoBlue)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
            ) {
                when (val state = cardsUiState) {
                    is UiState.Success -> {
                        val cards = state.data
                        if (cards.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("NO CARDS ADDED", color = NeoBlack, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                            }
                        } else {
                            val card = cards.first()
                            Box(modifier = Modifier.clickable { navController.navigate(Routes.MY_CARDS) }) {
                                CardOwnerItem(
                                    bankName = card.bankName,
                                    cardType = card.type,
                                    ownerName = card.owner,
                                    rating = card.rating,
                                    isActive = card.isAvailable,
                                    commission = card.commission,
                                    isOwnCard = true,
                                )
                            }
                        }
                    }
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().height(140.dp).border(3.dp, Color.Black).background(Color.LightGray))
                    }
                    else -> {}
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                NeoButton(
                    text = "LOG OUT",
                    backgroundColor = NeoRed,
                    onClick = {
                        authViewModel.logout()
                        navController.navigate(Routes.LOGIN) { 
                            popUpTo(0)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}


@Composable
fun StatCard(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .neoShadow(offsetY = 6f, offsetX = 6f)
            .border(3.dp, Color.Black, RectangleShape)
            .background(NeoWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, fontWeight = FontWeight.Black, color = NeoBlack, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, color = NeoBlack, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}
