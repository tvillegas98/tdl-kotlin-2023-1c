package com.example.myfirstapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
//import androidx.compose.ui.Alignment
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.R

@Composable
//@Preview
fun StandardNavigationAppBar(home: () -> Unit, registrarGastos: () -> Unit, perfil: () -> Unit, historialGastos: () -> Unit, presupuestos: () -> Unit) {
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

                // REGISTRAR GASTO
//                StandardIconButton(accion = registrarGastos, icon = Icons.Filled.Add)

//                // HISTORIAL GASTOS
//                StandardIconButton(accion = historialGastos, icon = Icons.Filled.List)

                // PRESUPUESTOS
                StandardIconButton(accion = presupuestos, icon = Icons.Filled.Wallet)

                // ESTADISTICAS / GRAFICOS
                StandardIconButton(accion = { /*TODO*/ }, icon = Icons.Filled.Info)
            }
        },
        containerColor = colorResource(id = R.color.FourthColor),
    )
}

@Composable
fun StandardTopAppBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { null },
        navigationIcon = {
            StandardBackButton(onBackClick)
        },
        backgroundColor = colorResource(id = R.color.FourthColor),
        elevation = 0.dp
    )
}