package com.example.cmarket.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Base URL til lærers API
// Api.kt
private const val BASE_URL = "https://anbo-salesitems.azurewebsites.net/"


// Retrofit-klient
private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL) // VIGTIG: slutter med /
    .addConverterFactory(GsonConverterFactory.create())
    .build()

// Global service, som resten af appen bruger
val salesService: SalesItemsService = retrofit.create(SalesItemsService::class.java)
