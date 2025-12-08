package com.example.weathernow.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

// Sealed class to represent login state
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String?) : LoginUiState()
}

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginUiState = _loginUiState.asStateFlow()

    val isLoginButtonEnabled = combine(email, password, emailError) { email, password, emailError ->
        email.isNotBlank() && password.isNotBlank() && emailError == null
    }

    fun onEmailChange(newEmail: String) {
        _email.update { newEmail }
        _emailError.value = if (newEmail.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            "Correo electrónico inválido."
        } else {
            null
        }
    }

    fun onPasswordChange(newPassword: String) {
        _password.update { newPassword }
    }

    fun login() {
        _loginUiState.value = LoginUiState.Loading
        val currentEmail = email.value
        val currentPassword = password.value

        auth.signInWithEmailAndPassword(currentEmail, currentPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginUiState.value = LoginUiState.Success
                } else {
                    _loginUiState.value = LoginUiState.Error(task.exception?.message)
                }
            }
    }

    fun resetLoginState() {
        _loginUiState.value = LoginUiState.Idle
    }
}
