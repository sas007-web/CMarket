package com.example.cmarket.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmarket.data.model.SalesItem
import com.example.cmarket.domain.SalesItemsRepository
import com.example.cmarket.domain.SalesItemsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class SortKey { PRICE_ASC, PRICE_DESC, DATE_NEW, DATE_OLD }

data class ListUiState(
    val all: List<SalesItem> = emptyList(),
    val query: String = "",
    val minPriceText: String = "",
    val sort: SortKey = SortKey.PRICE_DESC,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val minPrice: Double? get() = minPriceText.toDoubleOrNull()

    val filteredSorted: List<SalesItem>
        get() {
            val q = query
            val f1 = all.filter { (it.description).contains(q, ignoreCase = true) }
            val f2 = f1.filter { minPrice == null || it.price >= minPrice!! }

            return when (sort) {
                SortKey.PRICE_ASC  -> f2.sortedBy { it.price }
                SortKey.PRICE_DESC -> f2.sortedByDescending { it.price }
                SortKey.DATE_NEW   -> f2.sortedByDescending { it.time.toLong() }
                SortKey.DATE_OLD   -> f2.sortedBy { it.time.toLong() }
            }
        }
}

class SalesItemsViewModel(
    private val repo: SalesItemsRepository = SalesItemsRepositoryImpl()
) : ViewModel() {

    private val _ui = MutableStateFlow(ListUiState(isLoading = true))
    val ui: StateFlow<ListUiState> = _ui

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        _ui.value = _ui.value.copy(isLoading = true, error = null)
        runCatching { repo.getAll() }
            .onSuccess { _ui.value = _ui.value.copy(all = it, isLoading = false) }
            .onFailure { _ui.value = _ui.value.copy(isLoading = false, error = it.message) }
    }

    fun setQuery(q: String) {
        _ui.value = _ui.value.copy(query = q)
    }

    fun setMinPrice(t: String) {
        _ui.value = _ui.value.copy(minPriceText = t)
    }

    fun setSort(s: SortKey) {
        _ui.value = _ui.value.copy(sort = s)
    }

    fun getItemById(id: Int) =
        _ui.value.all.firstOrNull { it.id == id }

    fun createItem(
        description: String,
        price: Double,
        sellerEmail: String,
        sellerPhone: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            runCatching {
                val newItem = SalesItem(
                    id = 0, // bliver sat af serveren
                    description = description,
                    price = price,
                    time = (System.currentTimeMillis() / 1000).toInt(),
                    sellerEmail = sellerEmail,
                    sellerPhone = sellerPhone,
                    pictureUrl = ""
                )
                repo.create(newItem)
            }.onSuccess {
                refresh()                 // hent ny liste inkl. den nye vare
                onResult(true, null)
            }.onFailure { e ->
                onResult(false, e.message)
            }
        }
    }

}
