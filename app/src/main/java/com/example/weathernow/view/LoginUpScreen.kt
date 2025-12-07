package com.example.weathernow.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathernow.R
import com.example.weathernow.theme.CardBackgroundColor
import com.example.weathernow.theme.InputFieldColor
import com.example.weathernow.theme.PrimaryButtonColor
import com.example.weathernow.theme.TextColorDark
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

// Asume que esta fuente fue definida en otro lugar, por ejemplo, en tu archivo Theme.kt
val ScriptFont = FontFamily.Cursive


class RegisterActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            RegisterScreen(this, auth)
        }
    }

    fun goToMainScreen() {
        val intent = Intent(this, MapActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun goToLoginScreen() {
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun RegisterScreen(activity: RegisterActivity, auth: FirebaseAuth) {
    // Definición de variables de estado
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(Modifier.fillMaxSize()) {
        // 1. FONDO DE IMAGEN
        Image(
            painter = painterResource(id = R.drawable.login_background), // Mismo fondo de paisaje
            contentDescription = "Fondo de paisaje de ciclista",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. CONTENIDO DEL REGISTRO (Centrado en una tarjeta)
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
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "WeatherNow",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = ScriptFont,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "CREA TU CUENTA",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextColorDark,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Campos de texto para Registro
            NameField(fullName) { fullName = it }
            Spacer(modifier = Modifier.height(12.dp))
            EmailField(email) { email = it }
            Spacer(modifier = Modifier.height(12.dp))
            PasswordField(password) { password = it }
            Spacer(modifier = Modifier.height(12.dp))
            ConfirmPasswordField(confirmPassword) { confirmPassword = it }
            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Registro
            RegisterButton(onClick = {
                if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(context, "Completa todos los campos.", Toast.LENGTH_SHORT).show()
                    return@RegisterButton
                }
                if (password != confirmPassword) {
                    Toast.makeText(context, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                    return@RegisterButton
                }

                // Lógica de registro de Firebase
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            Log.d("REGISTER_TAG", "createUserWithEmail:success")
                            Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                            activity.goToMainScreen()
                        } else {
                            Log.w("REGISTER_TAG", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(context, "Registro fallido: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
            })
            Spacer(modifier = Modifier.height(16.dp))

            // Texto para ir al Login
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.clickable { activity.goToLoginScreen() }
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
}

// --- Componentes Reutilizados / Nuevos ---

@Composable
fun NameField(name: String, onNameChange: (String) -> Unit) {
    TextField(
        value = name,
        onValueChange = onNameChange,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
        placeholder = { Text(text = "Nombre completo", color = Color.Gray) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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
fun ConfirmPasswordField(password: String, onPasswordChange: (String) -> Unit) {
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        placeholder = { Text(text = "Confirmar contraseña", color = Color.Gray) },
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
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
fun RegisterButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryButtonColor)
    ) {
        Text(text = "REGISTRARSE", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}


