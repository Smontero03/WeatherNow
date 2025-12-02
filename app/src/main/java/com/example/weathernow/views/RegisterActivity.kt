package com.example.weathernow.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.weathernow.views.RegisterScreen
import com.example.weathernow.views.theme.WeatherNowTheme

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
