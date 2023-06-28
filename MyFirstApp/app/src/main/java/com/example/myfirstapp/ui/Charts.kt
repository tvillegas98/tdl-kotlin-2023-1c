package com.example.myfirstapp.ui

import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.lang.Math.round
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.ArrayList

// on below line we are adding different colors.

// on below line we are creating data class for
// pie chart data and passing variable as browser
// name and value.
data class ChartData(
    var browserName: String?,
    var value: Double?
)

enum class ChartType {
    PIE,
    BAR
}

fun convertToChartData(mapa: Map<String, Double>, chartType: ChartType ): List<ChartData> {
    val listaChartData = mutableListOf<ChartData>()

    val sumaTotal = mapa.values.sum()

    for ((clave, valor) in mapa) {
        val chartData = if (chartType == ChartType.BAR) {
            ChartData(browserName = clave, value = valor)
        } else {
            ChartData(browserName = clave, value = (valor/sumaTotal) * 100)
        }
        listaChartData.add(chartData)
    }
    return  listaChartData
}

fun colorToArgb(coloresLista: List<Color>): List<Int> {
    val res = coloresLista.map {color ->
        color.toArgb()
    }
    return  res
}
@Composable
fun DrawChart(
    chartData: List<ChartData>,
    chartType: ChartType,
    chartSize: Dp = 250.dp,
    labels: List<String> = emptyList()
) {
    Column(
        modifier = Modifier.padding(top = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .size(chartSize),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Crossfade(targetState = chartData) { data ->
                AndroidView(factory = { context ->
                    val chart = when (chartType) {
                        ChartType.PIE -> PieChart(context)
                        ChartType.BAR -> BarChart(context)
                    }

                    chart.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    chart.description.isEnabled = false
                    chart.legend.isEnabled = true
                    chart.legend.textSize = 14F
                    chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    chart.legend.isEnabled = false

                    if (chartType == ChartType.BAR && labels.isNotEmpty()) {
                        val xAxis = chart.xAxis
                        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.setDrawGridLines(false)
                        xAxis.granularity = 1f
                        xAxis.textSize = 15f
                        xAxis.typeface = Typeface.DEFAULT_BOLD
                        xAxis.labelRotationAngle = 45f
                    }
                    chart
                },
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(5.dp), update = { chart ->
                        when (chartType) {
                            ChartType.PIE -> updatePieChartWithData(chart as PieChart, data)
                            ChartType.BAR -> updateBarChartWithData(chart as BarChart, data)
                        }
                    }
                )
            }
        }
    }
}

fun updatePieChartWithData(chart: PieChart, data: List<ChartData>) {
    val entries = ArrayList<PieEntry>()

    for (i in data.indices) {
        val item = data[i]
        entries.add(PieEntry(item.value?.toFloat() ?: 0.toFloat(), item.browserName ?: ""))
    }

    val dataSet: PieDataSet
    if (entries.isEmpty()) {
        entries.add(PieEntry(100f))
        dataSet = PieDataSet(entries, "Sin Gastos")
        dataSet.color = Color.Gray.toArgb()
        dataSet.setDrawValues(false)
    } else {
        dataSet = PieDataSet(entries, "")
        dataSet.colors = colorToArgb(coloresPieChart)
        dataSet.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
        dataSet.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
        dataSet.sliceSpace = 2f
        dataSet.valueTextColor = whiteColor.toArgb()
        dataSet.valueTextSize = 15f
        dataSet.valueTypeface = Typeface.DEFAULT_BOLD
        dataSet.setDrawValues(false)
    }

    chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener{
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            if (e != null) {
                val dosDecimales = BigDecimal(e.y.toDouble())
                    .setScale(2, RoundingMode.HALF_UP)
                chart.centerText = "${dosDecimales}%"
                chart.setCenterTextSize(25F)
                chart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
            }
        }

        override fun onNothingSelected() {
            chart.centerText = ""
        }
    })

    chart.animateY(1000)
    chart.setDrawEntryLabels(false)
    val pieData = PieData(dataSet)
    chart.data = pieData
    chart.invalidate()
}

fun updateBarChartWithData(chart: BarChart, data: List<ChartData>) {
    val entries = ArrayList<BarEntry>()

    for (i in data.indices) {
        val item = data[i]
        entries.add(BarEntry(i.toFloat(), item.value?.toFloat() ?: 0.toFloat()))
    }

    val dataSet = BarDataSet(entries, "Prueba")
    dataSet.colors = colorToArgb(coloresPieChart)
    dataSet.valueTextSize = 15f
    dataSet.valueTypeface = Typeface.DEFAULT_BOLD
    dataSet.setDrawValues(false)

    val barData = BarData(dataSet)
    chart.data = barData
    chart.invalidate()
}

@Composable
fun DrawPieChart(gastosPorCatgoria: Map<String, Double>) {
    val dataPieChart = convertToChartData(gastosPorCatgoria, ChartType.PIE)
    DrawChart(chartData = dataPieChart, chartType = ChartType.PIE, chartSize = 250.dp)
}

@Composable
fun DrawBarChart(frecCategoriaPorFecha: Map<String, Double>) {
    val dataBarChart = convertToChartData(frecCategoriaPorFecha, ChartType.BAR)
    val labels = frecCategoriaPorFecha.keys.toList()
    DrawChart(chartData = dataBarChart, chartType = ChartType.BAR, chartSize = 450.dp, labels = labels)
}






