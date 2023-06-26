package com.example.myfirstapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstapp.ui.DrawBarChart
import com.example.myfirstapp.ui.StandardButton
import com.example.myfirstapp.ui.StandardNavigationAppBar
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Locale

class GraficosActivity : ComponentActivity() {
    private val home = {startActivity(Intent(this, HomeActivity::class.java))}
    private val perfil = {startActivity(Intent(this, ProfileActivity::class.java))}
    private val presupuestos = {startActivity(Intent(this, PresupuestosActivity::class.java))}
    private val graficos = {startActivity(Intent(this, GraficosActivity::class.java))}

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
                        bottomBar = {
                            StandardNavigationAppBar(
                                home=home,
                                perfil = perfil,
                                presupuestos = presupuestos,
                                graficos = graficos
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        ResumenGeneral()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun ResumenGeneral() {
        val initialDate = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)
        val showDialog = rememberSaveable { mutableStateOf(false) }

        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            StandardButton(onClick = {showDialog.value = true}, label = "Seleccionar Fecha")

            if (showDialog.value) {
                DatePickerDialog(
                    onDismissRequest = { showDialog.value = false },
                    confirmButton = {
                        TextButton(
                            onClick = { showDialog.value = false }
                        )
                        {
                            Text("Ok")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog.value = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            } else {
                datePickerState.selectedDateMillis?.let {
                    val fechaSeleccionada = milisecToDate(datePickerState.selectedDateMillis!!)
                    BarPlotPorFecha(fechaSeleccionada)
                }
            }
        }
    }

    @Composable
    private fun BarPlotPorFecha(fecha: String) {
        val frecCategoriaPorFecha : Map<String,Double>  = frecuenciaCategoriaPorFecha(fecha)
        if (frecCategoriaPorFecha.isEmpty()) {
            Card (
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(text="No hay gastos registrados el $fecha", textAlign = TextAlign.Center)
            }
        } else {
            Spacer(modifier = Modifier.width(25.dp))

            Text(
                text = "Distribucion de Gastos del $fecha",
                fontSize = 20.sp
            )
            DrawBarChart(frecCategoriaPorFecha = frecCategoriaPorFecha)
        }
    }

    @Composable
    private fun frecuenciaCategoriaPorFecha(fecha: String): Map<String, Double> {
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
                        document.getDate("date")?.let { date ->
                            val formateoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val fechaGasto = formateoFecha.format(date)

                            if (fechaGasto == fecha) {
                                document.getString("category")?.let{categoria ->
                                    val frecuenciaActual = tempMap.getOrDefault(key = categoria, defaultValue = 0.0)
                                    tempMap[categoria] = frecuenciaActual + 1
                                }
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
            delay(3000L) // Este delay está para asegurarnos de que la consulta llegue
        }
        return gastosPorCategoria.value
    }

    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @return String representing date in specified format
     */
    private fun milisecToDate(milliSeconds: Long): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat("dd/MM/yyyy")

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

}