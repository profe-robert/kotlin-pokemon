package com.proferoberto.quienesesepokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.proferoberto.quienesesepokemon.ui.game.PokemonGameScreen
import com.proferoberto.quienesesepokemon.ui.theme.QuienEsEsePokemonTheme

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