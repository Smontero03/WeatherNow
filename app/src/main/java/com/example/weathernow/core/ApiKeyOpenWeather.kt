package com.example.weathernow.core
//import com.example.weathernow.views.ProfileScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//import com.example.weathernow.views.HomeScreen
//import com.example.weathernow.views.LoginScreen
import com.example.weathernow.ui.theme.WeatherNowTheme

class MainActivity : ComponentActivity() {

    // TODO: Consider moving the API key to a more secure place like local.properties
    private val API_KEY = "e010a8a2f70a56d704afa85a359c526e"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherNowTheme {
                WeatherNowApp(
                    createApiUrl = { cityName -> createWeatherApiUrl(cityName) }
                )
            }
        }
    }

    /**
     * Creates a URL string for the OpenWeatherMap API.
     * @param cityName The name of the city to query.
     * @return The formatted URL string.
     */
    private fun createWeatherApiUrl(cityName: String): String {
        // Returns the URL with the specified city, your API key, and sets units to metric and language to Spanish
        return "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$API_KEY&units=metric&lang=es"
    }
}

@Composable
fun WeatherNowApp(createApiUrl: (String) -> String) {
    val navController = rememberNavController()

    // State variables for weather data
    var cityName by remember { mutableStateOf("Buscando...") }
    var temperature by remember { mutableStateOf("--°") }
    var humidity by remember { mutableStateOf("--%") }
    var description by remember { mutableStateOf("cargando...") }
    var wind by remember { mutableStateOf("-- km/h") }
    var locationName by remember { mutableStateOf("Tu ubicación") }

    // TODO: Implement the refresh logic
    val onRefresh = {
        val cityToQuery = "Bogota" // This could come from user input
        val url = createApiUrl(cityToQuery)
        Log.d("WeatherNowApp", "Generated URL: $url")
        // Here you would make the network request to the weather API using the generated url
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            // TODO: Pass navController to LoginScreen for navigation
           // LoginScreen()
        }
        composable(route = "register"){
            //RegisterScreen()
        }
        composable("home") {
            // TODO: Pass the weather data and refresh callback to HomeScreen
            //HomeScreen()
        }
        composable("profile") {
            // TODO: Pass navController to ProfileScreen for navigation
            //ProfileScreen()
        }
    }
}