package com.example.myfirstapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector


data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(
        name = "Add",
        route = "RegistrarGastosActivity",
        icon = Icons.Rounded.Add,
    ),
    BottomNavItem(
        name = "Info",
        route = "HistorialGastosActivity",
        icon = Icons.Rounded.Info,
    ),
    BottomNavItem(
        name = "",
        route = "",
        icon = Icons.Rounded.List,
    ),
    BottomNavItem(
        name = "",
        route = "",
        icon = Icons.Rounded.Person,
    )
)