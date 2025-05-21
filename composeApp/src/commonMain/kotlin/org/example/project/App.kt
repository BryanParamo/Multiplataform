@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    io.kamel.core.ExperimentalKamelApi::class
)
package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.clickable
import androidx.compose.runtime.remember

@Composable
fun App() {
    // Estados
    var page by remember { mutableStateOf(1) }
    var characters by remember { mutableStateOf<List<Character>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<Character?>(null) }


    // Carga cada que cambien page o query
    LaunchedEffect(page, query) {
        isLoading = true
        errorMsg   = null

        try {
            // ① Toda tu lógica de red:
            val nuevos = if (query.isBlank()) {
                ApiClient.getCharacters(page)
            } else {
                ApiClient.getCharactersByName(query, page)
            }
            characters = if (page == 1) nuevos else characters + nuevos

        } catch (e: Exception) {
            //errorMsg = "Error cargando datos: ${e.localizedMessage}"
        } finally {
            // ③ Y el finally cierra el bloque
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // Campo de búsqueda
                    TextField(
                        value = query,
                        onValueChange = {
                            // al cambiar la búsqueda, reiniciamos página y lista
                            query = it
                            page = 1
                        },
                        placeholder = { Text("Buscar…") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                // Estado inicial de carga
                isLoading && characters.isEmpty() -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                // Error sin datos
                errorMsg != null && characters.isEmpty() -> {
                    Text(
                        text = errorMsg!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                // Lista con paginación
                else -> {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(characters) { char ->
                            CharacterCard(char) { clicked ->
                                selected = clicked
                            }
                        }
                        item {
                            // Pie de lista: indicador o botón "Cargar más"
                            if (isLoading) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                Button(
                                    onClick = { page++ },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text("Cargar más")
                                }
                            }
                        }
                    }
                    if (selected != null) {
                        val c = selected!!
                        AlertDialog(
                            onDismissRequest = { selected = null },
                            title = { Text(c.name) },
                            text = {
                                Column {
                                    KamelImage(
                                        resource = asyncPainterResource(data = c.image),
                                        contentDescription = c.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text("Status: ${c.status}")
                                    Text("Species: ${c.species}")
                                    Text("Gender: ${c.gender}")
                                    Text("Origin: ${c.origin.name}")
                                    Text("Location: ${c.location.name}")
                                    Text("Episodes: ${c.episode.size}")
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { selected = null }) {
                                    Text("Cerrar")
                                }
                            }
                        )
                    }

                }
            }
        }
    }
}

@OptIn(io.kamel.core.ExperimentalKamelApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CharacterCard(char: Character, onClick: (Character)->Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(char) },    // ← aquí
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val painter = asyncPainterResource(data = char.image)
            KamelImage(
                resource = painter,
                contentDescription = char.name,
                modifier = Modifier.size(60.dp)
            )
            Column(Modifier.weight(1f)) {
                Text(char.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${char.species} • ${char.status}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
