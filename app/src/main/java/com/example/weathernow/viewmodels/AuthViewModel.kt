package com.example.weathernow.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _authMessage = mutableStateOf<String?>(null)
    val authMessage: State<String?> = _authMessage

    fun registerUser(
        id: String,
        nombres: String,
        correo: String,
        tipoVehiculo: String,
        password: String
    ) {
        auth.createUserWithEmailAndPassword(correo, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val userId = auth.currentUser!!.uid

                    val userData = mapOf(
                        "id" to id,
                        "nombres" to nombres,
                        "correo" to correo,
                        "tipoVehiculo" to tipoVehiculo
                    )

                    db.collection("usuarios")
                        .document(userId)
                        .set(userData)
                        .addOnSuccessListener {
                            _authMessage.value = "Registro exitoso"
                        }
                        .addOnFailureListener {
                            _authMessage.value = "Error al guardar datos: ${it.message}"
                        }
                } else {
                    _authMessage.value = "Error en registro: ${task.exception?.message}"
                }
            }
    }
}
