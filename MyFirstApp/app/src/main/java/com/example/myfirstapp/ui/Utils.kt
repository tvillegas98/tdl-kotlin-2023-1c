package com.example.myfirstapp.ui


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

val greenColor = Color(0xFF0F9D58)
val blueColor = Color(0xFF2196F3)
val yellowColor = Color(0xFFFFC107)
val redColor = Color(0xFFF44336)
val pinkColor = Color(0xFFFF0CFF)
val orangeColor = Color(0xFFFF8F00)
val whiteColor = Color(0xFFFFFFFF)
val coloresPieChart = listOf<Color>(greenColor,blueColor,yellowColor, redColor, orangeColor, pinkColor)

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
fun StandardIconButton(accion : () -> Unit, icon : ImageVector){
    IconButton(
        onClick = accion
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier.size(35.dp)
        )
    }
}

@Composable
fun StandardBackButton(onBackClick: () -> Unit) {
    androidx.compose.material.IconButton(onClick = onBackClick) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ButtonPerfil(icon: ImageVector, label : String, action : () -> Unit) {
    Button(
        onClick = action,
        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            androidx.compose.material3.Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            androidx.compose.material3.Text(text = label)
        }
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

            androidx.compose.material3.Text(
                text = "Seleccionar $asuntoTextField",
                modifier = Modifier.padding(vertical = 16.dp)
            )

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

fun obtenerDocumentos(nombreColeccion: String, lista: MutableList<String>) {
    val db = Firebase.firestore
    db.collection(nombreColeccion)
        .get()
        .addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                val documentData = document.getString("name")
                documentData?.let {
                    lista.add(it)
                }
            }
        }
        .addOnFailureListener { exception ->
            println("Error obteniendo documentos: $exception")
        }
}