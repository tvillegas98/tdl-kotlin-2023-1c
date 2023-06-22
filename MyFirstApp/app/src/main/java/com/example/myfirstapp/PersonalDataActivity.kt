package com.example.myfirstapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.myfirstapp.ui.StandardButton
import com.example.myfirstapp.ui.StandardNavigationAppBar
import com.example.myfirstapp.ui.StandardTextField
import com.example.myfirstapp.ui.StandardTopAppBar
import com.example.myfirstapp.ui.getUserData
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PersonalDataActivity : ComponentActivity() {
    private val home            = {startActivity(Intent(this, HomeActivity::class.java))}
    private val perfil          = {startActivity(Intent(this, ProfileActivity::class.java))}
    private val presupuestos    = {startActivity(Intent(this, PresupuestosActivity::class.java))}

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyFirstAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            StandardTopAppBar(onBackClick=perfil)
                        },
                        bottomBar = {
                            StandardNavigationAppBar(
                                home            = home,
                                perfil          = perfil,
                                presupuestos    = presupuestos
                            )
                        }
                    ) {
                        PersonalData()
                    }
                }
            }
        }
    }

    @Composable
    fun PersonalData() {
        val emailState = rememberSaveable { mutableStateOf("") }
        val firstNameState = rememberSaveable { mutableStateOf("") }
        val lastNameState = rememberSaveable { mutableStateOf("") }
        val birthDayState = rememberSaveable { mutableStateOf("") }

        LaunchedEffect(Unit) {
            getUserData(emailState, firstNameState, lastNameState, birthDayState)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.white))
        ) {
            StandardTextField(
                string = firstNameState.value,
                label = "Nombre",
                onValueChanged = { firstNameState.value = it },
                icon = Icons.Default.ContactPage
            )
            StandardTextField(
                string = lastNameState.value,
                label = "Apellido",
                onValueChanged = { lastNameState.value = it },
                icon = Icons.Default.ContactPage
            )
            StandardTextField(
                string = emailState.value,
                label = "Email",
                onValueChanged = { emailState.value = it },
                icon = Icons.Default.Email
            )
            StandardTextField(
                string = birthDayState.value,
                label = "Nacimiento",
                onValueChanged = { birthDayState.value = it },
                icon = Icons.Default.Celebration
            )
            StandardButton(
                onClick = {
                            saveUserData(
                                        emailState.value,
                                        firstNameState.value,
                                        lastNameState.value,
                                        birthDayState.value
                                        )
                          },
                label = "Guardar"
            )
        }
    }

    private fun saveUserData(email: String, firstName: String, lastName: String, birthDay: String) {
        val db = Firebase.firestore
        val currentFirebaseUser = Firebase.auth.currentUser
        val userId = currentFirebaseUser!!.uid

        // Consulta para verificar si existe un documento para el usuario actual
        val query = db.collection("usersData").whereEqualTo("userId", userId)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // No se encontró ningún documento, agregar uno nuevo
                    val data = hashMapOf(
                        "email"         to email,
                        "firstName"     to firstName,
                        "lastName"      to lastName,
                        "birthDay"      to birthDay,
                        "userId"        to userId
                    )

                    db.collection("usersData")
                        .add(data)
                        .addOnSuccessListener { documentReference ->
                            Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error adding document", e)
                        }
                } else {
                    // Se encontró un documento, actualizar los campos relevantes
                    val documentSnapshot = querySnapshot.documents[0]
                    val documentId = documentSnapshot.id

                    db.collection("usersData")
                        .document(documentId)
                        .update(
                            "email", email,
                            "firstName", firstName,
                            "lastName", lastName,
                            "birthDay", birthDay
                        )
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "DocumentSnapshot updated with ID: $documentId")
                            Toast.makeText(
                                baseContext,
                                "Datos guardados con éxito.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error updating document", e)
                            Toast.makeText(
                                baseContext,
                                "Ocurrió un error al guardar tus datos.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error getting documents", e)
            }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MyFirstAppTheme {
            PersonalData()
        }
    }
}
