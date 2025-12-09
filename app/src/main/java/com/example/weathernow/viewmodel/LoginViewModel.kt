package com.example.weathernow.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
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


    fun firebaseAuthWithGoogleIdToken(googleIdTokenCredential: GoogleIdTokenCredential) {
        _loginUiState.value = LoginUiState.Loading // Muestra el indicador de carga

        val credential = GoogleAuthProvider.getCredential(
            googleIdTokenCredential.idToken,
            null
        )

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginUiState.value = LoginUiState.Success
                } else {
                    // Adaptamos el manejo de errores para Login
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas o cuenta no registrada."
                        else -> "Error de inicio de sesión con Google: ${task.exception?.message}"
                    }
                    _loginUiState.value = LoginUiState.Error(errorMessage)
                }
            }
    }

    // Función para manejar errores de CredentialManager antes de llegar a Firebase
    fun handleGoogleSignInFailure(exception: Exception) {
        // En un caso real, podrías querer un mensaje más detallado
        _loginUiState.value = LoginUiState.Error("Fallo al obtener credenciales de Google: ${exception.message}")
    }

    fun resetLoginState() {
        _loginUiState.value = LoginUiState.Idle
    }
}
