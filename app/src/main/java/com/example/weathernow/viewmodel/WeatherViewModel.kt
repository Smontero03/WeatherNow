package com.example.weathernow.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathernow.model.Weather
import com.example.weathernow.remote.WeatherApiService
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    var weather by mutableStateOf<Weather?>(null)
        private set

    private val weatherApiService = WeatherApiService.create()

    fun fetchWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                // Replace with your own OpenWeatherMap API key
                val apiKey = "6012d02f093a8262116cde5afae31029"
                val response = weatherApiService.getWeather(latitude, longitude, apiKey)
                weather = Weather(
                    temperature = response.main.temp,
                    description = response.weather.firstOrNull()?.description ?: "No description",
                    icon = response.weather.firstOrNull()?.icon ?: ""
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}