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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch

@Composable
fun App() {
    var characters by remember { mutableStateOf<List<Character>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            // Carga la primera página de personajes
            characters = ApiClient.getCharacters(page = 1)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Rick & Morty Characters") })
        }
    ) { padding ->
        CharacterList(characters, Modifier.padding(padding))
    }
}

@Composable
fun CharacterList(characters: List<Character>, modifier: Modifier = Modifier) {
    LazyColumn(modifier.fillMaxSize()) {
        items(characters) { char ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Imagen del personaje
                    val painter = asyncPainterResource(data = char.image)
                    KamelImage(
                        resource = painter,
                        contentDescription = char.name,
                        modifier = Modifier.size(60.dp)
                    )
                    // Nombre y especie
                    Column(Modifier.weight(1f)) {
                        Text(text = char.name, style = MaterialTheme.typography.bodyLarge)
                        Text(text = "${char.species} • ${char.status}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
