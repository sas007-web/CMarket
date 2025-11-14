package com.example.cmarket.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

// Alt det vi viser på login/registrering + status
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val userEmail: String? = null
)

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Hele auth-tilstanden som Compose kan observere
    var uiState by mutableStateOf(AuthUiState())
        private set

    init {
        // Hvis en bruger allerede er logget ind, opdater state
        val user = auth.currentUser
        if (user != null) {
            uiState = uiState.copy(
                isLoggedIn = true,
                userEmail = user.email
            )
        }
    }

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value)
    }

    fun login(onSuccess: () -> Unit = {}) {
        val email = uiState.email
        val password = uiState.password
        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(error = "Email og password må ikke være tomme")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    uiState = uiState.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userEmail = user?.email,
                        password = "" // ryd password
                    )
                    onSuccess()
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = task.exception?.message ?: "Login fejlede"
                    )
                }
            }
    }

    fun register(onSuccess: () -> Unit = {}) {
        val email = uiState.email
        val password = uiState.password
        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(error = "Email og password må ikke være tomme")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    uiState = uiState.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userEmail = user?.email,
                        password = ""
                    )
                    onSuccess()
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = task.exception?.message ?: "Registrering fejlede"
                    )
                }
            }
    }

    fun logout() {
        auth.signOut()
        uiState = AuthUiState() // reset alt
    }
}
