package com.example.weathernow.view

import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weathernow.R
import com.example.weathernow.theme.CardBackgroundColor
import com.example.weathernow.theme.InputFieldColor
import com.example.weathernow.theme.PrimaryButtonColor
import com.example.weathernow.theme.ScriptFont
import com.example.weathernow.theme.SecondaryButtonColor
import com.example.weathernow.theme.TextColorDark
import com.example.weathernow.view.shared.BackGroundImage
import com.example.weathernow.view.shared.EmailField
import com.example.weathernow.view.shared.ErrorField
import com.example.weathernow.viewmodel.LoginUiState
import com.example.weathernow.viewmodel.LoginViewModel
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel()
) {
    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val emailError by loginViewModel.emailError.collectAsState()
    val loginUiState by loginViewModel.loginUiState.collectAsState()
    val isLoginButtonEnabled by loginViewModel.isLoginButtonEnabled.collectAsState(initial = false)
    val context = LocalContext.current
    val activity = LocalContext.current as? ComponentActivity // Se necesita la Activity para Credential Manager
    val coroutineScope = rememberCoroutineScope()
    val nonce = "simple_nonce"

    if (activity == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Error: no se pudo iniciar el inicio de sesión.")
        }
        return
    }

    fun singInWithGoogleFlow() {
        val credentialManager = CredentialManager.create(context)
        val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder(
            context.getString(R.string.web_client_id)
        ).setNonce(nonce)
            .build()
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        coroutineScope.launch { // Usar el coroutineScope para la llamada suspendida
            try {
                // Notifica al ViewModel que el flujo ha comenzado si no está ya en Loading
                if (loginViewModel.loginUiState.value != LoginUiState.Loading) {
                    loginViewModel.resetLoginState()
                }

                val result = credentialManager.getCredential(
                    request = request,
                    context = activity,
                )

                // Manejar el resultado de la credencial
                val credential = result.credential
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        // Llama al ViewModel para autenticar con Firebase
                        loginViewModel.firebaseAuthWithGoogleIdToken(googleIdTokenCredential)

                    } catch (e: GoogleIdTokenParsingException) {
                        loginViewModel.handleGoogleSignInFailure(e)
                    }
                } else {
                    // Otra credencial no manejada
                    loginViewModel.handleGoogleSignInFailure(Exception("Tipo de credencial no esperado: ${credential.type}"))
                }

            } catch (e: GetCredentialException) {
                // Manejar fallo de Credential Manager (ej: usuario cancela)
                loginViewModel.handleGoogleSignInFailure(e)
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        BackGroundImage()
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
            TitleText(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(24.dp))
            EmailField(
                email = email,
                error = emailError,
                onEmailChange = { loginViewModel.onEmailChange(it) }
            )
            ErrorField(emailError, Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(16.dp))
            PasswordField(password, onPasswordChange = { loginViewModel.onPasswordChange(it) })
            Spacer(modifier = Modifier.height(8.dp))
            ForgotPassword(modifier = Modifier.align(Alignment.End)) { navController.navigate("recovery") }
            Spacer(modifier = Modifier.height(16.dp))

            if (loginUiState == LoginUiState.Loading) {
                CircularProgressIndicator()
            } else {
                LoginButton(enabled = isLoginButtonEnabled, onClick = { loginViewModel.login() })
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "O",
                color = TextColorDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))
            GoogleSignInButton(onClick = { singInWithGoogleFlow() })
            Spacer(modifier = Modifier.height(16.dp))
            LoginUpLink(Modifier.align(Alignment.CenterHorizontally)) { navController.navigate("register") }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Udistrital-2025 (c)",
                color = TextColorDark.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }

    when (loginUiState) {
        is LoginUiState.Success -> {
            navController.navigate("map") {
                popUpTo("login") { inclusive = true }
            }
            loginViewModel.resetLoginState()
        }

        is LoginUiState.Error -> {
            val message = (loginUiState as LoginUiState.Error).message
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            loginViewModel.resetLoginState()
        }

        else -> {}
    }
}

@Composable
fun LoginUpLink(modifier: Modifier, onGoToRegister: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.clickable { onGoToRegister() }
    ) {
        Text(
            text = "¿No tienes cuenta? ",
            color = TextColorDark,
            fontSize = 14.sp
        )
        Text(
            text = "Regístrate.",
            color = PrimaryButtonColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TitleText(modifier: Modifier) {
    Text(
        text = "Weather Now",
        fontSize = 45.sp,
        fontFamily = ScriptFont,
        color = Color.White,
        modifier = modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun ForgotPassword(modifier: Modifier, onForgotPassword: () -> Unit = {}) {
    Text(
        text = "¿Olvidaste la contraseña?",
        color = TextColorDark,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.clickable { onForgotPassword() }
    )
}

@Composable
fun LoginButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryButtonColor,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.White
        )
    ) {
        Text(text = "INGRESAR", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SecondaryButtonColor)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = "Google Icon",
            modifier = Modifier
                .size(24.dp)
        )
        Text(
            text = "Inicia sesión con Google",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun PasswordField(password: String, onPasswordChange: (String) -> Unit) {
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        placeholder = { Text(text = "Contraseña", color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
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

