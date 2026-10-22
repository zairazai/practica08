package com.example.practica08.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.practica08.data.Usuario
import com.example.practica08.domain.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

@Composable
fun MainScreen() {

    // Obtenemos el contexto de nuestra aplicación
    val context = LocalContext.current

    // Obtenemos la instancia de la base de datos
    val db = remember {
        AppDatabase.getDatabase(context)
    }

    // Obtenemos nuestro DAO para trabajar con los usuarios
    val dao = db.usuarioDao()

    // Scope que nos permite ejecutar corrutinas
    val coroutineScope = rememberCoroutineScope()

    // Convertimos el Flow de Room en un State de Compose.
    // Cuando cambien los usuarios, la pantalla se actualizará.
    val usuarios by dao.obtenerTodos()
        .collectAsState(initial = emptyList())

    // Guarda temporalmente lo escrito en el TextField
    var nuevoNombre by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 32.dp)
    ) {

        OutlinedTextField(
            value = nuevoNombre,
            onValueChange = { nuevoNombre = it },
            label = { Text("Nombre del usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (nuevoNombre.isNotBlank()) {

                    coroutineScope.launch(Dispatchers.IO) {

                        // Guardamos el usuario en Room
                        dao.insertar(
                            Usuario(nombre = nuevoNombre)
                        )

                        // Regresamos al hilo principal para modificar la UI
                        withContext(Dispatchers.Main) {
                            nuevoNombre = ""
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Usuario")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lista de Usuarios Registrados:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(usuarios) { usuario ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // RETO:
                    // Usamos Row para colocar los datos y
                    // el botón de eliminar en la misma fila.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "ID: ${usuario.id} | Nombre: ${usuario.nombre}"
                        )

                        // RETO:
                        // Botón con icono de papelera para eliminar el usuario.
                        IconButton(
                            onClick = {

                                // Ejecutamos la eliminación dentro de una corrutina.
                                coroutineScope.launch(Dispatchers.IO) {
                                    dao.eliminar(usuario)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar usuario"
                            )
                        }
                    }
                }
            }
        }
    }
}
