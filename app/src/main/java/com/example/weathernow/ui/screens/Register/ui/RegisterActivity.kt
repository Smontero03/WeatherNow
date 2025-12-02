package com.example.weathernow.ui.screens.Register.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.weathernow.ui.theme.WeatherNowTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherNowTheme {
                RegisterScreen()
            }
        }
    }
}
