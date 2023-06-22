package com.example.myfirstapp

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.example.myfirstapp.ui.PasswordTextField
import com.example.myfirstapp.ui.StandardTextField
import com.example.myfirstapp.ui.StandardButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class InicioActivity : ComponentActivity() {
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]
        setContent {
            MyFirstAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    UserLogIn()
                }
            }
        }
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            redirigir()
        }
    }
    // [END on_start_check_user]

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(
                        baseContext,
                        "Bienvenid@ de vuelta!",
                        Toast.LENGTH_SHORT,
                    ).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        // [END sign_in_with_email]
    }

    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) {
                // Email Verification sent
            }
        // [END send_email_verification]
    }

    private fun redirigir() {
        //TODO DESCOMENTAR CUANDO ESTE TODO HECHO
        //startActivity(Intent(this, HomeActivity::class.java))
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    //Prueba de integración con FB
    @Composable
    fun UserLogIn() {
        //TODO DES-HARDCODEAR
        var email by remember { mutableStateOf("actualizado@gmail.com") }
        var password by rememberSaveable { mutableStateOf("abcdef123456") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.PrimaryColor))
        ) {
            Text(text="Bienvenid@!", style=MaterialTheme.typography.h3)
            Spacer(modifier = Modifier.weight(.02f))
            StandardTextField(string = email, label ="email", onValueChanged = { email = it }, Icons.Default.Email)
            PasswordTextField(password = password, onValueChanged = { password = it })
            SignInButton(email = email, password = password)
            Spacer(modifier = Modifier.weight(1f))
            CreateAccountButton()
        }
    }

    @Composable
    fun SignInButton(email: String, password: String) {
        StandardButton(onClick = { signIn(email, password) }, label="Iniciar Sesion")
    }

    @Composable
    fun CreateAccountButton() {
        StandardButton(
            onClick = { startActivity(Intent(this, CrearCuentaActivity::class.java)) },
            label="Crear Usuario"
        )
    }
}