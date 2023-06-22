package com.example.myfirstapp

import com.example.myfirstapp.ui.DrawPieChart
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import com.example.myfirstapp.ui.CategoryIconBox
import com.example.myfirstapp.ui.StandardButton
import com.example.myfirstapp.ui.StandardNavigationAppBar
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
                            perfil = perfil,
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
        val gastosPorCategoria: Map<String, Double> = obtenerGastoPorCategoria()
        val mapOrdenado : Map<String, Double> = sortMapByValue(gastosPorCategoria)

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
        var indice = 0
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
                        CategoryIconBox(categoria, indice)
                        Text(categoria)
                        Text("$$monto")
                    }
                }
                indice++
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Composable
    private fun obtenerGastoPorCategoria(): Map<String, Double> {
        val currentFirebaseUser = Firebase.auth.currentUser
        val db = Firebase.firestore
        val gastosPorCategoria= remember { mutableStateOf(emptyMap<String, Double>()) }

        LaunchedEffect(Unit) {
            db.collection("gastos")
                .whereEqualTo("userUID", currentFirebaseUser!!.uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val tempMap = mutableMapOf<String, Double>()
                    for (document in querySnapshot) {
                        val categoria = document.getString("category")
                        val monto = document.getDouble("amount")

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

    private fun sortMapByValue(map: Map<String, Double>): Map<String, Double> {
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
