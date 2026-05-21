package com.example.pdsesimongameapp

object RegistroPartite {

    private val _listaPartite = mutableListOf<SimonGame.SimonGameData>()
    val listaPartite : List<SimonGame.SimonGameData> get() = _listaPartite

    fun addPartita (partita: SimonGame.SimonGameData)  {
        _listaPartite.add(partita)
    }

}