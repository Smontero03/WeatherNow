package com.example.weathernow.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathernow.model.Weather
import com.example.weathernow.remote.DirectionsApiService
import com.example.weathernow.remote.DirectionsResponse
import com.example.weathernow.remote.WeatherApiService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    var weather by mutableStateOf<Weather?>(null)
        private set

    var directionsResponse by mutableStateOf<DirectionsResponse?>(null)
        private set

    private val weatherApiService = WeatherApiService.create()
    private val directionsApiService = DirectionsApiService.create()

    fun fetchWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
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

    fun fetchDirections(origin: String, destination: String, apiKey: String) {
        viewModelScope.launch {
            try {
                directionsResponse = directionsApiService.getDirections(origin, destination, apiKey)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun decodePolyline(encodedPolyline: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encodedPolyline.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encodedPolyline[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encodedPolyline[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }
}