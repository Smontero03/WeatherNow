package com.example.weathernow.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

// Sealed class to represent registration state
sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String?) : RegisterUiState()
}

class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Field values
    private val _fullName = MutableStateFlow("")
    val fullName = _fullName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    // Validation errors
    private val _fullNameError = MutableStateFlow<String?>(null)
    val fullNameError = _fullNameError.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError = _confirmPasswordError.asStateFlow()

    private val _registerUiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerUiState = _registerUiState.asStateFlow()

    // Button enabled state
    val isRegisterButtonEnabled = combine(
        listOf( // Envuelve tus Flows en una lista
            fullName,
            email,
            password,
            confirmPassword,
            fullNameError,
            emailError,
            passwordError,
            confirmPasswordError
        )
    ) { values -> // La lambda ahora recibe un solo parámetro 'values' que es un Array
        // Desestructuramos el array para mayor claridad
        val fName = values[0] as String
        val mail = values[1] as String
        val pwd = values[2] as String
        val confPwd = values[3] as String
        val fNameErr = values[4]
        val mailErr = values[5]
        val pwdErr = values[6]
        val confPwdErr = values[7]

        // La lógica de validación sigue siendo la misma
        fName.isNotBlank() && mail.isNotBlank() && pwd.isNotBlank() && confPwd.isNotBlank() &&
                fNameErr == null && mailErr == null && pwdErr == null && confPwdErr == null
    }

    // Field change handlers with validation
    fun onFullNameChange(newFullName: String) {
        _fullName.value = newFullName
        _fullNameError.value =
            if (newFullName.any { it.isDigit() }) "No puede contener números." else null
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _emailError.value =
            if (newEmail.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail)
                    .matches()
            ) {
                "Correo electrónico inválido."
            } else {
                null
            }
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _passwordError.value = if (newPassword.isNotBlank() && newPassword.length < 6) {
            "La contraseña debe tener al menos 6 caracteres."
        } else {
            null
        }
        // Also validate confirm password if it's not empty
        if (_confirmPassword.value.isNotBlank()) {
            _confirmPasswordError.value =
                if (newPassword != _confirmPassword.value) "Las contraseñas no coinciden." else null
        }
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
        _confirmPasswordError.value =
            if (newConfirmPassword.isNotBlank() && newConfirmPassword != _password.value) {
                "Las contraseñas no coinciden."
            } else {
                null
            }
    }

    fun registerUser() {
        _registerUiState.value = RegisterUiState.Loading
        val currentEmail = email.value
        val currentPassword = password.value

        auth.createUserWithEmailAndPassword(currentEmail, currentPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _registerUiState.value = RegisterUiState.Success
                } else {
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil. Debe tener al menos 6 caracteres."
                        is FirebaseAuthInvalidCredentialsException -> "Correo electrónico no es válido."
                        is FirebaseAuthUserCollisionException -> "El correo electrónico ya está en uso."
                        else -> "Error de registro: ${task.exception?.message}"
                    }
                    _registerUiState.value = RegisterUiState.Error(errorMessage)
                }
            }
    }

    fun resetRegisterState() {
        _registerUiState.value = RegisterUiState.Idle
    }
}
