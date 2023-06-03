package com.example.myfirstapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.example.myfirstapp.ui.AppTopBar
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.example.myfirstapp.ui.PasswordTextField
import com.example.myfirstapp.ui.StandardTextField
import com.example.myfirstapp.ui.StandardButton
import com.google.firebase.auth.FirebaseAuth

class crearCuentaActivity : ComponentActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter",
        "UnusedMaterialScaffoldPaddingParameter"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            MyFirstAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = { AppTopBar(title="Crear Cuenta",
                                            onBackClick = { startActivity(Intent(this, InicioActivity::class.java)) }
                        ) }
                    ) {
                        askForUserData()
                    }
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    private fun askForUserData() {
        var email by remember { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var nombre by rememberSaveable { mutableStateOf("") }
        var apellido by rememberSaveable { mutableStateOf("") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.PrimaryColor))
        ) {
            StandardTextField(string = nombre, label ="Nombre", onValueChanged = { nombre = it }, icon = Icons.Default.Person)
            StandardTextField(string = apellido, label ="Apellido", onValueChanged = { apellido = it }, icon = Icons.Default.Person)
            StandardTextField(string = email, label ="Email",onValueChanged = { email = it }, icon=Icons.Default.Email)
            PasswordTextField(password = password, onValueChanged = { password = it })
            createAccountButton(email = email, password = password, nombreCompleto = nombre+apellido)
        }

    }


    @Composable
    fun createAccountButton(email: String, password: String, nombreCompleto: String) {
        StandardButton(onClick = { verifyAccount(email, password, nombreCompleto) }, label = "Crear Usuario")
    }

    private fun verifyAccount(email:String, password:String, nombreCompleto:String) {
        //Verifico que el email no este registrado a otra cuenta
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods
                if (signInMethods.isNullOrEmpty()) {
                    createAccount(email,password)
                } else {
                    Toast.makeText(
                        baseContext,
                        "Ya Existe una Cuenta Asociada al Email.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            } else {
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    startActivity(Intent(this, RegistrarGastosActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

}
