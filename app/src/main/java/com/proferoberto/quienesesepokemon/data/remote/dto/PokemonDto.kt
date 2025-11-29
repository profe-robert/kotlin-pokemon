package com.proferoberto.quienesesepokemon.data.remote.dto

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