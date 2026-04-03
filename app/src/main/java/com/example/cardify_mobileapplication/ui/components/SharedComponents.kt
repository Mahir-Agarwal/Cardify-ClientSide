package com.example.cardify_mobileapplication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.cardify_mobileapplication.ui.theme.*
import com.example.cardify_mobileapplication.ui.viewmodels.CardInfo
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

fun Modifier.neoShadow(offsetY: Float = 12f, offsetX: Float = 12f): Modifier = this.then(
    Modifier.drawBehind {
        drawRect(
            color = Color.Black,
            topLeft = Offset(offsetX, offsetY),
            size = size
        )
    }
)

@Composable
fun NeoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    backgroundColor: Color = NeoBlue,
    textColor: Color = NeoWhite
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .neoShadow(offsetY = 12f, offsetX = 12f)
            .border(3.dp, Color.Black, RectangleShape)
            .background(if (enabled) backgroundColor else Color.LightGray)
            .clickable(enabled = enabled && !isLoading, onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = textColor, modifier = Modifier.size(24.dp))
        } else {
            Text(
                text = text.uppercase(),
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
    Box(modifier = modifier.fillMaxWidth().padding(top = 12.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .neoShadow(offsetY = 8f, offsetX = 8f),
            shape = RectangleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedContainerColor = NeoWhite,
                unfocusedContainerColor = NeoWhite,
                errorBorderColor = NeoRed,
                cursorColor = NeoBlack
            ),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon,
            singleLine = true,
            isError = isError,
            supportingText = supportingText
        )
        
        // Badge-Style Label
        Box(
            modifier = Modifier
                .offset(x = 12.dp, y = (-12).dp)
                .border(2.dp, Color.Black, RectangleShape)
                .background(NeoGreen)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = NeoBlack
            )
        }
    }
}

@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeoWhite,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .neoShadow(offsetY = 16f, offsetX = 16f)
            .border(3.dp, Color.Black, RectangleShape)
            .background(backgroundColor)
            .padding(24.dp),
        content = content
    )
}

@Composable
fun NeoStepper(
    currentStep: Int,
    steps: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, step ->
            val isCompleted = index < currentStep
            val isCurrent = index == currentStep
            val color = if (isCompleted || isCurrent) NeoGreen else NeoWhite
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .neoShadow(offsetY = 4f, offsetX = 4f)
                        .border(2.dp, Color.Black, RectangleShape)
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = (index + 1).toString(),
                            color = Color.Black,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = step.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            if (index < steps.size - 1) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 16.dp)
                        .padding(horizontal = 4.dp),
                    color = Color.Black,
                    thickness = 3.dp
                )
            }
        }
    }
}

@Composable
fun EditCardDialog(
    card: CardInfo,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Boolean) -> Unit
) {
    var bankName by remember { mutableStateOf(card.bankName) }
    var cardType by remember { mutableStateOf(card.type) }
    var isAvailable by remember { mutableStateOf(card.isAvailable) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Box(
                modifier = Modifier
                    .neoShadow(offsetY = 4f, offsetX = 4f)
                    .border(2.dp, Color.Black, RectangleShape)
                    .background(NeoGreen)
                    .clickable { onConfirm(card.id, bankName, cardType, isAvailable) }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("SAVE", fontWeight = FontWeight.Black, color = NeoBlack)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", fontWeight = FontWeight.Black, color = NeoBlack)
            }
        },
        title = {
            Text("EDIT CARD", fontWeight = FontWeight.Black, style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NeoTextField(value = bankName, onValueChange = { bankName = it }, label = "Bank Name")
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { isAvailable = !isAvailable }) {
                    Checkbox(
                        checked = isAvailable, 
                        onCheckedChange = { isAvailable = it },
                        colors = CheckboxDefaults.colors(checkedColor = NeoGreen, checkmarkColor = NeoWhite)
                    )
                    Text("MARK AS AVAILABLE", fontWeight = FontWeight.Bold, color = NeoBlack)
                }
            }
        },
        containerColor = NeoWhite,
        shape = RectangleShape,
        modifier = Modifier.border(3.dp, Color.Black, RectangleShape)
    )
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Box(
                modifier = Modifier
                    .neoShadow(offsetY = 4f, offsetX = 4f)
                    .border(2.dp, Color.Black, RectangleShape)
                    .background(NeoRed)
                    .clickable { onConfirm() }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("YES, DELETE", fontWeight = FontWeight.Black, color = NeoWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", fontWeight = FontWeight.Black, color = NeoBlack)
            }
        },
        title = {
            Text(title, fontWeight = FontWeight.Black, style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Text(message, fontWeight = FontWeight.Bold, color = NeoBlack)
        },
        containerColor = NeoWhite,
        shape = RectangleShape,
        modifier = Modifier.border(3.dp, Color.Black, RectangleShape)
    )
}
