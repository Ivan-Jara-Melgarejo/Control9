package com.example.ivan_jaramelgarejo_23052025_control9

import retrofit2.Response
import retrofit2.http.GET

interface MindicadorApiService {
    @GET("api/euro")
    suspend fun getEuroValue(): Response<MindicadorResponse>
}

data class MindicadorResponse(
    val serie: List<Serie>
)

data class Serie(
    val fecha: String,
    val valor: Double
)