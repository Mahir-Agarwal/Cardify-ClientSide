package com.example.cardify_mobileapplication.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.cardify_mobileapplication.ui.screens.home.CardOwnerItem
import com.example.cardify_mobileapplication.ui.theme.*
import com.example.cardify_mobileapplication.ui.viewmodels.AppViewModelFactory
import com.example.cardify_mobileapplication.ui.viewmodels.CardInfo
import com.example.cardify_mobileapplication.ui.viewmodels.HomeViewModel
import com.example.cardify_mobileapplication.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCardsScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val cardsUiState by homeViewModel.uiState.collectAsState()
    val editingCardState = remember { mutableStateOf<CardInfo?>(null) }
    val deletingCardState = remember { mutableStateOf<CardInfo?>(null) }

    LaunchedEffect(Unit) {
        homeViewModel.fetchMyCards()
    }

    // Dialogs
    val editingCard = editingCardState.value
    if (editingCard != null) {
        EditCardDialog(
            card = editingCard,
            onDismiss = { editingCardState.value = null },
            onConfirm = { id, bank, type, available ->
                homeViewModel.updateCard(id, bank, type, available)
                editingCardState.value = null
            }
        )
    }

    val deletingCard = deletingCardState.value
    if (deletingCard != null) {
        ConfirmDialog(
            title = "DELETE CARD?",
            message = "This action cannot be undone.",
            onDismiss = { deletingCardState.value = null },
            onConfirm = {
                homeViewModel.deleteCard(deletingCard.id)
                deletingCardState.value = null
            }
        )
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
                Text("MY CARDS", fontWeight = FontWeight.Black, style = MaterialTheme.typography.displaySmall, color = NeoBlack)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoLightGray)
                .padding(paddingValues)
        ) {
            when (val state = cardsUiState) {
                is UiState.Success -> {
                    val cards = state.data
                    if (cards.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("NO CARDS ADDED", color = NeoBlack, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(24.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(cards.size) { index ->
                                val card = cards[index]
                                CardOwnerItem(
                                    bankName = card.bankName,
                                    cardType = card.type,
                                    ownerName = card.owner,
                                    rating = card.rating,
                                    isActive = card.isAvailable,
                                    commission = card.commission,
                                    isOwnCard = true,
                                    onEditClick = { editingCardState.value = card },
                                    onDeleteClick = { deletingCardState.value = card }
                                )
                            }
                        }
                    }
                }
                is UiState.Loading -> {
                    LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        items(3) {
                            Box(modifier = Modifier.fillMaxWidth().height(140.dp).border(3.dp, Color.Black).background(Color.LightGray))
                        }
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = NeoRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                else -> {}
            }
        }
    }
}
