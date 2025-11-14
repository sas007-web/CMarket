package com.example.cmarket.domain
import android.util.Log
import com.example.cmarket.data.model.SalesItem
import com.example.cmarket.data.network.salesService

class SalesItemsRepositoryImpl : SalesItemsRepository {
    override suspend fun getAll(): List<SalesItem> =
        salesService.getAll()

    override suspend fun getById(id: Int): SalesItem = salesService.getById(id)
    override suspend fun create(item: SalesItem): SalesItem = salesService.create(item)
    override suspend fun delete(id: Int): Boolean = salesService.delete(id).isSuccessful
}

