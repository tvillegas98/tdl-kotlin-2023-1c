package com.example.myfirstapp

import android.annotation.SuppressLint
import android.content.ContentValues
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.ui.StandardIconButton
import com.example.myfirstapp.ui.StandardTextField
import com.example.myfirstapp.ui.StandardTopAppBar
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class ContactsActivity : ComponentActivity() {
    private val perfil          = {startActivity(Intent(this, ProfileActivity::class.java))}

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
                        topBar = {
                            StandardTopAppBar(onBackClick=perfil)
                        }
                    ) {
                        ContactsLayer()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    fun ContactsLayer() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.white))
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AddNewContactSection()
            ContactsSection()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    fun AddNewContactSection() {
        var newContactEmail by remember { mutableStateOf("") }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(color = colorResource(id = R.color.ThirdColor))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(text = "Ingresa el mail del contacto que deseas agregar")
                StandardTextField(
                    string = newContactEmail,
                    label = "Email",
                    onValueChanged = { newContactEmail = it },
                    icon = Icons.Default.Email
                )
                SendContactRequestButton(newContactEmail)
                //StandardIconButton({sendContactRequest(newContactEmail)}, Icons.Filled.Send)
            }
        }
    }

    @Composable
    fun SendContactRequestButton(newContactEmail: String) {
        val coroutineScope = rememberCoroutineScope()

        StandardIconButton(
            {
                coroutineScope.launch {
                    sendContactRequest(newContactEmail)
                }
            },
            icon = Icons.Filled.Send
        )
    }

    // Devuelve el Id del usuario que posee dicho email en sus datos.
    suspend fun getIdByEmail(email : String): String {
        val db  = Firebase.firestore
        var id  = ""

        try {
            val querySnapshot = db.collection("usersData").whereEqualTo("email", email).get().await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                id = document.getString("userId").toString()
                Toast.makeText(
                    baseContext,
                    id,
                    Toast.LENGTH_SHORT,
                ).show()
                return document.getString("userId").toString()
            } else {
                Toast.makeText(
                    baseContext,
                    "There are no users with that email.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        } catch (e: Exception) {
            // Manejar el error
        }
        return id
    }

    // Envía una solicitud de contacto :
    // Agregamos a las solicitudes salientes del usuario
    // El mail del nuevo contacto, uid del nuevo contacto y la hora de la solicitud
    private fun registerOutgoingContactRequest(requesterEmail: String, requesterId: String, requestedId: String) {
        val db                  = Firebase.firestore
        val outgoingRequest = hashMapOf(
            "outgoingContactEmail"  to requesterEmail,
            "date"                  to Timestamp(Date()),
            "outgoingContactUserId" to requestedId,
            "userId"                to requesterId
        )

        db.collection("outgoingRequests")
            .add(outgoingRequest)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(
                    baseContext,
                    "Contact request has been sended.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error sending contact request.", e)
            }
    }

    // Envía una solicitud de contacto :
    // Agregamos a las solicitudes entrantes de un usuario
    // El mail del nuevo contacto, uid del solicitante y la hora de la solicitud
    private fun registerIncomingContactRequest(requestedEmail: String, requesterId: String, requestedId: String) {
        val db                  = Firebase.firestore
        val incomingRequest = hashMapOf(
            "incomingContactEmail"  to requestedEmail,
            "date"                  to Timestamp(Date()),
            "incomingContactUserId" to requesterId,
            "userId"                to requestedId
        )

        db.collection("incomingRequests")
            .add(incomingRequest)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(
                    baseContext,
                    "Contact request has been registered on the other user.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error registering contact request on the other user.", e)
            }
    }

    // Invoca las funciones necesarias para el circuito del envio de solicitudes de contacto
    private suspend fun sendContactRequest(requestedEmail : String) {
        val requesterId     = Firebase.auth.currentUser!!.uid
        val requesterEmail  = Firebase.auth.currentUser!!.email
        val requestedId     = getIdByEmail(requestedEmail)


        registerOutgoingContactRequest(requestedEmail, requesterId, requestedId)
        if (requesterEmail != null) {
            registerIncomingContactRequest(requesterEmail, requesterId, requestedId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    fun ContactsSection() {
        var contacts: List<String> by remember { mutableStateOf(emptyList()) }

        LaunchedEffect(Unit) {
            getContacts { fetchedContacts ->
                contacts = fetchedContacts.sorted()
            }
        }

        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState(), enabled = true)
                .padding(bottom = 80.dp)
        ) {
            Text(text = "Your contacts")
            contacts.forEach { firstName ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Contacts,
                            contentDescription = null,
                            tint = colorResource(id = R.color.ThirdColor)
                        )
                        Text(firstName)
                    }
                }
            }
        }
    }

    private fun getContacts(callback: (List<String>) -> Unit) {
        val currentFirebaseUser = Firebase.auth.currentUser
        val db = Firebase.firestore
        val collectionRef = db.collection("contacts")
        val query = collectionRef.whereEqualTo("userId", currentFirebaseUser!!.uid)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val contacts = mutableListOf<String>()
                for (document in querySnapshot.documents) {
                    val contactsName = document.get("contactsFirstName") as List<String>
                    contacts.addAll(contactsName)
                }
                Toast.makeText(
                    baseContext,
                    "Contactos cargados con exito. Hay ${contacts.size} contactos",
                    Toast.LENGTH_SHORT,
                ).show()
                callback(contacts)
            }
            .addOnFailureListener {
                Toast.makeText(
                    baseContext,
                    "Error while getting your contacts.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Preview(
        showBackground = true,
        widthDp = 393,
        heightDp = 851
    )
    @Composable
    fun GreetingPreview() {
        MyFirstAppTheme {
            ContactsLayer()
        }
    }
}