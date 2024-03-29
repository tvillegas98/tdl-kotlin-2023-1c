package com.example.myfirstapp.ui

//import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.R

@Composable
//@Preview
fun StandardNavigationAppBar(home: () -> Unit, perfil: () -> Unit, presupuestos: () -> Unit, graficos: () -> Unit) {
    androidx.compose.material3.BottomAppBar(
        actions = {
            Row (
                modifier =Modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                //Home
                StandardIconButton(accion = home, icon = Icons.Filled.Home)

                //PERFIL
                StandardIconButton(accion = perfil, icon = Icons.Filled.Person)

                // PRESUPUESTOS
                StandardIconButton(accion = presupuestos, icon = Icons.Filled.Wallet)

                // ESTADISTICAS / GRAFICOS
                StandardIconButton(accion = graficos, icon = Icons.Filled.Leaderboard)
            }
        },
        containerColor = colorResource(id = R.color.FourthColor),
    )
}

@Composable
fun StandardTopAppBar(
    onBackClick: () -> Unit,
    title : String? = null
) {
    TopAppBar(
        title = {
            if (title != null) {
                Text(
                    text= title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start=10.dp)
                )
            }
        },
        navigationIcon = {
            StandardIconButton(onBackClick, Icons.Default.ArrowBack, iconColorTintId= R.color.black)
        },
        backgroundColor = colorResource(id = R.color.FourthColor),
        elevation = 0.dp
    )
}