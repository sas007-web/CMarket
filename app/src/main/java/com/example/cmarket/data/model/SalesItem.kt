package com.example.cmarket.data.model

data class SalesItem(
    val id: Int,
    val description: String,
    val price: Double,
    val time: Int,
    val sellerEmail: String,
    val sellerPhone: String,
    val pictureUrl: String
)
