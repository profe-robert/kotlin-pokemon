# ¿Quién es ese Pokémon?

Aplicación Android desarrollada en **Kotlin + Jetpack Compose**, que simula el juego clásico “¿Quién es ese Pokémon?”.  
La app consume datos desde **PokeAPI** y utiliza el patrón de diseño **MVVM**.

---

## 🧱 Tecnologías principales

- Kotlin
- Jetpack Compose (UI declarativa)
- AndroidX ViewModel / Lifecycle
- Retrofit + Moshi (consumo de API REST)
- Coroutines
- Coil (carga de imágenes remotas)

---

## 📂 Estructura de carpetas (MVVM)

```text
app/
└── src/
    └── main/
        ├── java/com/example/quienesesepokemon
        │   ├── data
        │   │   ├── remote
        │   │   │   ├── api
        │   │   │   │   └── PokeApiService.kt        # Definición de endpoints de PokeAPI
        │   │   │   └── dto
        │   │   │       ├── PokemonDto.kt           # Modelos que vienen de la API
        │   │   │       └── Mappers.kt              # Conversión DTO -> modelo de dominio
        │   │   └── repository
        │   │       └── PokemonRepository.kt        # Lógica de acceso a datos (API)
        │   │
        │   ├── domain
        │   │   └── model
        │   │       └── Pokemon.kt                  # Modelo de dominio
        │   │
        │   ├── ui
        │   │   ├── game
        │   │   │   ├── PokemonGameState.kt         # Estado de la vista (UI state)
        │   │   │   ├── PokemonGameViewModel.kt     # Lógica de presentación (MVVM)
        │   │   │   └── PokemonGameScreen.kt        # Pantalla principal con Compose
        │   │   └── theme
        │   │       ├── Color.kt
        │   │       ├── Theme.kt
        │   │       └── Type.kt
        │   │
        │   └── MainActivity.kt                     # Punto de entrada, setContent { ... }
        │
        └── AndroidManifest.xml                      # Configuración de la app y permisos
```
## 🛠 Requisitos previos

Android Studio actualizado (versión recomendada: última estable).
JDK 17 (o la versión recomendada por el Android Studio instalado).
Dispositivo físico o emulador Android con API 29 o superior.

## Dependencias

### app/src/main/AndroidManifest.xml
```
<manifest
    xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.proferoberto.quienesesepokemon">

    <!-- 👇 Agrega esta línea -->
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        ...
```

### 🔧 build.gradle
```code
// ViewModel para Compose
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")

// Retrofit
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-moshi:2.11.0")

// Moshi
implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

// Manejo de imágenes remotas
implementation("io.coil-kt:coil-compose:2.6.0")
```

### 🔧 libs.versions.toml
```code
[versions]
agp = "8.5.2"
kotlin = "2.0.21"
coreKtx = "1.13.1"
junit = "4.13.2"
junitVersion = "1.2.1"
espressoCore = "3.6.1"
lifecycleRuntimeKtx = "2.8.4"
activityCompose = "1.9.2"
composeBom = "2024.09.00"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }

junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }

androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }

androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-foundation = { group = "androidx.compose.foundation", name = "foundation" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

## Código del Proyecto


### data/remote/dto/PokemonDto.kt
```code
package com.example.quienesesepokemon.data.remote.dto

import com.squareup.moshi.Json

data class PokemonDto(
    val id: Int,
    val name: String,
    val sprites: SpritesDto
)

data class SpritesDto(
    @Json(name = "front_default")
    val frontDefault: String?,
    val other: OtherSpritesDto?
)

data class OtherSpritesDto(
    @Json(name = "official-artwork")
    val officialArtwork: OfficialArtworkDto?
)

data class OfficialArtworkDto(
    @Json(name = "front_default")
    val frontDefault: String?
)

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String?
)

fun PokemonDto.toPokemon(): Pokemon {
    val officialArtwork = sprites.other?.officialArtwork?.frontDefault
    val fallback = sprites.frontDefault
    return Pokemon(
        id = id,
        name = name,
        imageUrl = officialArtwork ?: fallback
    )
}
```

### data/remote/PokeApiService.kt
```code
package com.example.quienesesepokemon.data.remote

import com.example.quienesesepokemon.data.remote.dto.PokemonDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {

    @GET("pokemon/{id}")
    suspend fun getPokemonById(
        @Path("id") id: Int
    ): PokemonDto

    // Si quieres otro endpoint (lista, etc) lo agregas después
}
```

### data/remote/PokeApiClient.kt
```code
package com.example.quienesesepokemon.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object PokeApiClient {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val api: PokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(PokeApiService::class.java)
    }
}

```

### data/repository/PokemonRepository.kt
```code

import com.example.quienesesepokemon.data.remote.PokeApiClient
import com.example.quienesesepokemon.data.remote.dto.Pokemon
import com.example.quienesesepokemon.data.remote.dto.toPokemon

class PokemonRepository {

    suspend fun getPokemonById(id: Int): Pokemon {
        val dto = PokeApiClient.api.getPokemonById(id)
        return dto.toPokemon()
    }

    suspend fun getPokemonListByIds(ids: List<Int>): List<Pokemon> {
        return ids.map { id ->
            getPokemonById(id)
        }
    }
}
```

### ui/game/PokemonGameState.kt
```code
// ui/game/PokemonGameState.kt
package com.example.quienesesepokemon.ui.game

import com.example.quienesesepokemon.data.remote.dto.Pokemon

