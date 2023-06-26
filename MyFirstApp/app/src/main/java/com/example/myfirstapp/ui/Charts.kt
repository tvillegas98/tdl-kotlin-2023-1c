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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.ArrayList

// on below line we are adding different colors.

// on below line we are creating data class for
// pie chart data and passing variable as browser
// name and value.
data class ChartData(
    var browserName: String?,
    var value: Double?
)

// on below line we are creating a
// pie chart function on below line.
@Composable
fun DrawPieChart(gastosPorCatgoria: Map<String, Double>) {
    val dataPieChart = convertToPieChartData(gastosPorCatgoria)

    // on below line we are again creating a column
    // with modifier and horizontal and vertical arrangement
    Column(
        modifier = Modifier.padding(top=18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // on below line we are creating a simple text
        // and specifying a text as Web browser usage share
        Text(
            text = "Distribucion de Gastos",

            // on below line we are specifying style for our text
            style = TextStyle.Default,

            // on below line we are specifying font family.
            fontFamily = FontFamily.Default,

            // on below line we are specifying font style
            fontStyle = FontStyle.Normal,

            // on below line we are specifying font size.
            fontSize = 20.sp
        )

        // on below line we are creating a column and
        // specifying the horizontal and vertical arrangement
        // and specifying padding from all sides.
        Column(
            modifier = Modifier
                .padding(18.dp)
                .size(250.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // on below line we are creating a cross fade and
            // specifying target state as pie chart data the
            // method we have created in Pie chart data class.
            Crossfade(targetState = dataPieChart) { pieChartData ->
                // on below line we are creating an
                // android view for pie chart.
                AndroidView(factory = { context ->
                    // on below line we are creating a pie chart
                    // and specifying layout params.
                    PieChart(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            // on below line we are specifying layout
                            // params as MATCH PARENT for height and width.
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        // on below line we are setting description
                        // enables for our pie chart.
                        this.description.isEnabled = false

                        // on below line we are setting draw hole
                        // to false not to draw hole in pie chart
                        this.isDrawHoleEnabled = false

                        // on below line we are enabling legend.
                        this.legend.isEnabled = true

                        // on below line we are specifying
                        // text size for our legend.
                        this.legend.textSize = 14F

                        // on below line we are specifying
                        // alignment for our legend.
                        this.legend.horizontalAlignment =
                            Legend.LegendHorizontalAlignment.CENTER

                        // deshabilito los labels dentro del chart
                        this.setDrawEntryLabels(false)

                        this.legend.isEnabled = false

                        this.animateY(1000)

                        // on below line we are specifying entry label color as white.
                        //this.setEntryLabelColor(R.color.white)
                    }
                },
                    // on below line we are specifying modifier
                    // for it and specifying padding to it.
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(5.dp), update = {
                        // on below line we are calling update pie chart
                        // method and passing pie chart and list of data.
                        updatePieChartWithData(it, pieChartData)
                    })
            }
        }
    }
}

fun convertToPieChartData(gastosPorCatgoria: Map<String, Double>): List<ChartData> {
    val listaPieChartData = mutableListOf<ChartData>()

    val sumaTotal = gastosPorCatgoria.values.sum()

    for ((clave, valor) in gastosPorCatgoria) {
        val pieChartData = ChartData(browserName = clave, value = (valor/sumaTotal) * 100)
        listaPieChartData.add(pieChartData)
    }
    return  listaPieChartData
}

// on below line we are creating a update pie
// chart function to update data in pie chart.
fun updatePieChartWithData(
    // on below line we are creating a variable
    // for pie chart and data for our list of data.
    chart: PieChart,
    data: List<ChartData>
) {
    // on below line we are creating
    // array list for the entries.
    val entries = ArrayList<PieEntry>()

    // on below line we are running for loop for
    // passing data from list into entries list.
    for (i in data.indices) {
        val item = data[i]
        entries.add(PieEntry(item.value?.toFloat() ?: 0.toFloat(), item.browserName ?: ""))
    }

    // on below line we are creating
    // a variable for pie data set.
    val ds : PieDataSet
    if (entries.isEmpty()) {
        entries.add(PieEntry(100f))
        ds = PieDataSet(entries, "Sin Gastos")
        ds.color = Color.Gray.toArgb()
        ds.setDrawValues(false)
    }
    else {
        ds = PieDataSet(entries, "")

        // Convierto la lista de colores en una lista de colores Argb
        val colores = colorToArgb(coloresPieChart)

        // on below line we are specifying color
        // int the array list from colors.
        ds.colors = colores
        // on below line we are specifying position for value
        ds.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

        // on below line we are specifying position for value inside the slice.
        ds.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

        // on below line we are specifying
        // slice space between two slices.
        ds.sliceSpace = 2f

        // on below line we are specifying text color
        ds.valueTextColor = whiteColor.toArgb()

        // on below line we are specifying
        // text size for value.
        ds.valueTextSize = 15f

        // on below line we are specifying type face as bold.
        ds.valueTypeface = Typeface.DEFAULT_BOLD

        ds.setDrawValues(false)
    }

    // on below line we are creating
    // a variable for pie data
    val d = PieData(ds)

    // on below line we are setting this
    // pie data in chart data.
    chart.data = d

    // on below line we are
    // calling invalidate in chart.
    chart.invalidate()
}

fun colorToArgb(coloresLista: List<Color>): List<Int> {
    val res = coloresLista.map {color ->
        color.toArgb()
    }
    return  res
}


@Composable
fun DrawBarChart(frecCategoriaPorFecha: Map<String, Double>) {
    val dataBarChart = convertToPieChartData(frecCategoriaPorFecha)

    // on below line we are again creating a column
    // with modifier and horizontal and vertical arrangement
    Column(
        modifier = Modifier.padding(top=18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // on below line we are creating a column and
        // specifying the horizontal and vertical arrangement
        // and specifying padding from all sides.
        Column(
            modifier = Modifier
                .padding(18.dp)
                .size(450.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // on below line we are creating a cross fade and
            // specifying target state as pie chart data the
            // method we have created in Pie chart data class.
            Crossfade(targetState = dataBarChart) { pieChartData ->
                // on below line we are creating an
                // android view for pie chart.
                AndroidView(factory = { context ->
                    // on below line we are creating a pie chart
                    // and specifying layout params.
                    BarChart(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            // on below line we are specifying layout
                            // params as MATCH PARENT for height and width.
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        // on below line we are setting description
                        // enables for our pie chart.
                        this.description.isEnabled = false

                        // on below line we are enabling legend.
                        this.legend.isEnabled = true

                        // on below line we are specifying
                        // text size for our legend.
                        this.legend.textSize = 14F

                        // on below line we are specifying
                        // alignment for our legend.
                        this.legend.horizontalAlignment =
                            Legend.LegendHorizontalAlignment.CENTER

                        this.legend.isEnabled = false

                        this.animateX(1000)

                        val labels = frecCategoriaPorFecha.keys.toList()
                        val xAxis = this.xAxis
                        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.setDrawGridLines(false)
                        xAxis.granularity = 1f
                        xAxis.textSize = 15f
                        xAxis.typeface = Typeface.DEFAULT_BOLD
                        // on below line we are specifying entry label color as white.
                        //this.setEntryLabelColor(R.color.white)
                    }
                },
                    // on below line we are specifying modifier
                    // for it and specifying padding to it.
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(5.dp), update = {
                        // on below line we are calling update pie chart
                        // method and passing pie chart and list of data.
                        updateBarChartWithData(it, pieChartData)
                    }
                )
            }
        }
    }
}

fun updateBarChartWithData(chart: BarChart, data: List<ChartData>) {
        // on below line we are creating
        // array list for the entries.
        val entries = ArrayList<BarEntry>()

        // on below line we are running for loop for
        // passing data from list into entries list.
        for (i in data.indices) {
            val item = data[i]
            entries.add(BarEntry(i.toFloat(), item.value?.toFloat() ?: 0.toFloat()))
        }

        // on below line we are creating
        // a variable for pie data set.
        val ds = BarDataSet(entries, "Prueba")

        // Convierto la lista de colores en una lista de colores Argb
        val colores = colorToArgb(coloresPieChart)

        // on below line we are specifying color
        // int the array list from colors.
        ds.colors = colores

        // on below line we are specifying text color
//        ds.valueTextColor = whiteColor.toArgb()

        // on below line we are specifying
        // text size for value.
        ds.valueTextSize = 15f

        // on below line we are specifying type face as bold.
        ds.valueTypeface = Typeface.DEFAULT_BOLD

        ds.setDrawValues(false)

        // on below line we are creating
        // a variable for pie data
        val d = BarData(ds)

        // on below line we are setting this
        // pie data in chart data.
        chart.data = d

        // on below line we are
        // calling invalidate in chart.
        chart.invalidate()
}
