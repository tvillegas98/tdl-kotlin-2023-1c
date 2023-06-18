package com.example.myfirstapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.ui.StandardNavigationAppBar
import com.example.myfirstapp.ui.StandardTopAppBar
import com.example.myfirstapp.ui.obtenerDocumentos
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import me.saket.cascade.CascadeDropdownMenu
import me.saket.cascade.rememberCascadeState
import java.text.SimpleDateFormat
import java.util.Locale

class HistorialGastosActivity : ComponentActivity() {
    private val home = {startActivity(Intent(this, HomeActivity::class.java))}
    private val perfil = {startActivity(Intent(this, ProfileActivity::class.java))}
    private val presupuestos = {startActivity(Intent(this, PresupuestosActivity::class.java))}
    private val historialGastos = {startActivity(Intent(this, HistorialGastosActivity::class.java))}

    private var filtroCategoria : String? = null
    private var filtroFuente : String? = null
    private var busquedaTitulo : String? = null
    private var ordenarPor : String? = null
    private var ordenAscendente : Boolean = false

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filtroCategoria = intent.getStringExtra("categoria")
        filtroFuente = intent.getStringExtra("fuente")
        busquedaTitulo = intent.getStringExtra("titulo")
        ordenarPor = intent.getStringExtra("orderBy")
        ordenAscendente = intent.getBooleanExtra("ascOrder", false)


        setContent {
            MyFirstAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = colorResource(id = R.color.PrimaryColor)
                ) {
                    Scaffold(
                        topBar = {
                            StandardTopAppBar(onBackClick=home)
                        },
                        bottomBar = { StandardNavigationAppBar(
                            home=home,
                            perfil = perfil,
                            presupuestos = presupuestos )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        HistorialDeGastos()
                    }
                }
            }
        }
    }
    @Composable
    fun HistorialDeGastos() {
        val currentFirebaseUser = Firebase.auth.currentUser
        val db = Firebase.firestore
        val gastos = remember { mutableStateOf(emptyList<List<String>>()) }

        LaunchedEffect(Unit) {
            var collectionRef = db.collection("gastos")
                .whereEqualTo("userUID", currentFirebaseUser!!.uid)

            // Si la categoria no es nula, la filtro
            if (filtroCategoria != null) {
                collectionRef = collectionRef.whereEqualTo("category", filtroCategoria)
            }
            // Si la fuente no es nula, la filtro
            if (filtroFuente != null) {
                collectionRef = collectionRef.whereEqualTo("source", filtroFuente)
            }
            if (busquedaTitulo != null) {
                // Simula un like pero solo si el texto empieza con dicha palabra, no hay algo mejor que se pueda hacer con firebase
                collectionRef = collectionRef.whereGreaterThanOrEqualTo("title", busquedaTitulo!!).whereLessThanOrEqualTo("title",busquedaTitulo+'\uf8ff')
            }
            if (ordenarPor != null) {
                //TODO VER POR QUE NO FUNCIONA COMO DEBERIA
                collectionRef = collectionRef.orderBy(ordenarPor!!, if(ordenAscendente) Query.Direction.ASCENDING else Query.Direction.DESCENDING)
            }

            collectionRef.get()
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
                .addOnFailureListener {
                    Toast.makeText(
                        baseContext,
                        "ERROR",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
        }

        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState(), enabled = true)
                .padding(top = 55.dp, bottom = 80.dp)
                .background(color = colorResource(id = R.color.PrimaryColor)),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Filtros()
            ListarHistorial(gastos)
        }
    }

    @Composable
    private fun Filtros() {
        Row (
            modifier= Modifier
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.PrimaryColor))
                .padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconFiltros()
            IconOrden()
            BarraBusqueda()
        }

    }

    @Composable
    private fun BarraBusqueda() {
        var busqueda:      String by remember {mutableStateOf("")}

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar Titulo") },
            modifier = Modifier.background(color = colorResource(id = R.color.white)),
            trailingIcon = {
                IconButton(
                    onClick = { buscarTitulo(busqueda) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.Gray
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search // Define el tipo de acción del botón "Enter"
            ),
            keyboardActions = KeyboardActions(
                onSearch = { buscarTitulo(busqueda) }
            )
        )
    }

    @Composable
    private fun IconFiltros() {
        val filtrosLista : MutableList<String> = mutableListOf("Categoria","Fuente")
        var expanded by remember { mutableStateOf(false) }

        val fuentes : MutableList<String> = mutableListOf()
        val categorias : MutableList<String> = mutableListOf()
        obtenerDocumentos(nombreColeccion = "sources", lista=fuentes)
        obtenerDocumentos(nombreColeccion = "categories", lista=categorias)

        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.FilterAlt,
                contentDescription = "Icono de filtro",
                modifier = Modifier.size(35.dp)
            )
        }

        val state = rememberCascadeState()
        CascadeDropdownMenu(
            state = state,
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Si hay algun filtro actualmente agrego una opcion para eliminarlos
            if (filtroFuente != null || filtroCategoria != null) {
                DropdownMenuItem(
                    text = { Text(text = "Borrar Filtros") },
                    onClick = historialGastos
                )
            }

            filtrosLista.forEach { filtro ->
                DropdownMenuItem(
                    text = { Text(text = filtro) },
                    children = {
                        if (filtro == "Fuente") {
                            fuentes.forEach { subFiltro ->
                                DropdownMenuItem(
                                    text = { Text(text = subFiltro) },
                                    onClick = aplicarFiltro(filtro, subFiltro)
                                )
                            }
                        } else {
                            categorias.forEach { subFiltro ->
                                DropdownMenuItem(
                                    text = { Text(text = subFiltro) },
                                    onClick = aplicarFiltro(filtro, subFiltro)
                                )
                            }
                        }
                    }
                )
            }
        }
    }
    @Composable
    private fun IconOrden() {
        var expanded by remember { mutableStateOf(false) }

        // TODO VER POR QUE NO SE PUEDE HACER DINAMICO
//        val campos : List<String> = obtenerCamposGastos()

        val campos : Map<String, String> =
            mapOf(Pair("Titulo", "title"), Pair("Categoria","category"),
                Pair("Fecha","date"), Pair("Fuente","source"), Pair("Monto","amount"))

        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.FilterList,
                contentDescription = "Icono de filtro",
                modifier = Modifier.size(35.dp)
            )
        }

        val state = rememberCascadeState()
        CascadeDropdownMenu(
            state = state,
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (ordenarPor != null) {
                DropdownMenuItem(
                    text = { Text(text = "Borrar Orden") },
                    onClick = historialGastos
                )
            }
            campos.forEach { (campoTraducion, campo) ->
                DropdownMenuItem(
                    text = { Text(text = campoTraducion) },
                    children = {
                        DropdownMenuItem(
                            text = { Text(text = "Ascendente") },
                            onClick = aplicarOrden(campo=campo,ascendente = true)
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Descendente") },
                            onClick = aplicarOrden(campo=campo, ascendente = false)
                        )
                    }
                )
            }
        }
    }

    private fun aplicarOrden(campo: String, ascendente: Boolean): () -> Unit {
        val intent = Intent(this, HistorialGastosActivity::class.java)

        intent.putExtra("orderBy", campo)
        intent.putExtra("orderType", ascendente)
        if (filtroFuente != null) {
            intent.putExtra("fuente", filtroFuente)
        }
        if (filtroCategoria != null) {
            intent.putExtra("categoria", filtroCategoria)
        }
        return {startActivity(intent)}
    }

