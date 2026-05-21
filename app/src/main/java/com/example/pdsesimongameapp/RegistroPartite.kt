package com.example.pdsesimongameapp

object RegistroPartite {
    val listaPartite = mutableListOf<SimonGame.SimonGameData>()
    fun addPartita (partita: SimonGame.SimonGameData)  {
        listaPartite.add(partita)
    }

}