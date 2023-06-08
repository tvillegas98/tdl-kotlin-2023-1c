package com.example.myfirstapp

import DrawPieChart
import PieChartData
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myfirstapp.ui.StandardNavigationAppBar
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import blueColor
import com.example.myfirstapp.ui.StandardButton
import com.example.myfirstapp.ui.StandardIconButton
import com.github.mikephil.charting.components.Legend
import getPieChartData
import greenColor
import redColor
import yellowColor
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
                        home()
                    }
                }
            }
        }
    }

    @Composable
    fun home() {
        Column(
            modifier=Modifier.background(color=Color.Blue),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DrawPieChart()

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp)
                    .background(color= Color.Red),
                horizontalArrangement = Arrangement.SpaceBetween

                    ){
                StandardButton(onClick = historialGastos, label = "Historial Completo")
                StandardButton(onClick = registrarGastos, label = "Nuevo Gasto")
//                StandardIconButton(accion = registrarGastos, icon = Icons.Default.Add)
            }
        }
    }

}
