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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathernow.MapActivity
import com.example.weathernow.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

// --- Colores basados en el diseño de paisaje (Ajustados) ---
val PrimaryButtonColor = Color(0xFF3897F0) // Azul de botón
val SecondaryButtonColor = Color(0xFF68AEE0) // Azul de Google más claro
val InputFieldColor = Color(0xFFFFFFFF) // Blanco puro para los campos
val CardBackgroundColor = Color(0x66FFFFFF) // Fondo de tarjeta blanco con 40% de transparencia
val TextColorDark = Color(0xFF333333) // Gris oscuro para texto

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            LoginScreen(auth, onGoToMap = { goToMap() })
        }
    }

    public override fun onStart() {
        super.onStart()
        //val currentUser = auth.currentUser
        //if (currentUser != null) goToMap()
    }

    fun goToMap() {
        val intent = Intent(this, MapActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

@Composable
fun LoginScreen(auth: FirebaseAuth, onGoToMap: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(Modifier.fillMaxSize()) {
        BackGroundImage()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 32.dp, vertical = 48.dp)
                .clip(RoundedCornerShape(20.dp)) // Borde redondeado de la tarjeta
                .background(CardBackgroundColor) // Fondo semi-transparente
                .padding(24.dp), // Padding interno de la tarjeta
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HeaderImage(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(24.dp))
            TitleText(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(24.dp))
            EmailField(email, onEmailChange = { email = it })
            Spacer(modifier = Modifier.height(16.dp))
            PasswordField(password, onPasswordChange = { password = it })
            Spacer(modifier = Modifier.height(8.dp))
            ForgotPassword(modifier = Modifier.align(Alignment.End))
            Spacer(modifier = Modifier.height(16.dp))
            // Botones
            LoginButton(onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Por favor, ingresa correo y contraseña.", Toast.LENGTH_SHORT).show()
                    return@LoginButton
                }
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{ task ->
                        if (task.isSuccessful) {
                            Log.d("TAG", "signInWithEmail:success")
                            onGoToMap()
                        } else {
                            Log.w("LOGIN_TAG", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                context,
                                "Autenticación fallida. Revisa tus credenciales.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            })

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "O",
                color = TextColorDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            GoogleSignInButton()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Udistrital-2025 (c)",
                color = TextColorDark.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
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
        contentDescription = "Fondo de paisaje de ciclista",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ForgotPassword(modifier: Modifier){
    Text(
        text = "¿Olvidaste la contraseña?",
        color = TextColorDark,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.clickable { /* Lógica de recuperación de contraseña */ }
    )
}

@Composable
fun LoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryButtonColor)
    ) {
        Text(text = "INGRESAR", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun GoogleSignInButton() {
    Button(
        onClick = { /* Lógica de inicio de sesión con Google */ },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SecondaryButtonColor)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = "Google Icon",
            modifier = Modifier.size(24.dp).padding(end = 8.dp)
        )
        Text(text = "Inicia sesión con Google", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
    }
}

@Composable
fun PasswordField(password: String, onPasswordChange: (String) -> Unit) {
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        placeholder = { Text(text = "Contraseña", color = Color.Gray) },
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
fun EmailField(email: String, onEmailChange: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = onEmailChange,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
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
