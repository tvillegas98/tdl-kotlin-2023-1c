package com.example.myfirstapp

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class RegistrarGastosActivity : ComponentActivity() {
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

        val categorias: MutableList<String> = mutableListOf()
        val fuentes:    MutableList<String> = mutableListOf()

        obtenerDocumentos("categories", categorias)
        obtenerDocumentos("sources", fuentes)

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
                DropdownMenu(
                    "Categoría",
                    categoria,
                    categorias,
                    { categoria = it }
                )
                TextField(
                    "Título",
                    titulo,
                    { titulo = it }
                )
                MontoTextField(
                    monto = monto,
                    onValueChanged = { monto = it }
                )
                TextField(
                    "Observaciones",
                    observaciones,
                    { observaciones = it }
                )
                DropdownMenu(
                    "Fuente",
                    fuente,
                    fuentes,
                    { fuente = it }
                )

                //TextField(asunto:String, onValueChange = {})
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

    fun obtenerDocumentos(nombreColeccion: String, lista: MutableList<String>) {
        val db = Firebase.firestore
        db.collection(nombreColeccion)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val documentData = document.getString("name")
                    documentData?.let {
                        lista.add(it)
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error obteniendo documentos: $exception")
            }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DropdownMenu(
        asuntoTextField: String,
        asunto: String,
        opciones: List<String>,
        onValueChanged: (String) -> Unit
    ) {

        val selectedItem = remember { mutableStateOf(asunto) }
        var expanded by remember { mutableStateOf(false) }

        Column {

            val isPlaceholderVisible = selectedItem.value.isEmpty()
            if (isPlaceholderVisible) {
                Text(
                    text = "Seleccionar $asuntoTextField",
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                }
            ) {
                TextField(
                    value = selectedItem.value,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    opciones.forEach { item ->
                        DropdownMenuItem(
                            onClick = {
                                selectedItem.value = item
                                expanded = false
                                onValueChanged(item)
                            },
                            text = {
                                Text(text = item)
                            }
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TextField(
        asuntoTextField: String,
        asunto: String,
        onValueChanged: (String) -> Unit
    ) {
        TextField(
            value = asunto,
            onValueChange = { newValue -> onValueChanged(newValue) },
            label = { Text("$asuntoTextField") }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MontoTextField(
        monto: Double,
        onValueChanged: (Double) -> Unit
    ) {
        TextField(
            value = monto.toString(),
            onValueChange = { newValue -> onValueChanged(newValue.toDouble()) },
            label = { Text("Monto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
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
                    "source"        to fuente,
                    "date"          to Timestamp(Date())
                )

                db.collection("gastos")
                    .add(gasto)
                    .addOnSuccessListener { documentReference ->
                        Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        Toast.makeText(
                            baseContext,
                            "Gasto Registrado.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        startActivity(Intent(this, HistorialGastosActivity::class.java))
                    }

                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error adding document", e)
                    }

            }) {
            Text(text = "Registrar Gasto")
        }
    }
}