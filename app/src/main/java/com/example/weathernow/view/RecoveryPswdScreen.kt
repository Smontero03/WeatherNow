package com.example.weathernow.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weathernow.theme.CardBackgroundColor
import com.example.weathernow.theme.PrimaryButtonColor
import com.example.weathernow.theme.TextColorDark
import com.example.weathernow.view.shared.BackGroundImage
import com.example.weathernow.view.shared.EmailField
import com.example.weathernow.view.shared.ErrorField
import com.example.weathernow.view.shared.HeaderImage
import com.example.weathernow.viewmodel.RecoveryUiState
import com.example.weathernow.viewmodel.RecoveryViewModel

@Composable
fun RecoveryPswdScreen(
    navController: NavController,
    recoveryViewModel: RecoveryViewModel = viewModel()
) {
    val email by recoveryViewModel.email.collectAsState()
    val emailError by recoveryViewModel.emailError.collectAsState()
    val uiState by recoveryViewModel.uiState.collectAsState()
    val isButtonEnabled by recoveryViewModel.isButtonEnabled.collectAsState(initial = false)

    Box(Modifier.fillMaxSize()) {
        BackGroundImage()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 32.dp, vertical = 48.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(CardBackgroundColor)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (uiState) {
                is RecoveryUiState.Success -> {
                    SuccessMessage(navController, recoveryViewModel)
                }
                else -> {
                    RecoveryForm(email, emailError, isButtonEnabled, recoveryViewModel, uiState)
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.RecoveryForm(
    email: String,
    emailError: String?,
    isButtonEnabled: Boolean,
    recoveryViewModel: RecoveryViewModel,
    uiState: RecoveryUiState
) {
    HeaderImage(Modifier.align(Alignment.CenterHorizontally))
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = "Recuperar Contraseña",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = "Ingresa tu correo electrónico para recibir un enlace de restablecimiento.",
        fontSize = 14.sp,
        color = TextColorDark,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 24.dp)
    )
    EmailField(email, emailError) { recoveryViewModel.onEmailChange(it) }
    ErrorField(emailError) // Using the same ErrorField from RegisterScreen
    Spacer(modifier = Modifier.height(24.dp))

    if (uiState is RecoveryUiState.Loading) {
        CircularProgressIndicator(color = PrimaryButtonColor)
    } else {
        Button(
            onClick = { recoveryViewModel.sendPasswordResetEmail() },
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryButtonColor,
                disabledContainerColor = Color.Gray
            )
        ) {
            Text(text = "ENVIAR CORREO", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        }
    }

    if (uiState is RecoveryUiState.Error) {
        Text(
            text = uiState.message ?: "Error desconocido",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


@Composable
private fun ColumnScope.SuccessMessage(navController: NavController, recoveryViewModel: RecoveryViewModel) {
    Text(
        text = "¡Correo Enviado!",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    Text(
        text = "Hemos enviado un enlace a tu correo electrónico para que puedas restablecer tu contraseña.",
        fontSize = 16.sp,
        color = TextColorDark,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 24.dp)
    )
    Button(
        onClick = {
            navController.popBackStack()
            recoveryViewModel.resetState()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryButtonColor)
    ) {
        Text(text = "VOLVER", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
    }
}
