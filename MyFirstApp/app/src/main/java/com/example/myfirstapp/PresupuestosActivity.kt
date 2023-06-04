package com.example.myfirstapp

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.GregorianCalendar


class PresupuestosActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyFirstAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    registrarPresupuesto()
                }
            }
        }
    }
    @Composable
    fun registrarPresupuesto(){
        var categoria:      String by remember {mutableStateOf("")}
        var montoBase: Double by remember { mutableStateOf(0.0) }
        val categorias: MutableList<String> = mutableListOf()
        val currentFirebaseUser = Firebase.auth.currentUser

        obtenerDocumentos("categories", categorias)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Cyan)
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                Text(
                    text = "¿Cuánto querés gastar por mes?",
                    color = Color.Gray
                )
                DropdownMenu(
                    "Categoría",
                    categoria,
                    categorias,
                    { categoria = it }
                )
                MontoTextField(monto = montoBase, onValueChanged = {montoBase = it})

                crearPresupuestoButton(
                    categoria = categoria,
                    montoBase = montoBase,
                    userUID = currentFirebaseUser!!.uid
                )
            }
        }
    }

    /* Helpers */
    private fun obtenerDocumentos(nombreColeccion: String, lista: MutableList<String>) {
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MontoTextField(
        monto: Double,
        onValueChanged: (Double) -> Unit
    ) {
        TextField(
            value = monto.toString(),
            onValueChange = { newValue -> onValueChanged(newValue.toDouble()) },
            label = { Text("Monto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                Text(
                    text = "Seleccionar $asuntoTextField",
                    color = Color.Gray,
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
                                Text(text = item)
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun crearPresupuestoButton(
        categoria: String,
        montoBase: Double,
        userUID: String
    ) {
        val calendario: Calendar = GregorianCalendar()
        val dateFormat = SimpleDateFormat("MM-yyyy")
        val fechaDelPresupuesto = dateFormat.format(calendario.time).toString()
        val db = Firebase.firestore
        Button(
            onClick = {
                val presupuesto = hashMapOf(
                    "category"      to categoria,
                    "baseAmount"    to montoBase,
                    "userUID" to userUID,
                    "date" to fechaDelPresupuesto
                )


                db.collection("presupuestos")
                    .whereEqualTo("date", fechaDelPresupuesto)
                    .whereEqualTo("category", categoria)
                    .get()
                    .addOnSuccessListener {documentReference ->
                        if(documentReference.documents.isEmpty()){
                            db.collection("presupuestos")
                                .add(presupuesto)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                                    Toast.makeText(
                                        baseContext,
                                        "Presupuesto registrado.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                    startActivity(Intent(this, RegistrarGastosActivity::class.java))
                                }

                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error adding document", e)
                                }
                        }else{
                            Toast.makeText(
                                baseContext,
                                "Ya existe un presupuesto para este mes y categoría.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Hubo un error al realizar la query", e)
                    }
            },
            modifier = Modifier.padding(vertical = 24.dp)
        ) {
            Text(text = "Crear Presupuesto")
        }
    }
}