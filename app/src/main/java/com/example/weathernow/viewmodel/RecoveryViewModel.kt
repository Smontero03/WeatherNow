package com.example.weathernow.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

// Sealed class to represent password recovery state
sealed class RecoveryUiState {
    object Idle : RecoveryUiState()
    object Loading : RecoveryUiState()
    object Success : RecoveryUiState()
    data class Error(val message: String?) : RecoveryUiState()
}

class RecoveryViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _uiState = MutableStateFlow<RecoveryUiState>(RecoveryUiState.Idle)
    val uiState = _uiState.asStateFlow()

    val isButtonEnabled = combine(email, emailError) { emailValue, emailErrorValue ->
        emailValue.isNotBlank() && emailErrorValue == null
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _emailError.value = if (newEmail.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            "Formato de correo electrónico inválido."
        } else {
            null
        }
    }

    fun sendPasswordResetEmail() {
        if (email.value.isBlank() || emailError.value != null) {
            return
        }

        _uiState.value = RecoveryUiState.Loading
        auth.sendPasswordResetEmail(email.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = RecoveryUiState.Success
                } else {
                    _uiState.value = RecoveryUiState.Error(task.exception?.message ?: "Error al enviar el correo de restablecimiento.")
                }
            }
    }

    fun resetState() {
        _uiState.value = RecoveryUiState.Idle
        _emailError.value = null
    }
}
