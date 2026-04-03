package com.example.cardify_mobileapplication.utils

import android.media.Image
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class CardScannerAnalyzer(
    private val onCardDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    // Regex for typical 16-digit card numbers: XXXX XXXX XXXX XXXX
    private val cardRegex = Regex("\\b\\d{4}[ -]?\\d{4}[ -]?\\d{4}[ -]?\\d{4}\\b")

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage: Image? = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val textBlocks = visionText.textBlocks
                    for (block in textBlocks) {
                        val match = cardRegex.find(block.text)
                        if (match != null) {
                            val cleanPan = match.value.replace(Regex("[ -]"), "")
                            onCardDetected(cleanPan)
                            // We don't close the imageProxy here if we want to stop, 
                            // we just let the callback handle navigation.
                            // To prevent multiple scans, the UI should just dismount the camera.
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle OCR failure if necessary
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
