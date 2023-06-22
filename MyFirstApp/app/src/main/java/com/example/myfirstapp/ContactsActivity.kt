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
import androidx.compose.material.icons.outlined.Delete
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
import com.google.firebase.firestore.FirebaseFirestore
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
// Esta clase se utiliza para guardar los datos de una solicitud
// La misma puede ser del tipo OUTGOING o INCOMING
data class Request(
    val requesterId:    String,
    val requesterEmail: String,
    val requestedId:    String,
    val requestedEmail: String,
    val requestType:    RequestType
)

data class Contact(
    val email:  String,
    val id:     String
)

class ContactsActivity : ComponentActivity() {
    private val perfil  = {startActivity(Intent(this, ProfileActivity::class.java))}
    private val db = Firebase.firestore
    private val currentFirebaseUser                 = Firebase.auth.currentUser
    private val currentFirebaseUserId: String       = currentFirebaseUser!!.uid
    private val currentFirebaseUserEmail: String? = currentFirebaseUser?.email

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
        var requestType by remember { mutableStateOf(RequestType.INCOMING) }
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

    // Invoca las funciones necesarias para el circuito del envio de solicitudes de contacto
    private suspend fun sendContactRequest(requestedEmail : String) {
        val requesterId     = currentFirebaseUserId
        val requesterEmail  = currentFirebaseUserEmail
        val requestedId     = getIdByEmail(requestedEmail)

        if (requestedId == "" || requesterEmail == null) {
            return
        } else {
            registerRequest(
                requesterId,
                requesterEmail,
                requestedId,
                requestedEmail
            )
        }
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

    private fun registerRequest(
        requesterId:    String,
        requesterEmail: String,
        requestedId:    String,
        requestedEmail: String
    ) {
        val request = hashMapOf(
                                "date"                  to Timestamp(Date()),
                                "requesterId"           to requesterId,
                                "requesterEmail"        to requesterEmail,
                                "requestedId"           to requestedId,
                                "requestedEmail"        to requestedEmail
                                )

        db.collection("contactRequests")
            .add(request)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(
                    baseContext,
                    "Request has been sended.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Request Error.", e)
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    fun ContactsSection(requestType: RequestType) {
        // Inicializamos las listas para luego tomar los datos de FB, mostrarlos en pantalla y modificarlos mediante la misma.
        var contacts: List<Contact> by remember { mutableStateOf(emptyList()) }
        var requests: List<Request> by remember { mutableStateOf(emptyList()) }

        LaunchedEffect(requestType) {
            contacts = emptyList()
            requests = emptyList()

            when (requestType) {
                RequestType.CONTACTS -> {
                    getContacts { fetchedContacts ->
                        contacts = fetchedContacts
                    }
                }

                else -> {
                    getRequests(requestType) {fetchedRequests ->
                        requests = fetchedRequests
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState(), enabled = true)
                .padding(bottom = 80.dp)
        ) {
            // En función de lo seleccionado por el usuario, mostramos solicitudes o contactos
            if (requestType == RequestType.CONTACTS) {
                ShowContacts(contacts)
            } else {
                ShowRequests(requests, requestType)
            }
        }
    }

    private fun deleteBothContacts(firstContact: Contact, secondContact: Contact) {
        deleteContact(contactToDelete = secondContact, from = firstContact)
        deleteContact(contactToDelete = firstContact, from = secondContact)
    }

    private fun deleteContact(contactToDelete: Contact, from: Contact) {
        val query = db.collection("contacts").whereEqualTo("userId", from.id)
        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.first()
                    val data = document.data?.toMutableMap()

                    if (data != null) {
                        data.remove(contactToDelete.email)
                        document.reference
                            .set(data)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    baseContext,
                                    "Contact successfully deleted.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    baseContext,
                                    "Error deleting contact: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    baseContext,
                    "Error deleting contact: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    @Composable
    private fun ShowRequests(requests: List<Request>, requestType: RequestType) {
        if (requests.isNotEmpty()) {
            requests.forEach { request ->
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
                        // Mostramos distintos botones en función del tipo de solicitud.
                        when (requestType) {
                            RequestType.INCOMING -> {
                                Text(request.requesterEmail)
                                StandardIconButton(
                                    accion = { acceptRequest(request) },
                                    icon = Icons.Outlined.Done,
                                    iconColorTintId = R.color.white,
                                    iconBackgroundColorId = R.color.FourthColor
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                StandardIconButton(
                                    accion = { deleteRequest(request) },
                                    icon = Icons.Outlined.Block,
                                    iconColorTintId = R.color.white,
                                    iconBackgroundColorId = R.color.red
                                )
                            }

                            RequestType.OUTGOING -> {
                                Text(request.requestedEmail)
                                StandardIconButton(
                                    accion = { deleteRequest(request) },
                                    icon = Icons.Outlined.Cancel,
                                    iconColorTintId = R.color.white,
                                    iconBackgroundColorId = R.color.orange
                                )
                            }

                            else -> {}
                        }
                    }
                }
            }
        } else {
            // Idealmente poner un icono o algo para hacer mas linda la UI.
            Text(text = "No requests pending !.")
        }
    }
    @Composable
    private fun ShowContacts(contacts: List<Contact>) {
        val actualUserContact: Contact? = currentFirebaseUserEmail?.let { Contact(email = it, id = currentFirebaseUserId) }
        // Lo mismo si obtuvimos contactos.
        if (contacts.isNotEmpty()) {
            contacts.forEach { contact ->
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
                        Text(contact.email)
                        StandardIconButton(
                            accion = {
                                if (actualUserContact != null) {
                                    deleteBothContacts(actualUserContact, contact)
                                }
                            },
                            icon = Icons.Outlined.Delete,
                            iconColorTintId = R.color.white,
                            iconBackgroundColorId = R.color.red
                        )
                    }
                }
            }
        } else {
            // Idealmente poner un icono o algo para hacer mas linda la UI.
            Text(text = "You do not have any contact, what are you waiting for ?")
        }
    }

    // Las siguientes funciones manejan las maneras posibles de afectar una solicitud.
    private fun acceptRequest(request: Request) {
        saveContact(request.requesterId, request.requestedId, request.requestedEmail)
        saveContact(request.requestedId, request.requesterId, request.requesterEmail)
        deleteRequest(request)
    }

    private fun saveContact(userId: String, newContactId: String, newContactEmail: String) {
        val contactsCollection = FirebaseFirestore.getInstance().collection("contacts")
        val userContactsRef = contactsCollection.document(userId)
        db.runTransaction { transaction ->
            val userContactsDoc = transaction.get(userContactsRef)

            if (!userContactsDoc.exists()) {
                val newContactMap = mapOf(
                    "userId"        to userId,
                    newContactEmail to newContactId
                )
                transaction.set(userContactsRef, newContactMap)
            } else {
                val existingContacts = userContactsDoc.data?.toMutableMap()

                if (!existingContacts?.containsKey(newContactEmail)!!) {
                    existingContacts[newContactEmail] = newContactId
                    transaction.update(userContactsRef, existingContacts)
                } else {

                }
            }
        }
    }

    private fun deleteRequest(request: Request) {
        val query = db.collection("contactRequests")
            .whereEqualTo("requesterId", request.requesterId)
            .whereEqualTo("requestedId", request.requestedId)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Toast.makeText(
                                baseContext,
                                "Request succesfully deleted.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                baseContext,
                                "Error while deleting request.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    baseContext,
                    "That request does not exist.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
    }

    private fun getRequests(type: RequestType, callback: (List<Request>) -> Unit) {
        val collectionRef = db.collection("contactRequests")

        val query =  when (type) {
            RequestType.INCOMING -> collectionRef.whereEqualTo("requestedId", currentFirebaseUserId)
            RequestType.OUTGOING -> collectionRef.whereEqualTo("requesterId", currentFirebaseUserId)
            else -> null
        }

        query?.get()
            ?.addOnSuccessListener { querySnapshot ->
                val requests = mutableListOf<Request>()
                for (document in querySnapshot.documents) {
                    val request = Request(
                        requesterId     = document.get("requesterId") as String,
                        requesterEmail  = document.get("requesterEmail") as String,
                        requestedId     = document.get("requestedId") as String,
                        requestedEmail  = document.get("requestedEmail") as String,
                        requestType     = type
                    )
                    requests.add(request)
                }
                Toast.makeText(
                    baseContext,
                    "Requests succesfully loaded. You have ${requests.size} requests",
                    Toast.LENGTH_SHORT,
                ).show()
                callback(requests)
            }
            ?.addOnFailureListener {
                Toast.makeText(
                    baseContext,
                    "Error while getting your incoming requests.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
    }

    private fun getContacts(callback: (List<Contact>) -> Unit) {
        val collectionRef = db.collection("contacts")
        val query = collectionRef.whereEqualTo("userId", currentFirebaseUserId)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val contacts = mutableListOf<Contact>()
                val data = querySnapshot.documents.first().data
                if (data != null) {
                    for ((email, id) in data) {
                        if (email != "userId") {
                            contacts.add(
                                Contact(
                                    email   = email,
                                    id      = id as String
                                )
                            )
                        }
                    }
                    Toast.makeText(
                        baseContext,
                        "Contactos cargados con exito. Hay ${contacts.size} contactos",
                        Toast.LENGTH_SHORT,
                    ).show()
                    callback(contacts)
                } else {
                    Toast.makeText(
                        baseContext,
                        "Error",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
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