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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Diversity1
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CardDefaults.shape
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

enum class RequestType {
    CONTACTS,
    OUTGOING,
    INCOMING
}
@Suppress("UNUSED_EXPRESSION")
class ContactsActivity : ComponentActivity() {
    private val perfil  = {startActivity(Intent(this, ProfileActivity::class.java))}

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
        var requestType by remember { mutableStateOf(RequestType.CONTACTS) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.white))
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AddNewContactSection()
            ContactsButtonsSection { newRequestType -> requestType = newRequestType }
            ContactsSection(requestType)
        }
    }

    @Composable
    fun ContactsButtonsSection(onRequestTypeChange: (RequestType) -> Unit) {
        Row (
            modifier = Modifier.padding(0.dp, 2.dp, 0.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StandardIconButton(accion = { onRequestTypeChange(RequestType.CONTACTS) }, icon = Icons.Outlined.Diversity1, iconColorTintId = R.color.black, iconBackgroundColorId = R.color.ThirdColor )
            Spacer(modifier = Modifier.width(8.dp))
            StandardIconButton(accion = { onRequestTypeChange(RequestType.INCOMING) }, icon = Icons.Outlined.Notifications, iconColorTintId = R.color.black, iconBackgroundColorId = R.color.ThirdColor )
            Spacer(modifier = Modifier.width(8.dp))
            StandardIconButton(accion = { onRequestTypeChange(RequestType.OUTGOING) }, icon = Icons.Outlined.Explore, iconColorTintId = R.color.black, iconBackgroundColorId = R.color.ThirdColor )
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
                .border(2.dp, colorResource(id = R.color.black), shape)
                .background(color = colorResource(id = R.color.ThirdColor), shape = shape)
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
                Spacer(modifier = Modifier.height(6.dp))
                SendContactRequestButton(newContactEmail)
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
            icon = Icons.Filled.Send,
            iconColorTintId = R.color.SecondColor
        )
    }

    // Devuelve el Id del usuario que posee dicho email en sus datos.
    suspend fun getIdByEmail(email : String): String {
        val db  = Firebase.firestore
        val id  = ""

        try {
            val querySnapshot = db.collection("usersData").whereEqualTo("email", email).get().await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
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
        val requestedId     = getIdByEmail(requestedEmail)
        val requesterId     = Firebase.auth.currentUser!!.uid
        val requesterEmail  = Firebase.auth.currentUser!!.email

        if (requestedId == "" || requesterEmail == null) {
            return
        } else {
            registerOutgoingContactRequest(requestedEmail, requesterId, requestedId)
            registerIncomingContactRequest(requesterEmail, requesterId, requestedId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    fun ContactsSection(requestType: RequestType) {
        var contacts: List<String> by remember { mutableStateOf(emptyList()) }

        LaunchedEffect(requestType) {
            contacts = emptyList()
            when (requestType) {
                RequestType.CONTACTS -> {
                    getContacts { fetchedContacts ->
                        contacts = fetchedContacts.sorted()
                    }
                }

                RequestType.INCOMING -> {
                    getIncomingRequests { fetchedRequests ->
                        contacts = fetchedRequests.sorted()
                    }
                }

                RequestType.OUTGOING -> {
                    getOutgoingRequests { fetchedRequests ->
                        contacts = fetchedRequests.sorted()
                    }
                }
            }

        }

        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState(), enabled = true)
                .padding(bottom = 80.dp)
        ) {
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
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(firstName)

                        // Mostramos distintos botones en función de lo que mostramos
                        when (requestType) {
                            RequestType.INCOMING -> {
                                StandardIconButton(accion = {  }, icon = Icons.Outlined.Done, iconColorTintId = R.color.white, iconBackgroundColorId = R.color.FourthColor )
                                Spacer(modifier = Modifier.width(8.dp))
                                StandardIconButton(accion = { }, icon = Icons.Outlined.Block, iconColorTintId = R.color.white, iconBackgroundColorId = R.color.red )
                            }

                            RequestType.OUTGOING -> {
                                StandardIconButton(accion = { }, icon = Icons.Outlined.Cancel, iconColorTintId = R.color.white, iconBackgroundColorId = R.color.orange )
                            }

                            else -> null
                        }
                    }
                }
            }
        }
    }

    private fun getOutgoingRequests(callback: (List<String>) -> Unit) {
        val currentFirebaseUser = Firebase.auth.currentUser
        val db = Firebase.firestore
        val collectionRef = db.collection("outgoingRequests")
        val query = collectionRef.whereEqualTo("userId", currentFirebaseUser!!.uid)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val contacts = mutableListOf<String>()
                for (document in querySnapshot.documents) {
                    val contactName = document.get("outgoingContactEmail") as String
                    contacts.add(contactName)
                }
                Toast.makeText(
                    baseContext,
                    "Outgoing requests succesfully loaded. You have ${contacts.size} requests",
                    Toast.LENGTH_SHORT,
                ).show()
                callback(contacts)
            }
            .addOnFailureListener {
                Toast.makeText(
                    baseContext,
                    "Error while getting your outgoing requests.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
    }

    private fun getIncomingRequests(callback: (List<String>) -> Unit) {
        val currentFirebaseUser = Firebase.auth.currentUser
        val db = Firebase.firestore
        val collectionRef = db.collection("incomingRequests")
        val query = collectionRef.whereEqualTo("userId", currentFirebaseUser!!.uid)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val contacts = mutableListOf<String>()
                for (document in querySnapshot.documents) {
                    val contactName = document.get("incomingContactEmail") as String
                    contacts.add(contactName)
                }
                Toast.makeText(
                    baseContext,
                    "Incomming requests succesfully loaded. You have ${contacts.size} requests",
                    Toast.LENGTH_SHORT,
                ).show()
                callback(contacts)
            }
            .addOnFailureListener {
                Toast.makeText(
                    baseContext,
                    "Error while getting your incoming requests.",
                    Toast.LENGTH_SHORT,
                ).show()
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