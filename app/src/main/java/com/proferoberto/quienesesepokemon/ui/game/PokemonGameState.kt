package com.proferoberto.quienesesepokemon.ui.game

import com.proferoberto.quienesesepokemon.data.remote.dto.Pokemon

data class PokemonGameState(
    val isLoading: Boolean = false,
    val currentPokemon: Pokemon? = null,
    val options: List<String> = emptyList(),
    val selectedOption: String? = null,
    val isAnswerCorrect: Boolean? = null, // null: aún no responde
    val isRevealed: Boolean = false,
    val errorMessage: String? = null
)