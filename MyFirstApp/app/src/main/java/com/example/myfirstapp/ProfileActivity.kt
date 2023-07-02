package com.example.myfirstapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.ui.ButtonPerfil
import com.example.myfirstapp.ui.StandardButton
import com.example.myfirstapp.ui.StandardNavigationAppBar
import com.example.myfirstapp.ui.getUserData
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileActivity : ComponentActivity() {
    /* NavBar */
    private val home            = { startActivity(Intent(this, HomeActivity::class.java)) }
    private val perfil          = { startActivity(Intent(this, ProfileActivity::class.java)) }
    private val presupuestos    = { startActivity(Intent(this, PresupuestosActivity::class.java)) }
    private val contactos       = { startActivity(Intent(this, ContactsActivity::class.java)) }
    private val graficos = {startActivity(Intent(this, GraficosActivity::class.java))}

    /* Profile */
    private val datos = {startActivity(Intent(this, PersonalDataActivity::class.java))}

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyFirstAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.white)
                ) {
                    Scaffold(
                        bottomBar = {
                            StandardNavigationAppBar(
                                home=home,
                                perfil = perfil,
                                presupuestos = presupuestos,
                                graficos = graficos
                            )
                        }
                    ) {
                        ProfileMenu()
                    }
                }
            }
        }
    }

    @Composable
    fun ProfileMenu() {
        val emailState = rememberSaveable { mutableStateOf("") }
        val firstNameState = rememberSaveable { mutableStateOf("") }
        val lastNameState = rememberSaveable { mutableStateOf("") }
        val birthDayState = rememberSaveable { mutableStateOf("") }

        LaunchedEffect(Unit) {
            getUserData(emailState, firstNameState, lastNameState, birthDayState)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom=80.dp)
                .background(color = colorResource(id = R.color.white))
        ) {
            androidx.compose.material.Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.height(300.dp).width(300.dp)
            )

            Text(text = firstNameState.value, style= androidx.compose.material.MaterialTheme.typography.h3)
            //Text(text = "Nivel $level", style= androidx.compose.material.MaterialTheme.typography.h5)

            ButtonPerfil(icon = Icons.Default.Person,       label = "Datos Personales",     onClick = datos)
            ButtonPerfil(icon = Icons.Default.Groups,       label = "Contactos",            onClick = contactos)
            ButtonPerfil(icon = Icons.Default.Settings,     label = "Configuración",        onClick = {})
            ButtonPerfil(icon = Icons.Default.Warning,      label = "Botón de baja",        onClick = {})
            ButtonPerfil(icon = Icons.Default.Info,         label = "Preguntas frecuentes", onClick = {})

            SignOutButton()
        }
    }

    @Composable
    fun SignOutButton() {
        StandardButton(onClick = { signOut() }, label="Cerrar sesión")
    }

    private fun signOut() {
        Firebase.auth.signOut()
        startActivity(Intent(this, InicioActivity::class.java))
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MyFirstAppTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Scaffold(
                    bottomBar = { StandardNavigationAppBar(home=home, perfil = perfil, presupuestos=presupuestos, graficos=graficos) }
                ) {
                    ProfileMenu()
                }
            }
        }
    }
}