package com.example.myfirstapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.ui.StandardButton
import com.example.myfirstapp.ui.StandardNavigationAppBar
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileActivity : ComponentActivity() {
    private val registrarGastos = {startActivity(Intent(this, RegistrarGastosActivity::class.java))}
    private val historialGastos = {startActivity(Intent(this, HistorialGastosActivity::class.java))}
    private val perfil = {startActivity(Intent(this, ProfileActivity::class.java))}

    @RequiresApi(Build.VERSION_CODES.M)
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
                        bottomBar = { StandardNavigationAppBar(registrarGastos=registrarGastos, perfil = perfil, historialGastos=historialGastos) }
                    ) {
                        ProfileMenu("Santiago", 1)
                    }
                }
            }
        }
    }

    @Composable
    fun ProfileMenu(name: String, level: Int) {
        val img = painterResource(id = R.drawable.pngtreeavatar_bussinesman_man_profile_icon_7268049)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.white))
        ) {
            Image(painter = img, contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier
                .height(300.dp)
                .width(300.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally))
            Text(text = name, style= androidx.compose.material.MaterialTheme.typography.h3)
            Text(text = "Nivel $level", style= androidx.compose.material.MaterialTheme.typography.h5)
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Datos personales")
                }
            }
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                    Text(text = "Configuración")
                }
            }
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null)
                    Text(text = "Botón de baja")
                }
            }
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null)
                    Text(text = "Preguntas frecuentes")
                }
            }
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white)),
                modifier = Modifier
                    .align(Alignment.Start)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null)
                    Text(text = "Soporte")
                }
            }
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
                    bottomBar = { StandardNavigationAppBar(registrarGastos=registrarGastos, perfil = perfil, historialGastos=historialGastos) }
                ) {
                    ProfileMenu("Santiago", 1)
                }
            }
        }
    }
}