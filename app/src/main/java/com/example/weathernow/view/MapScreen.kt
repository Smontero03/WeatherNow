package com.example.weathernow.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.weathernow.viewmodel.WeatherViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen() {
    MaterialTheme(
        colorScheme = lightColorScheme()
    ) {
        MapScreenContent()
    }
}

@Composable
fun MapScreenContent(weatherViewModel: WeatherViewModel = viewModel()) {
    val context = LocalContext.current
    val fusedLocationClient = remember { getFusedLocationProviderClient(context) }
    val bogota = LatLng(4.60971, -74.08175)

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasLocationPermission = granted
        }
    )

    var currentLocation by remember { mutableStateOf(bogota) }

    LaunchedEffect(Unit) {
        weatherViewModel.fetchWeather(bogota.latitude, bogota.longitude)
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L).build()

            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        val newLocation = LatLng(location.latitude, location.longitude)
                        currentLocation = newLocation
                        weatherViewModel.fetchWeather(newLocation.latitude, newLocation.longitude)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 12f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission
            )
        ) {
            Marker(
                state = MarkerState(position = currentLocation),
                title = if (hasLocationPermission) "Mi ubicación" else "Bogotá",
                snippet = if (hasLocationPermission) "Aquí estoy" else "Clima de Bogotá"
            )
        }
        weatherViewModel.weather?.let { weather ->
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "${weather.temperature}°C")
                    Text(text = weather.description)
                    AsyncImage(
                        model = "https://openweathermap.org/img/w/${weather.icon}.png",
                        contentDescription = weather.description,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}
