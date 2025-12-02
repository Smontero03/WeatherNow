package com.example.weathernow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weathernow.views.theme.WeatherNowTheme

class MapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherNowTheme {
                // Puedes quitar el Scaffold si no lo necesitas
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 👇 Aquí mostramos tu nueva pantalla
                    MainMapScreen()
                    //RegisterScreen ()
                }
            }
        }
    }
}

@Composable
fun MainMapScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF001F2E)) // fondo oscuro tipo mapa
    ) {

        // 🔹 (1) Placeholder del mapa
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1B2A))
        ) {
            // Aquí luego irá el GoogleMap()
            Text(
                text = "Mapa aquí",
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 🔹 (2) Botón de menú (arriba izquierda)
        IconButton(
            onClick = { /* TODO: abrir menú lateral */ },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(Color(0x88000000), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menú",
                tint = Color.White
            )
        }

        // 🔹 (3) Panel inferior con clima y destino
        Surface(
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            color = Color(0xFFA5F9E1),
            tonalElevation = 6.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text("¿A dónde nos dirigimos?", fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    "Clima alrededor:",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ClimateChip("Soleado", Color(0xFFFFF59D))
                    ClimateChip("Nublado", Color(0xFFE0E0E0))
                    ClimateChip("Lluvia leve", Color(0xFF64B5F6))
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    "Pronóstico para las próximas horas",
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.White, RoundedCornerShape(10.dp))
                )

                Spacer(Modifier.height(16.dp))
                Text(
                    "Tip para la conducción en climas muy lluviosos",
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.White, RoundedCornerShape(10.dp))
                )

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ClimateChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainMapScreenPreview() {
    WeatherNowTheme {
        MainMapScreen()
    }
}