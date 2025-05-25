package com.example.ivan_jaramelgarejo_23052025_control9

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://mindicador.cl/"

    val instance: MindicadorApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MindicadorApiService::class.java)
    }
}