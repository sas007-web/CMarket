package com.example.cmarket.domain

import com.example.cmarket.data.model.SalesItem

interface SalesItemsRepository {
    suspend fun getAll(): List<SalesItem>
    suspend fun getById(id: Int): SalesItem
    suspend fun create(item: SalesItem): SalesItem
    suspend fun delete(id: Int): Boolean
}