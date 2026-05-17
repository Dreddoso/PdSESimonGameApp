package com.example.pdsesimongameapp

object RegistroPartite {
    var countPartite : Int = 0
    val listaPartite = mutableListOf<SimonGame.SimonGameData>()
    //Partita = ( countRect, sequenza) es = ( 3, RGB)
    fun addPartita (partita: SimonGame.SimonGameData)  {
        listaPartite.add(partita)
        countPartite++
    }

}