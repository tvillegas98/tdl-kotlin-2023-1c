package com.example.myfirstapp

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.example.myfirstapp.ui.DropdownMenu
import com.example.myfirstapp.ui.StandardNavigationAppBar
import com.example.myfirstapp.ui.StandardNumberField
import com.example.myfirstapp.ui.obtenerDocumentos
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar


class PresupuestosActivity : ComponentActivity() {
    private val home = {startActivity(Intent(this, HomeActivity::class.java))}
    private val perfil = {startActivity(Intent(this, ProfileActivity::class.java))}
    private val presupuestos = {startActivity(Intent(this, PresupuestosActivity::class.java))}

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyFirstAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            StandardNavigationAppBar(
                                home=home,
                                perfil = perfil,
                                presupuestos = presupuestos
                            )
                        }
                    ) {
                        RegistrarPresupuesto()
                    }
                }
            }
        }
    }
    @Composable
    fun RegistrarPresupuesto(){
        var categoria:      String by remember {mutableStateOf("")}
        var montoBase: String by remember {mutableStateOf("")}
        val categorias: MutableList<String> = mutableListOf()
        val currentFirebaseUser = Firebase.auth.currentUser

        obtenerDocumentos("categories", categorias)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.PrimaryColor))
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                Text(
                    text = "¿Cuánto querés gastar por mes?"
                )
                DropdownMenu(
                    "Categoría",
                    categoria,
                    categorias,
                    { categoria = it }
                )
                StandardNumberField(
                    string = montoBase,
                    label = "Monto",
                    onValueChanged = {montoBase = it},
                    icon = Icons.Default.ShoppingCart
                )
                CrearPresupuestoButton(
                    categoria = categoria,
                    montoBase = montoBase,
                    userUID = currentFirebaseUser!!.uid
                )
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Composable
    fun CrearPresupuestoButton(
        categoria: String,
        montoBase: String,
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
                    "creationDate" to fechaDelPresupuesto,
                    "spentAmount" to 0.0
                )


                db.collection("presupuestos")
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
                            db.collection("presupuestos")
                                .document(documentReference.documents[0].id)
                                .update("baseAmount", montoBase)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        baseContext,
                                        "Se actualizó el monto base para esa categoría.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error updating document", e)
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Hubo un error al realizar la query", e)
                    }
            }
        ) {
            Text(text = "Crear Presupuesto")
        }
    }
}