package com.example.cardify_mobileapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import com.example.cardify_mobileapplication.ui.screens.auth.LoginScreen
import com.example.cardify_mobileapplication.ui.screens.auth.RegisterScreen
import com.example.cardify_mobileapplication.ui.screens.home.HomeScreen
import com.example.cardify_mobileapplication.ui.screens.owner.AddCardScreen
import com.example.cardify_mobileapplication.ui.screens.order.OrderRequestScreen
import com.example.cardify_mobileapplication.ui.screens.order.OrderDetailScreen
import com.example.cardify_mobileapplication.ui.screens.order.OrdersListScreen
import com.example.cardify_mobileapplication.ui.screens.chat.ChatScreen
import com.example.cardify_mobileapplication.ui.screens.review.ReviewScreen
import com.example.cardify_mobileapplication.ui.screens.profile.ProfileScreen
import com.example.cardify_mobileapplication.ui.screens.profile.MyCardsScreen

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.unit.dp
import com.example.cardify_mobileapplication.data.local.TokenManager
import com.example.cardify_mobileapplication.ui.theme.NeoBlue
import com.example.cardify_mobileapplication.ui.theme.NeoGreen

@Composable
fun CardifyNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.jwtToken.collectAsState(initial = "loading")

    LaunchedEffect(token) {
        if (token != "loading") {
            if (token != null) {
                navController.navigate(Routes.HOME) {
                    popUpTo(0)
                }
            } else {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(0)
                }
            }
        }
    }

    if (token == "loading") {
        // Simple loading indicator while checking session
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NeoBlue, strokeWidth = 5.dp)
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = if (token == null) Routes.LOGIN else Routes.HOME
        ) {
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }
        composable(Routes.HOME) {
            HomeScreen(navController)
        }
        composable(Routes.ADD_CARD) {
            AddCardScreen(navController)
        }
        composable(
            route = "${Routes.ORDER_REQUEST}/{cardId}",
            arguments = listOf(navArgument("cardId") { type = NavType.StringType })
        ) {
            val cardId = it.arguments?.getString("cardId")
            OrderRequestScreen(navController, cardId)
        }
        composable(
            route = "${Routes.ORDER_DETAIL}/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) {
            val orderId = it.arguments?.getString("orderId")
            OrderDetailScreen(navController, orderId)
        }
        composable(
            route = "${Routes.CHAT}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            val userId = it.arguments?.getString("userId")
            ChatScreen(navController, userId)
        }
        composable(
            route = "${Routes.REVIEW}/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) {
            val orderId = it.arguments?.getString("orderId")
            ReviewScreen(navController, orderId)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(navController)
        }
        composable(Routes.ORDERS_LIST) {
            OrdersListScreen(navController)
        }
        composable(Routes.MY_CARDS) {
            MyCardsScreen(navController)
        }
    }
}
}
