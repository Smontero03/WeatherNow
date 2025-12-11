package com.example.weathernow.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val DIRECTIONS_BASE_URL = "https://maps.googleapis.com/maps/api/directions/"

interface DirectionsApiService {
    @GET("json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): DirectionsResponse

    companion object {
        fun create(): DirectionsApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(DIRECTIONS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(DirectionsApiService::class.java)
        }
    }
}

data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    @SerializedName("overview_polyline")
    val overviewPolyline: Polyline
)

data class Polyline(
    val points: String
)
