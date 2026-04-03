package com.example.cardify_mobileapplication.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cardify_mobileapplication.ui.components.*
import com.example.cardify_mobileapplication.ui.navigation.Routes
import com.example.cardify_mobileapplication.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.cardify_mobileapplication.utils.UiState
import com.example.cardify_mobileapplication.ui.viewmodels.AppViewModelFactory
import com.example.cardify_mobileapplication.ui.viewmodels.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("BUYER") }
    val roles = listOf("BUYER", "OWNER")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoLightGray)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CREATE ACCOUNT",
            style = MaterialTheme.typography.displayMedium,
            color = NeoBlack,
            fontWeight = FontWeight.Black
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        NeoCard {
            Column {
                NeoTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Full Name"
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                NeoTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(16.dp))

                NeoTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "SELECT YOUR ROLE",
                    style = MaterialTheme.typography.labelLarge,
                    color = NeoBlack,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    roles.forEach { role ->
                        val isSelected = selectedRole == role
                        val bgColor = if (isSelected) NeoGreen else NeoWhite
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .neoShadow(offsetY = if(isSelected) 8f else 4f, offsetX = if(isSelected) 8f else 4f)
                                .border(3.dp, Color.Black, RectangleShape)
                                .background(bgColor)
                                .clickable { selectedRole = role }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = role,
                                color = NeoBlack,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                
                val uiState by viewModel.uiState.collectAsState()
                
                LaunchedEffect(uiState) {
                    if (uiState is UiState.Success) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(0)
                        }
                    }
                }

                NeoButton(
                    text = if (uiState is UiState.Loading) "LOADING..." else "SIGN UP",
                    onClick = { viewModel.register(name, email, password, selectedRole) },
                    enabled = uiState !is UiState.Loading && name.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
                    backgroundColor = NeoBlue
                )
                if (uiState is UiState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (uiState as UiState.Error).message,
                        color = NeoRed,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { navController.navigate(Routes.LOGIN) }) {
            Text(
                "ALREADY HAVE AN ACCOUNT? LOGIN",
                color = NeoBlack,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
