package com.proferoberto.quienesesepokemon.data.remote

import com.proferoberto.quienesesepokemon.data.remote.dto.PokemonDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {

    @GET("pokemon/{id}")
    suspend fun getPokemonById(
        @Path("id") id: Int
    ): PokemonDto

    // Si quieres otro endpoint (lista, etc) lo agregas después
}