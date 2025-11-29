package com.proferoberto.quienesesepokemon.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proferoberto.quienesesepokemon.data.remote.dto.Pokemon
import com.proferoberto.quienesesepokemon.data.repository.PokemonRepository
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