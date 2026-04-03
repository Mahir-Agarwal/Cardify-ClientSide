package com.example.cardify_mobileapplication.ui.components

import android.os.Build
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cardify_mobileapplication.ui.theme.*
import com.example.cardify_mobileapplication.utils.CardScannerAnalyzer
import kotlinx.coroutines.delay
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
@Composable
fun CardScannerOverlay(
    onCardDetected: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val isEmulator = remember {
        Build.FINGERPRINT.contains("generic") || 
        Build.MODEL.contains("Emulator") || 
        Build.MODEL.contains("Android SDK built for x86")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoLightGray)
    ) {
        if (isEmulator) {
            // Mock simulation
            LaunchedEffect(Unit) {
                delay(2500)
                onCardDetected("4340123456789012") // Simulated HDFC card
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "EMULATOR DETECTED.\nSIMULATING SCAN...",
                    color = NeoBlack,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {
            // Real CameraX Setup
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(
                                    Executors.newSingleThreadExecutor(),
                                    CardScannerAnalyzer { pan ->
                                        // Stop analyzing once detected to prevent multiple triggers
                                        it.clearAnalyzer()
                                        onCardDetected(pan)
                                    }
                                )
                            }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            // Handle failures
                        }

                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                }
            )
        }

        // Neo-Brutalism Cutout Frame overlay
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth().background(Color.Black.copy(alpha = 0.5f)))
            
            Row(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.Black.copy(alpha = 0.5f)))
                
                // The actual scanning window
                Box(
                    modifier = Modifier
                        .width(350.dp)
                        .fillMaxHeight()
                        .border(4.dp, NeoGreen, RectangleShape)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "SCANNING CARD...",
                        color = NeoGreen,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.background(NeoBlack).padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.Black.copy(alpha = 0.5f)))
            }
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth().background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 60.dp)
                ) {
                    Text("ALIGN CARD WITHIN THE FRAME", color = NeoWhite, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(40.dp))
                    NeoButton(text = "CANCEL", onClick = onCancel, backgroundColor = NeoRed)
                }
            }
        }
    }
}
