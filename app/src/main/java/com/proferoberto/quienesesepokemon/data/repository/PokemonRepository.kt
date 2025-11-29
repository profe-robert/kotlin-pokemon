package com.proferoberto.quienesesepokemon.data.repository

import com.proferoberto.quienesesepokemon.data.remote.PokeApiClient
import com.proferoberto.quienesesepokemon.data.remote.dto.Pokemon
import com.proferoberto.quienesesepokemon.data.remote.dto.toPokemon

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

    // Podrías agregar lógica de random aquí (ej: random id)
}