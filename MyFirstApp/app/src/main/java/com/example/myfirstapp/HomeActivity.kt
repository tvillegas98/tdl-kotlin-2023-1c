package com.example.myfirstapp

import DrawPieChart
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import com.example.myfirstapp.ui.StandardNavigationAppBar
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.myfirstapp.ui.StandardButton
import com.example.myfirstapp.ui.coloresPieChart
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : ComponentActivity() {
    private val home = {startActivity(Intent(this, HomeActivity::class.java))}
    private val registrarGastos = {startActivity(Intent(this, RegistrarGastosActivity::class.java))}
    private val historialGastos = {startActivity(Intent(this, HistorialGastosActivity::class.java))}
    private val perfil = {startActivity(Intent(this, ProfileActivity::class.java))}
    private val presupuestos = {startActivity(Intent(this, PresupuestosActivity::class.java))}

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyFirstAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        bottomBar = { StandardNavigationAppBar(
                            home=home,
                            registrarGastos=registrarGastos,
                            perfil = perfil,
                            historialGastos=historialGastos,
                            presupuestos = presupuestos )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        HomeScreen()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Composable
    private fun HomeScreen() {
        val gastosPorCategoria: Map<String, Float> = obtenerGastoPorCategoria()
        val mapOrdenado : Map<String, Float> = sortMapByValue(gastosPorCategoria)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            DrawPieChart(mapOrdenado)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StandardButton(onClick = registrarGastos, label = "Nuevo Gasto")
                StandardButton(onClick = historialGastos, label = "Historial Completo")
            }

            DrawSummary(mapOrdenado)
        }
    }

    @Composable
    private fun DrawSummary(gastosPorCategoria: Map<String, Float>) {
        var indice : Int = 0
        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState(), enabled = true)
                .padding(bottom = 80.dp)
        ) {
            gastosPorCategoria.forEach { (categoria, monto) ->
                Card (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable(onClick = historialPorCategoria(categoria)),
                    elevation = 8.dp, // Configura la elevación para aplicar una sombra
                    shape = RoundedCornerShape(8.dp), // Configura la forma redondeada del borde de la Card
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .background(color = coloresPieChart[indice])
                        )
                        Text("$categoria")
                        Text("$$monto")
                    }
                }
                indice++
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Composable
    private fun obtenerGastoPorCategoria(): Map<String, Float> {
        val currentFirebaseUser = Firebase.auth.currentUser
        val db = Firebase.firestore
        val gastosPorCategoria= remember { mutableStateOf(emptyMap<String, Float>()) }

        LaunchedEffect(Unit) {
            db.collection("gastos")
                .whereEqualTo("userUID", currentFirebaseUser!!.uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val tempMap = mutableMapOf<String, Float>()
                    for (document in querySnapshot) {
                        val categoria = document.getString("category")
                        val monto = document.getString("amount")?.toFloatOrNull()

                        if (categoria != null && monto != null) {
                            if (tempMap.containsKey(categoria)) {
                                val valorExistente = tempMap.getValue(categoria)
                                tempMap[categoria] = valorExistente + monto
                            } else {
                                tempMap[categoria] = monto
                            }
                        }
                    }
                    gastosPorCategoria.value = tempMap
                }
                .addOnFailureListener {
                    Toast.makeText(
                        baseContext,
                        "ERROR",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
        }

        return gastosPorCategoria.value
    }

    private fun sortMapByValue(map: Map<String, Float>): Map<String, Float> {
        return map.toList()
            .sortedByDescending { (_, value) -> value }
            .toMap()
    }

    private fun historialPorCategoria(categoria : String): () -> Unit {
        val intent = Intent(this, HistorialGastosActivity::class.java)
        intent.putExtra("categoria", categoria)
        return {startActivity(intent)}
    }
}
