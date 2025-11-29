package com.proferoberto.quienesesepokemon.ui.game

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

@OptIn(ExperimentalMaterial3Api::class)
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