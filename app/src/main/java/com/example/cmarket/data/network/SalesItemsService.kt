package com.example.cmarket.data.network

import com.example.cmarket.data.model.SalesItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST

interface SalesItemsService {

    // SalesItemsService.kt
    @GET("api/SalesItems")
    suspend fun getAll(): List<SalesItem>

    @GET("api/SalesItems/{id}")
    suspend fun getById(@Path("id") id: Int): SalesItem

    @POST("api/SalesItems")
    suspend fun create(@Body item: SalesItem): SalesItem


    @DELETE("api/SalesItems/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Unit>
}
