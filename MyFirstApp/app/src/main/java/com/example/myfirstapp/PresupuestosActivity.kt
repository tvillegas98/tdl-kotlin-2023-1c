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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myfirstapp.ui.DropdownMenu
import com.example.myfirstapp.ui.StandardNavigationAppBar
import com.example.myfirstapp.ui.StandardNumberField
import com.example.myfirstapp.ui.obtenerDocumentos
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar


class PresupuestosActivity : ComponentActivity() {
    private val home = {startActivity(Intent(this, HomeActivity::class.java))}
    private val registrarGastos = {startActivity(Intent(this, RegistrarGastosActivity::class.java))}
    private val historialGastos = {startActivity(Intent(this, HistorialGastosActivity::class.java))}
    private val perfil = {startActivity(Intent(this, ProfileActivity::class.java))}
    private val presupuestos = {startActivity(Intent(this, PresupuestosActivity::class.java))}

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
                    var mostrarPresupuestoModal by remember {
                        mutableStateOf(false)
                    }
                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(onClick = {
                                mostrarPresupuestoModal = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Crear Presupuesto"
                                )
                            }
                        },
                        bottomBar = {
                            StandardNavigationAppBar(
                                registrarGastos = registrarGastos,
                                perfil = perfil,
                                historialGastos = historialGastos,
                                presupuestos = presupuestos,
                                home = home
                            )
                        }
                    ) {
                        // registrarPresupuesto()
                        registrarPresupuestoModal(show = mostrarPresupuestoModal, onDismiss = {
                            mostrarPresupuestoModal = false
                        }, onDone = {
                            mostrarPresupuestoModal = false
                        })
                    }
                }
            }
        }
    }

    @Composable
    fun registrarPresupuestoModal(
        show: Boolean,
        onDismiss: () -> Unit,
        onDone: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        var categoria: String by remember { mutableStateOf("") }
        var montoBase: String by remember { mutableStateOf("") }
        val categorias: MutableList<String> = mutableListOf()
        val currentFirebaseUser = Firebase.auth.currentUser

        obtenerDocumentos("categories", categorias)

        if (show) {
            Dialog(
                onDismissRequest = { onDismiss() }, properties = DialogProperties(
                    dismissOnBackPress = false
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = modifier
                        .fillMaxWidth()
                        .background(
                            colorResource(id = R.color.PrimaryColor),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "¿Cuánto querés gastar por mes?"
                    )
                    DropdownMenu(
                        "Categoría",
                        categoria,
                        categorias,
                        { categoria = it }
                    )

                    Spacer(modifier = modifier.height(8.dp))

                    StandardNumberField(
                        string = montoBase,
                        label = "Monto",
                        onValueChanged = { montoBase = it },
                        icon = Icons.Default.ShoppingCart
                    )
                    Spacer(modifier = modifier.height(8.dp))

                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        crearPresupuestoButton(
                            categoria = categoria,
                            montoBase = montoBase,
                            userUID = currentFirebaseUser!!.uid,
                            onDone = onDone
                        )

                    }
                }
            }
        }
    }

    @Composable
    fun crearPresupuestoButton(
        categoria: String,
        montoBase: String,
        userUID: String,
        onDone: () -> Unit
    ) {
        val calendario: Calendar = GregorianCalendar()
        val dateFormat = SimpleDateFormat("MM-yyyy")
        val fechaDelPresupuesto = dateFormat.format(calendario.time).toString()
        val db = Firebase.firestore

        Button(
            onClick = {
                val presupuesto = hashMapOf(
                    "category" to categoria,
                    "baseAmount" to montoBase,
                    "userUID" to userUID,
                    "creationDate" to fechaDelPresupuesto,
                    "spentAmount" to 0.0
                )


                db.collection("presupuestos")
                    .whereEqualTo("category", categoria)
                    .get()
                    .addOnSuccessListener { documentReference ->
                        if (documentReference.documents.isEmpty()) {
                            db.collection("presupuestos")
                                .add(presupuesto)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(
                                        ContentValues.TAG,
                                        "DocumentSnapshot added with ID: ${documentReference.id}"
                                    )
                                    Toast.makeText(
                                        baseContext,
                                        "Presupuesto registrado.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                    startActivity(Intent(this, RegistrarGastosActivity::class.java))
                                    onDone()
                                }

                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error adding document", e)
                                }
                        } else {
                            db.collection("presupuestos")
                                .document(documentReference.documents[0].id)
                                .update("baseAmount", montoBase)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        baseContext,
                                        "Se actualizó el monto base para esa categoría.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                    onDone()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error updating document", e)
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Hubo un error al realizar la query", e)
                    }
            }
        ) {
            Text(text = "Crear Presupuesto")
        }
    }
}