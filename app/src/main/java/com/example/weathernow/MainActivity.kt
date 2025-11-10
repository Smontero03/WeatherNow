package com.example.weathernow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathernow.ui.screens.home.HomeScreen
import com.example.weathernow.ui.screens.login.LoginScreen
import com.example.weathernow.ui.screens.profile.ProfileScreen
import com.example.weathernow.ui.theme.WeatherNowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherNowTheme {
                WeatherNowApp()
            }
        }
    }
}

@Composable
fun WeatherNowApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen()
        }
        composable("home") {
            HomeScreen()
        }
        composable("profile") {
            ProfileScreen()
        }
    }
}
