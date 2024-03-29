package com.example.myfirstapp.ui


import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.ButtonDefaults.shape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import me.saket.cascade.CascadeDropdownMenu

val greenColor = Color(0xFF0F9D58)
val blueColor = Color(0xFF2196F3)
val yellowColor = Color(0xFFFFC107)
val redColor = Color(0xFFF44336)
val pinkColor = Color(0xFFFF0CFF)
val orangeColor = Color(0xFFFF8F00)
val whiteColor = Color(0xFFFFFFFF)
val coloresPieChart = listOf(greenColor,blueColor,yellowColor, redColor, orangeColor, pinkColor)

@Composable
fun OutlinedTextFieldBackground(
    color: Color,
    content: @Composable () -> Unit
) {
    // This box just wraps the background and the OutlinedTextField
    Box {
        // This box works as background
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 8.dp) // adding some space to the label
                .background(
                    color,
                    // rounded corner to match with the OutlinedTextField
                    shape = RoundedCornerShape(4.dp)
                )
        )
        // OutlineTextField will be the content...
        content()
    }
}

@Composable
fun StandardTextField(string: String, label: String, onValueChanged: (String) -> Unit, icon: ImageVector) {
    OutlinedTextFieldBackground(colorResource(id = R.color.white)) {
        OutlinedTextField(
            value = string,
            onValueChange = { newValue -> onValueChanged(newValue) },
            label = { Text(label) },
            leadingIcon = { Icon(imageVector = icon, contentDescription = null)}
        )
    }

}

@Composable
fun StandardNumberField(string: String, label: String, onValueChanged: (String) -> Unit, icon: ImageVector) {
    OutlinedTextFieldBackground(colorResource(id = R.color.white)) {
        OutlinedTextField(
            value = string,
            onValueChange = { newValue -> onValueChanged(newValue) },
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Icon(imageVector = icon, contentDescription = null)} ,
        )
    }
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
fun StandardIconButton(accion : () -> Unit, icon : ImageVector, iconColorTintId: Int = R.color.white, iconBackgroundColorId: Int = R.color.FourthColor){
    IconButton(
        onClick = accion,
        modifier = Modifier
            .background(color = colorResource(id = iconBackgroundColorId), shape)
            .border(
                2.dp,
                color = colorResource(
                    id = R.color.black
                ),
                shape
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            tint = colorResource(id = iconColorTintId),
            modifier = Modifier.size(35.dp)
        )
    }
}

@Composable
fun ButtonPerfil(icon: ImageVector, label : String, onClick : () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
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
    OutlinedTextFieldBackground(colorResource(id = R.color.white)) {
        OutlinedTextField(
            value = password,
            onValueChange = { newValue -> onValueChanged(newValue) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
        )
    }
}

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
        OutlinedTextFieldBackground(colorResource(id = R.color.white)) {
            OutlinedTextField(
                value = selectedItem.value,
                onValueChange = {},
                trailingIcon = {
                    IconButton(
                        onClick = { expanded = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Desplegar",
                            tint = Color.Black
                        )
                    }
                },
                label = { Text(asuntoTextField) },
                readOnly = true
            )
        }

        CascadeDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach {item ->
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
@Composable
fun CategoryIconBox(category: String, colorIndex: Int) {
    val icon = when (category) {
        "General" -> Icons.Outlined.ShoppingBag
        "Entretenimiento" -> Icons.Outlined.SportsEsports
        "Viajes" -> Icons.Outlined.Flight
        "Salud" -> Icons.Outlined.MedicalServices
        "Gastronomia" -> Icons.Outlined.Fastfood
        "Compras" -> Icons.Outlined.Sell
        else -> Icons.Outlined.QuestionMark
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color = coloresPieChart[colorIndex], RoundedCornerShape(16.dp))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

fun getUserData(
    emailState: MutableState<String>,
    firstNameState: MutableState<String>,
    lastNameState: MutableState<String>,
    birthDayState: MutableState<String>
) {
    val db = Firebase.firestore
    val currentFirebaseUser = Firebase.auth.currentUser
    val userId = currentFirebaseUser!!.uid

    val query = db.collection("usersData").whereEqualTo("userId", userId)

    query.get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Se encontró un documento, con lo cual el usuario guardó sus datos y precargamos los inputs.
                val documentSnapshot = querySnapshot.documents[0]
                val retrievedEmail = documentSnapshot.getString("email") ?: ""
                val retrievedFirstName = documentSnapshot.getString("firstName") ?: ""
                val retrievedLastName = documentSnapshot.getString("lastName") ?: ""
                val retrievedBirthDay = documentSnapshot.getString("birthDay") ?: ""

                emailState.value = retrievedEmail
                firstNameState.value = retrievedFirstName
                lastNameState.value = retrievedLastName
                birthDayState.value = retrievedBirthDay
            }
        }
        .addOnFailureListener { e ->
            Log.w(ContentValues.TAG, "Error getting documents", e)
        }
}