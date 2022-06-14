package com.pokemon.server

import Pokemon
import org.springframework.web.bind.annotation.*

@RestController
class UsuarioController(private val usuarioRepository: UsuarioRepository) {

    // Podemos hacer la request desde el navegador.
    @GetMapping("crearUsuario/{nombre}/{pass}")
    @Synchronized
    fun requestCrearUsuario(@PathVariable nombre: String, @PathVariable pass: String): Any {
        val userOptinal = usuarioRepository.findById(nombre)

        return if (userOptinal.isPresent) {
            val user = userOptinal.get()
            if (user.pass == pass) {
                user
            } else {
                "Contraseña incorrecta"
            }
        } else {
            val user = Usuario(nombre, pass)
            usuarioRepository.save(user)
            user
        }
    }

    /*
    curl --request POST  --header "Content-type:application/json" --data "{\"nombre\":\"u2\", \"pass\":\"p2\"}" localhost:8084/crearUsuario {"nombre":"u2","pass":"p2","token":"u2p2"}
     */
    @PostMapping("crearUsuario")
    @Synchronized
    fun requestCrearUsuarioJson(@RequestBody usuario: Usuario): Any {
        val userOptinal = usuarioRepository.findById(usuario.nombre)

        return if (userOptinal.isPresent) {
            val user = userOptinal.get()
            if (user.pass == usuario.pass) {
                user
            } else {
                "Contraseña incorrecta"
            }
        } else {
            usuarioRepository.save(usuario)
            usuario
        }
    }


    @PostMapping("pokemonFavorito/{token}/{pokemonId}")
    fun guardarPokemonFavorito(@PathVariable token: String, @PathVariable pokemonId: Int): Any {
        println(token)
        usuarioRepository.findAll().forEach { user ->
            if (user.token == token) {
                user.pokemonFavoritoId = pokemonId
                usuarioRepository.save(user)
                return "El usuario ${user.nombre} tiene un nuevo Pokémon favorito"
            }
        }
        return "Token no encontrado"
    }

    @PostMapping("pokemonCapturado/{token}/{pokemonId}")
    fun pokemonCapturado(@PathVariable token: String,@PathVariable pokemonId: Int): String{
        usuarioRepository.findAll().forEach { user ->
            if (user.token == token){
                listaPokemon.listaPokemon.forEach{
                    if (pokemonId.toLong() == it.id){
                        user.pokemonCapturados.add(pokemonId)
                        usuarioRepository.save(user)
                        it.entrenador=user.nombre

                        return "Pokemon guardado"
                    }
                }
                return "el id del pokemon no existe"
            }
        }
        return "Token no encontrado"
    }
    @GetMapping("verPokemonCapturado/{token}")
    fun verPokemonCapturado(@PathVariable token: String): Any {
        var pokemonCapturado = mutableListOf<Pokemon>()
        usuarioRepository.findAll().forEach { user ->
            if (user.token == token){
                user.pokemonCapturados.forEach{ id ->
                    listaPokemon.listaPokemon.forEach{pokemon ->
                            if (id.toLong() == (pokemon.id)){
                                pokemonCapturado.add(pokemon)
                            }
                    }
                }
                return pokemonCapturado

            }

}
    return "Token no encontrado"
}
    @PostMapping("intercambiarPokemon/{tokenUsuario1}/{tokenUsuario2}/{pokemonId1}/{pokemonId2}")
    fun intercambiarPokemon(@PathVariable tokenUsuario1: String,@PathVariable tokenUsuario2: String,@PathVariable pokemonId1: Int,@PathVariable pokemonId2: Int): Any{

        usuarioRepository.findAll().forEach { user1 ->
            if (user1.token == tokenUsuario1){

                usuarioRepository.findAll().forEach { user2 ->
                    if (user2.token == tokenUsuario2){

                        user1.pokemonCapturados.forEach{ idPokemon1->
                            if (idPokemon1 ==pokemonId1){

                                user2.pokemonCapturados.forEach{ idPokemon2->
                                    if (idPokemon2 ==pokemonId2){

                                        user1.pokemonCapturados.remove(idPokemon1)
                                        user1.pokemonCapturados.add(idPokemon2)
                                        usuarioRepository.save(user1)
                                        user2.pokemonCapturados.remove(idPokemon2)
                                        user2.pokemonCapturados.add(idPokemon1)
                                        usuarioRepository.save(user2)
                                        return "Intercambio Realizado"
                                    }
                                }
                                return "El pokemon 2 no existe"
                            }
                        }
                        return "El pokemon 1 no existe"
                    }
                }
                return "El Token 2 no existe"
            }
        }
        return "El Token 1 no existe"
    }
}