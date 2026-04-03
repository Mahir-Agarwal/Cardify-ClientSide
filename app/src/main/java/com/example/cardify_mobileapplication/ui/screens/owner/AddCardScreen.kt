package com.example.cardify_mobileapplication.ui.screens.owner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cardify_mobileapplication.ui.navigation.Routes
import com.example.cardify_mobileapplication.ui.components.*
import com.example.cardify_mobileapplication.ui.theme.*
import com.example.cardify_mobileapplication.utils.BinUtil
import com.example.cardify_mobileapplication.utils.UiState
import com.example.cardify_mobileapplication.ui.viewmodels.HomeViewModel
import com.example.cardify_mobileapplication.ui.viewmodels.AppViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var isCameraScanning by remember { mutableStateOf(false) }
    var bankName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("CREDIT") }
    var isAvailable by remember { mutableStateOf(true) }
    val cardTypes = listOf("CREDIT", "DEBIT")

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isCameraScanning = true
        }
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
                Text("ADD NEW CARD", fontWeight = FontWeight.Black, style = MaterialTheme.typography.displaySmall, color = NeoBlack)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoLightGray)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            NeoCard {
                Column {


                    NeoTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = "BANK NAME"
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Text("CARD TYPE", style = MaterialTheme.typography.labelLarge, color = NeoBlack, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        cardTypes.forEach { type ->
                            val isSelected = selectedType == type
                            val bgColor = if (isSelected) NeoBlue else NeoWhite
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                                    .neoShadow(offsetY = if(isSelected) 8f else 4f, offsetX = if(isSelected) 8f else 4f)
                                    .border(3.dp, Color.Black, RectangleShape)
                                    .background(bgColor)
                                    .clickable { selectedType = type }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    color = if(isSelected) NeoWhite else NeoBlack,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("AVAILABILITY", fontWeight = FontWeight.Black, color = NeoBlack)
                        Box(
                            modifier = Modifier
                                .border(3.dp, Color.Black, RectangleShape)
                                .background(if (isAvailable) NeoGreen else NeoWhite)
                                .clickable { isAvailable = !isAvailable }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(if (isAvailable) "YES" else "NO", color = NeoBlack, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    NeoButton(
                        text = "SCAN CARD ",
                        onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                isCameraScanning = true
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        backgroundColor = NeoBlue
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NeoButton(
                        text = "CONFIRM & SAVE",
                        onClick = {
                            coroutineScope.launch {
                                val job = viewModel.addCard(bankName, selectedType, isAvailable)
                                job.join()
                                if (viewModel.uiState.value is UiState.Success) {
                                    navController.popBackStack()
                                }
                            }
                        },
                        backgroundColor = NeoGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            Text(
                "MY CARDS", 
                style = MaterialTheme.typography.headlineSmall, 
                fontWeight = FontWeight.Black, 
                color = NeoBlack,
                modifier = Modifier.clickable { navController.navigate(Routes.MY_CARDS) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            val uiState by viewModel.uiState.collectAsState()
            
            LaunchedEffect(Unit) {
                viewModel.fetchMyCards()
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (val state = uiState) {
                    is UiState.Success -> {
                        val cards = state.data
                        items(cards.size) { index ->
                            val card = cards[index]
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .neoShadow(offsetY = 8f, offsetX = 8f)
                                    .border(3.dp, Color.Black, RectangleShape)
                                    .background(NeoWhite)
                                    .clickable { navController.navigate(Routes.MY_CARDS) }
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(card.bankName.uppercase(), fontWeight = FontWeight.Black, color = NeoBlack, style = MaterialTheme.typography.titleLarge)
                                        Text(card.type.uppercase(), style = MaterialTheme.typography.labelMedium, color = NeoBlack, fontWeight = FontWeight.Bold)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .border(3.dp, Color.Black, RectangleShape)
                                            .background(if (card.isAvailable) NeoGreen else NeoLightGray)
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(if (card.isAvailable) "ACTIVE" else "OFFLINE", color = NeoBlack, fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                    is UiState.Loading -> {
                        item { Text("LOADING YOUR CARDS...", fontWeight = FontWeight.Black) }
                    }
                    else -> {}
                }
            }
        }
    }

    if (isCameraScanning) {
        Dialog(
            onDismissRequest = { isCameraScanning = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            CardScannerOverlay(
                onCardDetected = { pan ->
                    isCameraScanning = false
                    
                    coroutineScope.launch {
                        val result = BinUtil.resolveCardOnline(pan)
                        bankName = result.bankName
                        selectedType = result.cardType
                    }

                    // Trigger vibration using basic Vibrator compatibility
                    try {
                        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                            vibratorManager.defaultVibrator
                        } else {
                            @Suppress("DEPRECATION")
                            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(200)
                        }
                    } catch (e: Exception) {
                        // ignore
                    }
                },
                onCancel = { 
                    isCameraScanning = false
                }
            )
        }
    }
}
