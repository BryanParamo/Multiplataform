package org.example.project

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CharacterResponse(
    val info: PageInfo,
    val results: List<Character>
)

@Serializable
data class PageInfo(
    val count: Int,
    val pages: Int,
    val next: String? = null,
    val prev: String? = null
)

@Serializable
data class Origin(val name: String, val url: String)

@Serializable
data class Location(val name: String, val url: String)

@Serializable
data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: Origin,
    val location: Location,
    val image: String,
    val episode: List<String>
)


// 1.2. Cliente HTTP
object ApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getCharacters(page: Int = 1): List<Character> {
        val url = "https://rickandmortyapi.com/api/character?page=$page"
        val response: CharacterResponse = client.get(url).body()
        return response.results
    }

    suspend fun getCharactersByName(name: String, page: Int): List<Character> =
        client.get("https://rickandmortyapi.com/api/character?name=$name&page=$page").body<CharacterResponse>().results

}


