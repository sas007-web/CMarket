package com.example.cmarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cmarket.screens.ListScreen
import com.example.cmarket.ui.auth.AuthViewModel
import com.example.cmarket.ui.list.SalesItemsViewModel
import com.example.cmarket.ui.theme.CMarketTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CMarketTheme {
                val nav = rememberNavController()

                // Fælles ViewModels
                val salesItemsVm: SalesItemsViewModel = viewModel()
                val authVm: AuthViewModel = viewModel()

                Scaffold(
                    topBar = {
                        AppTopBar(
                            isLoggedIn = authVm.uiState.isLoggedIn,
                            userEmail = authVm.uiState.userEmail,
                            onLoginClick = { nav.navigate(NavRoutes.Login.route) },
                            onLogoutClick = { authVm.logout() },
                            onCreateClick = {
                                if (authVm.uiState.isLoggedIn) {
                                    nav.navigate(NavRoutes.CreateItem.route)
                                } else {
                                    nav.navigate(NavRoutes.Login.route)
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = nav,
                        startDestination = NavRoutes.List.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // LISTE
                        composable(NavRoutes.List.route) {
                            ListScreen(
                                vm = salesItemsVm,
                                onOpenDetail = { id ->
                                    nav.navigate(NavRoutes.Detail.createRoute(id))
                                }
                            )
                        }

                        // DETAIL
                        composable(
                            route = NavRoutes.Detail.route,
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) { backStack ->
                            val id = backStack.arguments?.getInt("id") ?: 0
                            DetailScreen(
                                id = id,
                                vm = salesItemsVm,
                                onBack = { nav.popBackStack() }
                            )
                        }

                        // LOGIN
                        composable(NavRoutes.Login.route) {
                            LoginScreen(
                                vm = authVm,
                                onLoggedIn   = { nav.popBackStack() },
                                onGoRegister = { nav.navigate(NavRoutes.Register.route) }
                            )
                        }

                        // REGISTER
                        composable(NavRoutes.Register.route) {
                            RegisterScreen(
                                vm = authVm,
                                onRegistered = { nav.popBackStack() }
                            )
                        }

                        // OPRET ITEM
                        composable(NavRoutes.CreateItem.route) {
                            CreateItemScreen(
                                itemsVm = salesItemsVm,
                                authVm = authVm,
                                onDone = { nav.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ---------- Top bar ---------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    isLoggedIn: Boolean,
    userEmail: String? = null,
    onLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onCreateClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            if (isLoggedIn && userEmail != null) {
                Text("cmarket – $userEmail")
            } else {
                Text("cmarket")
            }
        },
        actions = {
            Text("🔔"); Spacer(Modifier.width(12.dp))
            //  PLUS-knap, nu klikbar
            TextButton(onClick = onCreateClick) { Text("＋") }
            Spacer(Modifier.width(12.dp))
            Text("💬"); Spacer(Modifier.width(12.dp))
            if (isLoggedIn) {
                TextButton(onClick = onLogoutClick) { Text("Log ud") }
            } else {
                TextButton(onClick = onLoginClick) { Text("Log ind") }
            }
        }
    )
}

/* ---------- DetailScreen: bruger ViewModel ---------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    id: Int,
    vm: SalesItemsViewModel,
    onBack: () -> Unit = {}
) {
    val item = vm.getItemById(id)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detaljer") }) }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
        ) {
            if (item == null) {
                Text("Kunne ikke finde vare med id=$id")
            } else {
                Text(text = item.description)
                Spacer(Modifier.height(8.dp))
                Text(text = "Pris: ${item.price} kr")
                Spacer(Modifier.height(8.dp))
                Text(text = "Id: ${item.id}")
            }

            Spacer(Modifier.height(16.dp))
            Button(onClick = onBack) { Text("Tilbage") }
        }
    }
}

/* ---------- Login/Register/Create ---------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    vm: AuthViewModel,
    onLoggedIn: () -> Unit = {},
    onGoRegister: () -> Unit = {}
) {
    val ui = vm.uiState

    Scaffold(topBar = { TopAppBar(title = { Text("Login") }) }) { inner ->
        Column(modifier = Modifier.padding(inner).padding(16.dp)) {

            Text("Email")
            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmailChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            Text("Password")
            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::onPasswordChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { vm.login(onSuccess = onLoggedIn) },
                enabled = !ui.isLoading
            ) {
                Text("Log ind")
            }

            TextButton(onClick = onGoRegister) { Text("Opret konto") }

            ui.error?.let {
                Spacer(Modifier.height(8.dp))
                Text("Fejl: $it")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    vm: AuthViewModel,
    onRegistered: () -> Unit = {}
) {
    val ui = vm.uiState

    Scaffold(topBar = { TopAppBar(title = { Text("Registrering") }) }) { inner ->
        Column(modifier = Modifier.padding(inner).padding(16.dp)) {

            Text("Email")
            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmailChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            Text("Password")
            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::onPasswordChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { vm.register(onSuccess = onRegistered) },
                enabled = !ui.isLoading
            ) {
                Text("Opret konto")
            }

            ui.error?.let {
                Spacer(Modifier.height(8.dp))
                Text("Fejl: $it")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateItemScreen(
    itemsVm: SalesItemsViewModel,
    authVm: AuthViewModel,
    onDone: () -> Unit = {}
) {
    val loggedIn = authVm.uiState.isLoggedIn

    var description by rememberSaveable { mutableStateOf("") }
    var priceText by rememberSaveable { mutableStateOf("") }
    var sellerPhone by rememberSaveable { mutableStateOf("") }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }
    var isSaving by rememberSaveable { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("Opret SalesItem") }) }) { inner ->
        Column(modifier = Modifier.padding(inner).padding(16.dp)) {

            if (!loggedIn) {
                Text("Du skal være logget ind for at oprette en vare.")
                Spacer(Modifier.height(8.dp))
                Button(onClick = onDone) { Text("Tilbage") }
                return@Column
            }

            
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Beskrivelse") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = { Text("Pris (kr)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = sellerPhone,
                onValueChange = { sellerPhone = it },
                label = { Text("Sælgers telefonnummer") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val price = priceText.replace(',', '.').toDoubleOrNull()
                    if (description.isBlank()) {
                        localError = "Beskrivelse må ikke være tom"
                        return@Button
                    }
                    if (price == null) {
                        localError = "Pris skal være et tal"
                        return@Button
                    }
                    if (sellerPhone.isBlank()) {
                        localError = "Telefonnummer må ikke være tomt"
                        return@Button
                    }
                    val sellerEmail = authVm.uiState.userEmail
                    if (sellerEmail == null) {
                        localError = "Du skal være logget ind med en gyldig email"
                        return@Button
                    }
                    isSaving = true
                    localError = null

                    itemsVm.createItem(
                        description = description,
                        price = price,
                        sellerEmail = sellerEmail,
                        sellerPhone = sellerPhone
                    ) { success, errorMsg ->
                        isSaving = false
                        if (success) {
                            onDone()
                        } else {
                            localError = errorMsg ?: "Kunne ikke oprette vare"
                        }
                    }
                },
                enabled = !isSaving
            ) {
                Text(if (isSaving) "Gemmer..." else "Gem")
            }


            localError?.let {
                Spacer(Modifier.height(8.dp))
                Text("Fejl: $it")
            }
        }
    }
}