//    private fun obtenerCamposGastos(): List<String> {
//        val db = Firebase.firestore
//        val collectionRef = db.collection("gastos")
//        val res : MutableList<String> = mutableListOf()
//
//
//        collectionRef.get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    if (document.id != "Observaciones"){
//                        res.add(document.id)
//                    }
//                }
//            }
//            .addOnFailureListener { exception ->
//                println("Error al obtener los documentos: $exception")
//            }
//        return res
//    }

    private fun aplicarFiltro(tipoFiltro: String, subFiltro: String): () -> Unit  {
        val intent = Intent(this, HistorialGastosActivity::class.java)
        when (tipoFiltro) {
            "Categoria" -> {
                intent.putExtra("categoria", subFiltro)
                if (filtroFuente != null) {
                    intent.putExtra("fuente", filtroFuente)
                }
            }
            "Fuente" -> {
                intent.putExtra("fuente", subFiltro)
                if (filtroCategoria != null) {
                    intent.putExtra("categoria", filtroCategoria)
                }
            }
        }
        return {startActivity(intent)}
    }

    private fun buscarTitulo(busqueda: String) {
        val intent = Intent(this, HistorialGastosActivity::class.java)
        if (filtroCategoria != null) {
            intent.putExtra("categoria", filtroCategoria)
        }
        if (filtroFuente != null) {
            intent.putExtra("fuente", filtroFuente)
        }
        intent.putExtra("titulo", busqueda)
        return startActivity(intent)
    }

    @Composable
    private fun ListarHistorial(gastos: MutableState<List<List<String>>>) {
        if (gastos.value.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp)
            ) {
                Text(text = "No hay gastos registrados")
            }
        } else {
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
}