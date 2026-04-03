package com.example.cardify_mobileapplication.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.cardify_mobileapplication.ui.viewmodels.AuthViewModel
import com.example.cardify_mobileapplication.ui.viewmodels.HomeViewModel
import com.example.cardify_mobileapplication.ui.viewmodels.AppViewModelFactory
import com.example.cardify_mobileapplication.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current)),
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUserName by authViewModel.userName.collectAsState(initial = "")
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("ALL", "CREDIT", "DEBIT", "PREPAID", "HDFC", "ICICI", "SBI", "AXIS")
    var selectedFilters by remember { mutableStateOf(setOf("ALL")) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Combined search and filter effect
    LaunchedEffect(searchQuery, selectedFilters) {
        val typeFilter = selectedFilters.find { it in listOf("CREDIT", "DEBIT", "PREPAID") }
        val bankFilter = selectedFilters.find { it in listOf("HDFC", "ICICI", "SBI", "AXIS") }
        
        // Use searchQuery if not empty, otherwise use bank chip
        val finalBank = if (searchQuery.isNotBlank()) searchQuery else bankFilter
        
        viewModel.fetchCards(query = finalBank, type = typeFilter)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                            .background(if (label == "HOME") NeoGreen else NeoWhite)
                            .clickable { if (label != "HOME") navController.navigate(route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, fontWeight = FontWeight.Black, color = NeoBlack)
                    }
                    if (index < navItems.size - 1) {
                        Box(modifier = Modifier.width(3.dp).fillMaxHeight().background(Color.Black))
                    }
                }
            }
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .neoShadow(offsetY = 8f, offsetX = 8f)
                    .border(3.dp, Color.Black, RectangleShape)
                    .background(NeoBlue)
                    .clickable { navController.navigate(Routes.ADD_CARD) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Card", tint = NeoWhite, modifier = Modifier.size(32.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoLightGray)
                .padding(paddingValues)
        ) {
            HeaderSection(
                searchQuery, 
                { searchQuery = it }, 
                filters, 
                selectedFilters, 
                { filter ->
                    val newSet = selectedFilters.toMutableSet()
                    if (filter == "ALL") {
                        newSet.clear()
                        newSet.add("ALL")
                    } else {
                        newSet.remove("ALL")
                        if (newSet.contains(filter)) {
                            newSet.remove(filter)
                            if (newSet.isEmpty()) newSet.add("ALL")
                        } else {
                            // If it's a type, remove other types
                            if (filter in listOf("CREDIT", "DEBIT", "PREPAID")) {
                                newSet.removeAll(listOf("CREDIT", "DEBIT", "PREPAID"))
                            }
                            // If it's a bank, remove other banks
                            if (filter in listOf("HDFC", "ICICI", "SBI", "AXIS")) {
                                newSet.removeAll(listOf("HDFC", "ICICI", "SBI", "AXIS"))
                            }
                            newSet.add(filter)
                        }
                    }
                    selectedFilters = newSet
                }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is UiState.Loading -> {
                        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(5) {
                                Box(modifier = Modifier.fillMaxWidth().height(140.dp).border(3.dp, Color.Black).background(Color.LightGray))
                            }
                        }
                    }
                    is UiState.Error -> {
                        LaunchedEffect(state.message) {
                            snackbarHostState.showSnackbar(state.message)
                        }
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("SYSTEM ERROR.", color = NeoRed, fontWeight = FontWeight.Black, style = MaterialTheme.typography.displaySmall)
                                Spacer(modifier = Modifier.height(24.dp))
                                NeoButton(
                                    text = "RETRY", 
                                    onClick = { 
                                        val typeFilter = selectedFilters.find { it in listOf("CREDIT", "DEBIT", "PREPAID") }
                                        viewModel.fetchCards(searchQuery, typeFilter) 
                                    },
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )
                            }
                        }
                    }
                    is UiState.Success -> {
                        val cards = state.data.filter { !it.owner.equals(currentUserName, ignoreCase = true) }
                        if (cards.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("NO CARDS FOUND", color = NeoBlack, fontWeight = FontWeight.Black, style = MaterialTheme.typography.displaySmall)
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(cards) { card ->
                                    CardOwnerItem(
                                        bankName = card.bankName,
                                        cardType = card.type,
                                        ownerName = card.owner,
                                        rating = card.rating,
                                        isActive = card.isAvailable,
                                        commission = card.commission,
                                        isOwnCard = false, // Always false since we filtered them out
                                        onRequestClick = { 
                                            navController.navigate("${Routes.ORDER_REQUEST}/${card.id}")
                                        }
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(80.dp)) }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun HeaderSection(
    searchQuery: String, onSearchChange: (String) -> Unit,
    filters: List<String>, selectedFilters: Set<String>, onFilterChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("CARDIFY", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = NeoBlack)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(3.dp, Color.Black, RectangleShape)
                    .background(NeoGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = NeoBlack)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        NeoTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = "SEARCH MARKETPLACE",
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = NeoBlack) }
        )
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(filters) { filter ->
            val isSelected = selectedFilters.contains(filter)
            Box(
                modifier = Modifier
                    .neoShadow(offsetY = if(isSelected) 6f else 0f, offsetX = if(isSelected) 6f else 0f)
                    .border(3.dp, Color.Black, RectangleShape)
                    .background(if(isSelected) NeoBlue else NeoWhite)
                    .clickable { onFilterChange(filter) }
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(filter, color = if(isSelected) NeoWhite else NeoBlack, fontWeight = FontWeight.Black)
            }
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
fun CardOwnerItem(
    bankName: String,
    cardType: String,
    ownerName: String,
    rating: Float,
    isActive: Boolean,
    commission: String,
    isOwnCard: Boolean = false,
    onRequestClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .neoShadow(offsetY = 12f, offsetX = 12f)
            .border(3.dp, Color.Black, RectangleShape)
            .background(NeoWhite)
            .padding(20.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(bankName.uppercase(), fontWeight = FontWeight.Black, color = NeoBlack, style = MaterialTheme.typography.headlineSmall)
                    Text(cardType.uppercase(), style = MaterialTheme.typography.titleSmall, color = NeoBlack, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .border(2.dp, Color.Black, RectangleShape)
                        .background(if (isActive && !isOwnCard) NeoGreen else if (isOwnCard) NeoBlue else NeoLightGray)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        if (isOwnCard) "YOUR CARD" else if (isActive) "ACTIVE" else "OFFLINE", 
                        color = if (isOwnCard) NeoWhite else NeoBlack, 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color.Black, thickness = 3.dp)
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(ownerName.uppercase(), fontWeight = FontWeight.Black, color = NeoBlack, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (rating == 0.0f) "NEW  •  COMM $commission" else "RATING $rating  •  COMM $commission",
                        style = MaterialTheme.typography.labelMedium, 
                        color = NeoBlack, 
                        fontWeight = FontWeight.Black
                    )
                }
                
                if (isOwnCard) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .neoShadow(offsetY = 4f, offsetX = 4f)
                                .border(3.dp, Color.Black, RectangleShape)
                                .background(NeoGreen)
                                .clickable(onClick = onEditClick)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NeoBlack, modifier = Modifier.size(20.dp))
                        }
                        Box(
                            modifier = Modifier
                                .neoShadow(offsetY = 4f, offsetX = 4f)
                                .border(3.dp, Color.Black, RectangleShape)
                                .background(NeoRed)
                                .clickable(onClick = onDeleteClick)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NeoWhite, modifier = Modifier.size(20.dp))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .neoShadow(offsetY = 6f, offsetX = 6f)
                            .border(3.dp, Color.Black, RectangleShape)
                            .background(NeoBlue)
                            .clickable(enabled = isActive, onClick = onRequestClick)
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text("REQUEST", color = NeoWhite, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
