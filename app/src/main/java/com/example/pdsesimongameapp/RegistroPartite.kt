package com.example.pdsesimongameapp

object RegistroPartite {
    val listaPartite = mutableListOf<SimonGame.SimonGameData>()
    //Partita = ( countRect, sequenza) es = ( 3, RGB)
    fun addPartita (partita: SimonGame.SimonGameData)  {
        listaPartite.add(partita)
    }

}