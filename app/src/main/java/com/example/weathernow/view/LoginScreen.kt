package com.example.weathernow.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathernow.R
import com.example.weathernow.theme.*
import com.example.weathernow.viewmodel.LoginUiState
import com.example.weathernow.viewmodel.LoginViewModel

class LoginScreen : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(
                loginViewModel,
                onGoToMap = { goToMap() },
                onGoToRegister = { goToRegister() }
            )
        }
    }

    fun goToMap() {
        val intent = Intent(this, MapActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun goToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onGoToMap: () -> Unit,
    onGoToRegister: () -> Unit
) {
    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val loginUiState by loginViewModel.loginUiState.collectAsState()
    val isLoginButtonEnabled by loginViewModel.isLoginButtonEnabled.collectAsState(initial = false)
    val context = LocalContext.current

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
            HeaderImage(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(24.dp))
            TitleText(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(24.dp))
            EmailField(email, onEmailChange = { loginViewModel.onEmailChange(it) })
            Spacer(modifier = Modifier.height(16.dp))
            PasswordField(password, onPasswordChange = { loginViewModel.onPasswordChange(it) })
            Spacer(modifier = Modifier.height(8.dp))
            ForgotPassword(modifier = Modifier.align(Alignment.End))
            Spacer(modifier = Modifier.height(16.dp))

            LoginButton(enabled = isLoginButtonEnabled, onClick = { loginViewModel.login() })

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "O",
                color = TextColorDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))
            GoogleSignInButton()
            Spacer(modifier = Modifier.height(16.dp))
            LoginUpLink(Modifier.align(Alignment.CenterHorizontally), onGoToRegister)
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
        is LoginUiState.Success -> onGoToMap()
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
    val scriptFont = FontFamily(
        Font(R.font.great_vibes, FontWeight.Normal)
    )
    Text(
        text = "Weather Now",
        fontSize = 45.sp,
        fontFamily = scriptFont,
        color = Color.White,
        modifier = modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun BackGroundImage() {
    Image(
        painter = painterResource(id = R.drawable.login_background),
        contentDescription = "Fondo de ciclista",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ForgotPassword(modifier: Modifier) {
    Text(
        text = "¿Olvidaste la contraseña?",
        color = TextColorDark,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.clickable { /* Lógica de recuperación de contraseña */ }
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
            disabledContainerColor = SecondaryButtonColor,
            disabledContentColor = Color.White
        )
    ) {
        Text(text = "INGRESAR", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun GoogleSignInButton() {
    Button(
        onClick = { /* Lógica de inicio de sesión con Google */ },
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
                .padding(end = 8.dp)
        )
        Text(
            text = "Inicia sesión con Google",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
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

@Composable
fun EmailField(email: String, onEmailChange: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = onEmailChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        placeholder = { Text(text = "Correo", color = Color.Gray) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
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
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.app_logo),
        contentDescription = "Logo de WeatherNow",
        modifier = modifier.size(100.dp)
    )
}
