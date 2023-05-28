package com.example.myfirstapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class HistorialGastosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyFirstAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HistorialDeGastos()
                }
            }
        }
    }
    @Composable
    fun HistorialDeGastos() {
        val db = Firebase.firestore
        val gastos = remember { mutableStateOf(emptyList<List<String>>()) }

        LaunchedEffect(Unit) {
            db.collection("gastos")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val tempList = mutableListOf<List<String>>()
                    for (document in querySnapshot) {
                        val gastoList = mutableListOf<String>()
                        document.getString("title")?.let {
                            gastoList.add("Titulo: $it")
                        }
                        document.getString("category")?.let {
                            gastoList.add("Categoria: $it")
                        }
                        document.getDouble("amount")?.let {
                            gastoList.add("Precio: $$it")
                        }
                        document.getDate("date")?.let { date ->
                            val formateoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            val fecha = formateoFecha.format(date)
                            gastoList.add("Fecha: $fecha")
                        }
                        document.getString("source")?.let {
                            gastoList.add("Fuente: $it")
                        }
                        document.getString("observations")?.let {
                            gastoList.add("Observaciones: $it")
                        }
                        tempList.add(gastoList)
                    }
                    gastos.value = tempList
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        baseContext,
                        "ERROR",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
        }

        Column {
            gastos.value.forEach { lista ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        lista.forEach { item ->
                            Text(text = item)
                        }
                    }
                }
            }
        }

    }
}