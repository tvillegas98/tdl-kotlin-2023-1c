package com.example.myfirstapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.ui.DropdownMenu
import com.example.myfirstapp.ui.StandardNumberField
import com.example.myfirstapp.ui.StandardTextField
import com.example.myfirstapp.ui.navigationAppBar
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class RegistrarGastosActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
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
                        bottomBar = { navigationAppBar() }
                    ) {
                        registroDeGasto()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    fun registroDeGasto() {
        var categoria:      String by remember {mutableStateOf("")}
        var titulo:         String by remember { mutableStateOf("") }
        var monto:          String by remember { mutableStateOf("") }
        var observaciones:  String by remember { mutableStateOf("") }
        var fuente:         String by remember { mutableStateOf("") }
        //es compartido ??
        //se repetira ?? (cada semana o mes)

        val categorias: MutableList<String> = mutableListOf()
        val fuentes:    MutableList<String> = mutableListOf()

        obtenerDocumentos("categories", categorias)
        obtenerDocumentos("sources", fuentes)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.PrimaryColor))
        ) {
            DropdownMenu(
                "Categoría",
                categoria,
                categorias,
                { categoria = it }
            )
            StandardTextField(
                string = titulo,
                label = "Titulo",
                onValueChanged = { titulo = it },
                icon = Icons.Default.Edit
            )
            StandardNumberField(
                string = monto,
                label = "Monto",
                onValueChanged = {monto = it},
                icon = Icons.Default.ShoppingCart
            )
            StandardTextField(
                string = observaciones,
                label = "Observaciones",
                onValueChanged = { observaciones = it },
                icon = Icons.Default.Edit
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


    @Composable
    fun crearGastoButton(
        categoria: String,
        titulo: String,
        monto: String,
        observaciones: String,
        fuente: String
    ) {
        val db = Firebase.firestore
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.FourthColor)),
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

            }
        ) {
            Text(text = "Registrar Gasto")
        }
    }
}