data class PokemonGameState(
    val isLoading: Boolean = false,
    val currentPokemon: Pokemon? = null,
    val options: List<String> = emptyList(),
    val selectedOption: String? = null,
    val isAnswerCorrect: Boolean? = null, // null: aún no responde
    val isRevealed: Boolean = false,
    val errorMessage: String? = null
)
```

### ui/game/PokemonGameViewModel.kt
```code
package com.example.quienesesepokemon.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quienesesepokemon.data.remote.dto.Pokemon
import com.example.quienesesepokemon.data.repository.PokemonRepository
import kotlinx.coroutines.launch
import kotlin.random.Random

class PokemonGameViewModel(
    private val repository: PokemonRepository = PokemonRepository()
) : ViewModel() {

    var state = androidx.compose.runtime.mutableStateOf(PokemonGameState())
        private set

    init {
        loadRandomPokemon()
    }

    fun loadRandomPokemon() {
        viewModelScope.launch {
            state.value = state.value.copy(
                isLoading = true,
                errorMessage = null,
                isAnswerCorrect = null,
                selectedOption = null,
                isRevealed = false,
                options = emptyList()
            )

            try {
                // 1) Elegir el Pokémon correcto
                val correctId = Random.nextInt(from = 1, until = 152) // 1ra gen
                val correctPokemon = repository.getPokemonById(correctId)

                // 2) Generar otros 3 ids distintos
                val ids = mutableSetOf(correctId)
                while (ids.size < 4) {
                    val randomId = Random.nextInt(from = 1, until = 152)
                    ids.add(randomId)
                }

                // 3) Obtener la lista de Pokémon (puedes optimizar para evitar traer 2 veces el correcto,
                //   pero para un ejemplo didáctico está bien así)
                val pokemonList = repository.getPokemonListByIds(ids.toList())

                // 4) Armar opciones de nombres
                val options = pokemonList
                    .map { it.name }
                    .shuffled() // mezcla para que no siempre esté en la misma posición

                state.value = state.value.copy(
                    isLoading = false,
                    currentPokemon = correctPokemon,
                    options = options
                )

            } catch (e: Exception) {
                state.value = state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar Pokémon: ${e.message}"
                )
            }
        }
    }

    fun onOptionSelected(option: String) {
        // Si ya se reveló, no hacemos nada
        if (state.value.isRevealed) return

        val correctName = state.value.currentPokemon?.name ?: return

        val isCorrect = correctName.equals(option, ignoreCase = true)

        state.value = state.value.copy(
            selectedOption = option,
            isAnswerCorrect = isCorrect,
            isRevealed = true // aquí revelamos la imagen
        )
    }
}
```

### ui/game/PokemonGameScreen.kt
```code
package com.example.quienesesepokemon.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@Composable
fun PokemonGameScreen(
    viewModel: PokemonGameViewModel = viewModel()
) {
    val state by viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("¿Quién es ese Pokémon?") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // IMAGEN / SILUETA
                    state.currentPokemon?.let { pokemon ->
                        AsyncImage(
                            model = pokemon.imageUrl,
                            contentDescription = pokemon.name,
                            modifier = Modifier
                                .size(220.dp),
                            // Si NO está revelado, aplicar filtro oscuro
                            colorFilter = if (!state.isRevealed) {
                                ColorFilter.tint(
                                    color = Color.Black,
                                    blendMode = BlendMode.SrcAtop
                                )
                            } else {
                                null
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Texto de instrucción
                    Text(
                        text = if (!state.isRevealed) {
                            "¡Adivina quién es este Pokémon!"
                        } else {
                            "Resultado:"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // OPCIONES TIPO QUIZ
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        state.options.forEach { option ->
                            val isSelected = state.selectedOption == option
                            val isCorrectOption =
                                state.currentPokemon?.name?.equals(option, ignoreCase = true) == true

                            val colors = when {
                                // Después de revelar, marcar verde el correcto
                                state.isRevealed && isCorrectOption ->
                                    ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50),
                                        contentColor = Color.White
                                    )

                                // Después de revelar, marcar rojo el seleccionado incorrecto
                                state.isRevealed && isSelected && !isCorrectOption ->
                                    ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF44336),
                                        contentColor = Color.White
                                    )

                                else -> ButtonDefaults.buttonColors()
                            }

                            Button(
                                onClick = { viewModel.onOptionSelected(option) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isRevealed, // no permitir cambiar respuesta después
                                colors = colors
                            ) {
                                Text(option.replaceFirstChar { it.uppercase() })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // MENSAJE DE RESULTADO
                    state.isAnswerCorrect?.let { isCorrect ->
                        val msg = if (isCorrect) {
                            "¡Correcto! Era ${state.currentPokemon?.name?.replaceFirstChar { it.uppercase() }} 🎉"
                        } else {
                            "Incorrecto 😢 Era ${state.currentPokemon?.name?.replaceFirstChar { it.uppercase() }}"
                        }
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    // ERROR
                    state.errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // BOTÓN SIGUIENTE
                    Button(
                        onClick = { viewModel.loadRandomPokemon() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Siguiente Pokémon")
                    }
                }
            }
        }
    }
}

```

### MainActivity.kt
```code
package com.example.quienesesepokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.quienesesepokemon.ui.game.PokemonGameScreen
import com.example.quienesesepokemon.ui.theme.QuienEsEsePokemonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuienEsEsePokemonTheme {
                PokemonGameScreen()
            }
        }
    }
}
```

