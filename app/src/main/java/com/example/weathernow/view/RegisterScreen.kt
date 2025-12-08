package com.example.weathernow.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weathernow.R
import com.example.weathernow.theme.CardBackgroundColor
import com.example.weathernow.theme.InputFieldColor
import com.example.weathernow.theme.PrimaryButtonColor
import com.example.weathernow.theme.TextColorDark
import com.example.weathernow.view.shared.EmailField
import com.example.weathernow.view.shared.ErrorField
import com.example.weathernow.viewmodel.RegisterUiState
import com.example.weathernow.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    registerViewModel: RegisterViewModel = viewModel()
) {
    val fullName by registerViewModel.fullName.collectAsState()
    val email by registerViewModel.email.collectAsState()
    val password by registerViewModel.password.collectAsState()
    val confirmPassword by registerViewModel.confirmPassword.collectAsState()
    val fullNameError by registerViewModel.fullNameError.collectAsState()
    val emailError by registerViewModel.emailError.collectAsState()
    val passwordError by registerViewModel.passwordError.collectAsState()
    val confirmPasswordError by registerViewModel.confirmPasswordError.collectAsState()
    val registerUiState by registerViewModel.registerUiState.collectAsState()
    val isRegisterButtonEnabled by registerViewModel.isRegisterButtonEnabled.collectAsState(initial = false)
    val context = LocalContext.current

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Fondo de paisaje de ciclista",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 32.dp, vertical = 48.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(CardBackgroundColor)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "CREA TU CUENTA",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextColorDark,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            NameField(fullName, fullNameError) { registerViewModel.onFullNameChange(it) }
            ErrorField(fullNameError, Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(12.dp))
            EmailField(
                email = email,
                error = emailError,
                onEmailChange = { registerViewModel.onEmailChange(it) }
            )
            ErrorField(emailError, Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(12.dp))
            PasswordField(password, passwordError) { registerViewModel.onPasswordChange(it) }
            ErrorField(passwordError, Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(12.dp))
            ConfirmPasswordField(
                confirmPassword,
                confirmPasswordError
            ) { registerViewModel.onConfirmPasswordChange(it) }
            ErrorField(confirmPasswordError, Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(32.dp))

            if (registerUiState is RegisterUiState.Loading) {
                CircularProgressIndicator()
            } else {
                RegisterButton(
                    enabled = isRegisterButtonEnabled,
                    onClick = { registerViewModel.registerUser() })
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.clickable { navController.popBackStack() }
            ) {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    color = TextColorDark,
                    fontSize = 14.sp
                )
                Text(
                    text = "Ingresa aquí.",
                    color = PrimaryButtonColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    when (registerUiState) {
        is RegisterUiState.Success -> {
            Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
            navController.navigate("map") {
                popUpTo("login") { inclusive = true }
            }
            registerViewModel.resetRegisterState()
        }

        is RegisterUiState.Error -> {
            val message = (registerUiState as RegisterUiState.Error).message
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            registerViewModel.resetRegisterState()
        }

        else -> {}
    }
}

@Composable
fun NameField(name: String, error: String?, onNameChange: (String) -> Unit) {
    TextField(
        value = name,
        onValueChange = onNameChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        placeholder = { Text(text = "Nombre completo", color = Color.Gray) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
        isError = error != null,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = InputFieldColor,
            unfocusedContainerColor = InputFieldColor,
            disabledContainerColor = InputFieldColor,
            cursorColor = TextColorDark,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun PasswordField(password: String, error: String?, onPasswordChange: (String) -> Unit) {
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        placeholder = { Text(text = "Contraseña", color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        isError = error != null,
        visualTransformation = PasswordVisualTransformation(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = InputFieldColor,
            unfocusedContainerColor = InputFieldColor,
            disabledContainerColor = InputFieldColor,
            cursorColor = TextColorDark,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun ConfirmPasswordField(password: String, error: String?, onPasswordChange: (String) -> Unit) {
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        placeholder = { Text(text = "Confirmar contraseña", color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        isError = error != null,
        visualTransformation = PasswordVisualTransformation(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = InputFieldColor,
            unfocusedContainerColor = InputFieldColor,
            disabledContainerColor = InputFieldColor,
            cursorColor = TextColorDark,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun RegisterButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryButtonColor,
            disabledContainerColor = Color.Gray
        )
    ) {
        Text(text = "REGISTRARSE", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}
