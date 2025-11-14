package com.example.cmarket.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cmarket.ui.list.SalesItemsViewModel
import com.example.cmarket.ui.list.SortKey

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    onOpenDetail: (Int) -> Unit = {},
    vm: SalesItemsViewModel = viewModel()
) {
    val ui = vm.ui.collectAsState().value

    Column(modifier = modifier.padding(12.dp)) {

        // Antal varer (nu inde i Column, får også padding)
        Text(text = "Antal varer: ${ui.filteredSorted.size}")

        // --- Filtre (2 stk) ---
        OutlinedTextField(
            value = ui.query,
            onValueChange = vm::setQuery,
            label = { Text("Søg titel") }
        )
        OutlinedTextField(
            value = ui.minPriceText,
            onValueChange = vm::setMinPrice,
            label = { Text("Min pris") }
        )

        // --- Sortering (mindst 2; her 4) ---
        TextButton(onClick = { vm.setSort(SortKey.PRICE_ASC) })  { Text("Pris ↑") }
        TextButton(onClick = { vm.setSort(SortKey.PRICE_DESC) }) { Text("Pris ↓") }
        TextButton(onClick = { vm.setSort(SortKey.DATE_NEW) })   { Text("Nyeste") }
        TextButton(onClick = { vm.setSort(SortKey.DATE_OLD) })   { Text("Ældste") }

        // --- Liste ---
        LazyColumn {
            items(ui.filteredSorted) { item ->
                Text(
                    text = "${item.description ?: "(ingen tekst)"} - ${item.price} kr",
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { onOpenDetail(item.id) }
                )
            }
        }

        // --- Fejltekst (hvis noget går galt) ---
        ui.error?.let { Text("Fejl: $it") }
    }
}
