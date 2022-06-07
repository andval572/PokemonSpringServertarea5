package com.pokemon.server

import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Usuario(@Id var nombre: String, var pass: String) {
    val token = nombre + pass
    var pokemonFavoritoId : Int? = null

    @ElementCollection
    var pokemonCapturados = mutableListOf<Int>()

}