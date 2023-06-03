package com.example.myfirstapp.ui


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.R


@RequiresApi(Build.VERSION_CODES.M)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardTextField(string: String, label: String, onValueChanged: (String) -> Unit, icon: ImageVector) {
    OutlinedTextField(
        value = string,
        onValueChange = { newValue -> onValueChanged(newValue) },
        label = { Text(label) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null)} ,
        modifier = Modifier.background(color = colorResource(id = R.color.white))
    )
}

@Composable
fun StandardNumberField(string: String, label: String, onValueChanged: (String) -> Unit, icon: ImageVector) {
    OutlinedTextField(
        value = string,
        onValueChange = { newValue -> onValueChanged(newValue) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        leadingIcon = { Icon(imageVector = icon, contentDescription = null)} ,
        modifier = Modifier.background(color = colorResource(id = R.color.white))
    )
}

@Composable
fun StandardButton(onClick: () -> Unit, label: String) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.FourthColor))
    ) {
        Text(text = label)
    }
}

@Composable
//@Preview
fun PasswordTextField(password: String, onValueChanged: (String) -> Unit) {

    OutlinedTextField(
        value = password,
        onValueChange = { newValue -> onValueChanged(newValue) },
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
        modifier = Modifier.background(color = colorResource(id = R.color.white))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(
    asuntoTextField: String,
    asunto: String,
    opciones: List<String>,
    onValueChanged: (String) -> Unit
) {

    val selectedItem = remember { mutableStateOf(asunto) }
    var expanded by remember { mutableStateOf(false) }

    Column {

        val isPlaceholderVisible = selectedItem.value.isEmpty()
        if (isPlaceholderVisible) {
            androidx.compose.material3.Text(
                text = "Seleccionar $asuntoTextField",
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = it
            }
        ) {
            TextField(
                value = selectedItem.value,
                onValueChange = { },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
                )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opciones.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            selectedItem.value = item
                            expanded = false
                            onValueChanged(item)
                        },
                        text = {
                            androidx.compose.material3.Text(text = item)
                        }
                    )
                }
            }
        }
    }
}