package com.example.pdsesimongameapp

object RegistroPartite {
    var countPartite : Int = 0
    val listaPartite = mutableListOf<Partita>()
    //Partita = ( countRect, sequenza) es = ( 3, RGB)
    fun addPartita (count : Int, sequenza : String)  {
        val partita = Partita(countPartite,count, sequenza)
        listaPartite.add(partita)
        countPartite++
    }

}