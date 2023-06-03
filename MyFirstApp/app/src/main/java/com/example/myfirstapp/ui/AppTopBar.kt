package com.example.myfirstapp.ui

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.R

@Composable
fun AppTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { null },
        navigationIcon = {
            backIcon(onBackClick)
        },
        backgroundColor = colorResource(id = R.color.ThirdColor),
        elevation = 0.dp
    )
}