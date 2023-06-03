package com.example.myfirstapp.ui

import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.R

@Composable
//@Preview
fun StandardNavigationAppBar(registrarGastos: () -> Unit, perfil: () -> Unit, historialGastos: () -> Unit) {
    androidx.compose.material3.BottomAppBar(
        actions = {
            //PERFIL
            StandardIconButton(accion = perfil, icon = Icons.Filled.Person)

            // REGISTRAR GASTO
            StandardIconButton(accion = registrarGastos, icon = Icons.Filled.Add)

            // HISTORIAL GASTOS
            StandardIconButton(accion = historialGastos , icon = Icons.Filled.List)

            // ESTADISTICAS / GRAFICOS
            StandardIconButton(accion = { /*TODO*/ }, icon = Icons.Filled.Info)

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
        backgroundColor = colorResource(id = R.color.ThirdColor),
        elevation = 0.dp
    )
}