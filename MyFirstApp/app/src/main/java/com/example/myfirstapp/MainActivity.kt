package com.example.myfirstapp

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.Date
import androidx.compose.runtime.LaunchedEffect
import java.text.SimpleDateFormat
import java.util.Locale


//class MainActivity : ComponentActivity() {
//
//    // [START declare_auth]
//    private lateinit var auth: FirebaseAuth
//    // [END declare_auth]
//
//    public override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // [START initialize_auth]
//        // Initialize Firebase Auth
//        auth = Firebase.auth
//        // [END initialize_auth]
//        setContent {
//            MyFirstAppTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    UserLogIn()
//                }
//            }
//        }
//    }
//
//    // [START on_start_check_user]
//    public override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            reload()
//        }
//    }
//    // [END on_start_check_user]
//
//    private fun createAccount(email: String, password: String) {
//        // [START create_user_with_email]
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "createUserWithEmail:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        baseContext,
//                        "Authentication failed.",
//                        Toast.LENGTH_SHORT,
//                    ).show()
//                    updateUI(null)
//                }
//            }
//        // [END create_user_with_email]
//    }
//
//    private fun signIn(email: String, password: String) {
//        // [START sign_in_with_email]
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithEmail:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        baseContext,
//                        "Authentication failed.",
//                        Toast.LENGTH_SHORT,
//                    ).show()
//                    updateUI(null)
//                }
//            }
//        // [END sign_in_with_email]
//    }
//
//    private fun sendEmailVerification() {
//        // [START send_email_verification]
//        val user = auth.currentUser!!
//        user.sendEmailVerification()
//            .addOnCompleteListener(this) { task ->
//                // Email Verification sent
//            }
//        // [END send_email_verification]
//    }
//
//    private fun updateUI(user: FirebaseUser?) {
//    }
//
//    private fun reload() {
//    }
//
//    companion object {
//        private const val TAG = "EmailPassword"
//    }
//
//    //Prueba de integración con FB
//    @Composable
//    fun UserLogIn() {
//        var email by remember { mutableStateOf("santiago.ruiz.sugliani@gmail.com") }
//        var password by rememberSaveable { mutableStateOf("abcdef123456") }
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.Cyan)
//        ) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .align(Alignment.Center)
//            ) {
//                SimpleFilledTextFieldSample(email = email, onValueChanged = { email = it })
//                PasswordTextField(password = password, onValueChanged = { password = it })
//                createAccountButton(email = email, password = password)
//            }
//        }
//    }
//
//    @OptIn(ExperimentalMaterial3Api::class)
//    @Composable
//    fun SimpleFilledTextFieldSample(email: String, onValueChanged: (String) -> Unit) {
//        TextField(
//            value = email,
//            onValueChange = { newValue -> onValueChanged(newValue) },
//            label = { Text("Email") }
//        )
//    }
//
//    @OptIn(ExperimentalMaterial3Api::class)
//    @Composable
//    fun PasswordTextField(password: String, onValueChanged: (String) -> Unit) {
//        TextField(
//            value = password,
//            onValueChange = { newValue -> onValueChanged(newValue) },
//            label = { Text("Password") },
//            visualTransformation = PasswordVisualTransformation(),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//        )
//    }
//
//    @Composable
//    fun createAccountButton(email: String, password: String) {
//        Button(
//            onClick = {createAccount(email, password)}
//        ) {
//            Text(text = "Crear usuario")
//        }
//    }
//
//    @Composable
//    fun StoreButton(email: String, password: String) {
//        val db = Firebase.firestore
//        Button(onClick = {
//            // Crear un nuevo usuario con email y contraseña
//            val user = hashMapOf(
//                "Email" to email,
//                "Password" to password
//            )
//
//            // Agregar un nuevo documento con un ID generado
//            db.collection("users")
//                .add(user)
//                .addOnSuccessListener { documentReference ->
//                    Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                }
//                .addOnFailureListener { e ->
//                    Log.w(ContentValues.TAG, "Error adding document", e)
//                }
//
//        }) {
//            Text(text = "Crear usuario")
//        }
//    }
//}



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyFirstAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HistorialDeGastos()
                    //registroDeGasto()
                }
            }
        }
    }

    @Composable
    fun HistorialDeGastos() {
        val db = Firebase.firestore
        val gastos = remember { mutableStateOf(emptyList<List<String>>()) }

        LaunchedEffect(Unit) {
            db.collection("gastos")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val tempList = mutableListOf<List<String>>()
                    for (document in querySnapshot) {
                        val gastoList = mutableListOf<String>()
                        document.getString("title")?.let {
                            gastoList.add("Titulo: $it")
                        }
                        document.getString("category")?.let {
                            gastoList.add("Categoria: $it")
                        }
                        document.getDouble("amount")?.let {
                            gastoList.add("Precio: $$it")
                        }
                        document.getDate("date")?.let { date ->
                            val formateoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            val fecha = formateoFecha.format(date)
                            gastoList.add("Fecha: $fecha")
                        }
                        document.getString("source")?.let {
                            gastoList.add("Fuente: $it")
                        }
                        document.getString("observations")?.let {
                            gastoList.add("Observaciones: $it")
                        }
                        tempList.add(gastoList)
                    }
                    gastos.value = tempList
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        baseContext,
                        "ERROR",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
        }

        Column {
            gastos.value.forEach { lista ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        lista.forEach { item ->
                            Text(text = item)
                        }
                    }
                }
            }
        }

    }

    @Composable
    fun registroDeGasto() {
        var categoria:      String by remember {mutableStateOf("")}
        var titulo:         String by remember { mutableStateOf("") }
        var monto:          Double by remember { mutableStateOf(0.00) }
        var observaciones:  String by remember { mutableStateOf("") }
        var fuente:         String by remember { mutableStateOf("") }
        //es compartido ??
        //se repetira ?? (cada semana o mes)

        val categorias: MutableList<String> = mutableListOf()
        val fuentes:    MutableList<String> = mutableListOf()

        obtenerDocumentos("categories", categorias)
        obtenerDocumentos("sources", fuentes)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Yellow)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                DropdownMenu(
                    "Categoría",
                    categoria,
                    categorias,
                    { categoria = it }
                )
                TextField(
                    "Título",
                    titulo,
                    { titulo = it }
                )
                MontoTextField(
                    monto = monto,
                    onValueChanged = { monto = it }
                )
                TextField(
                    "Observaciones",
                    observaciones,
                    { observaciones = it }
                )
                DropdownMenu(
                    "Fuente",
                    fuente,
                    fuentes,
                    { fuente = it }
                )

                //TextField(asunto:String, onValueChange = {})
                crearGastoButton(
                    categoria       = categoria,
                    titulo          = titulo,
                    monto           = monto,
                    observaciones   = observaciones,
                    fuente          = fuente
                )
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TextField(
        asuntoTextField: String,
        asunto: String,
        onValueChanged: (String) -> Unit
    ) {
        TextField(
            value = asunto,
            onValueChange = { newValue -> onValueChanged(newValue) },
            label = { Text("$asuntoTextField") }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MontoTextField(
        monto: Double,
        onValueChanged: (Double) -> Unit
    ) {
        TextField(
            value = monto.toString(),
            onValueChange = { newValue -> onValueChanged(newValue.toDouble()) },
            label = { Text("Monto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
    }

    @Composable
    fun crearGastoButton(
        categoria: String,
        titulo: String,
        monto: Double,
        observaciones: String,
        fuente: String
    ) {
        val db = Firebase.firestore
        Button(
            onClick = {
                val gasto = hashMapOf(
                    "category"      to categoria,
                    "title"         to titulo,
                    "amount"        to monto,
                    "observations"  to observaciones,
                    "source"        to fuente,
                    "date"          to Timestamp(java.util.Date())
                )

                db.collection("gastos")
                    .add(gasto)
                    .addOnSuccessListener { documentReference ->
                        Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        Toast.makeText(
                            baseContext,
                            "Gasto Registrado.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }

                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error adding document", e)
                    }

            }) {
            Text(text = "Registrar Gasto")
        }
    }
}