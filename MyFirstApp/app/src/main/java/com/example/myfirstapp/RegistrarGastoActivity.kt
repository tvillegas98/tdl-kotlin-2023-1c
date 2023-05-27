package com.example.myfirstapp

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistrarGastoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyFirstAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    registroDeGasto()
                }
            }
        }
    }

    @Composable
    fun registroDeGasto() {
        var categoria:      String by remember {mutableStateOf("")}
        var titulo:         String by remember { mutableStateOf("") }
        var monto:          Double by remember { mutableStateOf(0.00) }
        var observaciones:  String by remember { mutableStateOf("") }
        var fuente:         String by remember { mutableStateOf("") }
        //es compartido ??
        //se repetira ?? (cada semana o mes)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Yellow)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                CategoriaDropdownMenu(categoria = categoria, onValueChanged = { categoria = it })
                TituloTextField(titulo = titulo, onValueChanged = { titulo = it })
                MontoTextField(monto = monto, onValueChanged = { monto = it })
                ObservacionesTextField(observaciones = observaciones, onValueChanged = { observaciones = it })
                FuenteDropdownMenu(fuente = fuente, onValueChanged = { fuente = it })
                crearGastoButton(
                    categoria       = categoria,
                    titulo          = titulo,
                    monto           = monto,
                    observaciones   = observaciones,
                    fuente          = fuente
                )
            }
        }
    }

    @Composable
    fun CategoriaDropdownMenu(categoria: String, onValueChanged: (String) -> Unit) {
        val items = listOf("Gastronomia", "Entretenimiento", "Compras")

        val selectedItem = remember { mutableStateOf(categoria) }
        val expanded = remember { mutableStateOf(false) }

        Column {
            Text(text = "Elemento seleccionado: ${selectedItem.value}")
            Spacer(modifier = Modifier.height(16.dp))

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            selectedItem.value = item
                            expanded.value = false
                            onValueChanged(item)
                        },
                        text = {
                            Text(text = item)
                        }
                    )
                }
            }

            Button(onClick = { expanded.value = !expanded.value }) {
                Text(text = "Abrir/Cerrar")
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TituloTextField(titulo: String, onValueChanged: (String) -> Unit) {
        TextField(
            value = titulo,
            onValueChange = { newValue -> onValueChanged(newValue) },
            label = { Text("Título") }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MontoTextField(monto: Double, onValueChanged: (Double) -> Unit) {
        TextField(
            value = monto.toString(),
            onValueChange = { newValue -> onValueChanged(newValue.toDouble()) },
            label = { Text("Monto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ObservacionesTextField(observaciones: String, onValueChanged: (String) -> Unit) {
        TextField(
            value = observaciones,
            onValueChange = { newValue -> onValueChanged(newValue) },
            label = { Text("Observaciones") }
        )
    }

    @Composable
    fun FuenteDropdownMenu(fuente: String, onValueChanged: (String) -> Unit) {
        val items = listOf("Tarjeta de Crédito", "Tarjeta de Débito", "Efectivo")

        val selectedItem = remember { mutableStateOf(fuente) }
        val expanded = remember { mutableStateOf(false) }

        Column {
            Text(text = "Elemento seleccionado: ${selectedItem.value}")
            Spacer(modifier = Modifier.height(16.dp))

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            selectedItem.value = item
                            expanded.value = false
                            onValueChanged(item)
                        },
                        text = {
                            Text(text = item)
                        }
                    )
                }
            }

            Button(onClick = { expanded.value = !expanded.value }) {
                Text(text = "Abrir/Cerrar")
            }
        }
    }

    @Composable
    fun crearGastoButton(
            categoria: String,
            titulo: String,
            monto: Double,
            observaciones: String,
            fuente: String
    ) {
        val db = Firebase.firestore
        Button(
            onClick = {
                val gasto = hashMapOf(
                    "category"      to categoria,
                    "title"         to titulo,
                    "amount"        to monto,
                    "observations"  to observaciones,
                    "source"        to fuente
            )

            db.collection("gastos")
                .add(gasto)
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding document", e)
                }

        }) {
            Text(text = "Registrar Gasto")
        }
    }
}