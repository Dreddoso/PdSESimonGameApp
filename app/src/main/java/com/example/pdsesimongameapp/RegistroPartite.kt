package com.example.pdsesimongameapp

object RegistroPartite {

    private val _listaPartite = mutableListOf<Partita>()
    val listaPartite : List<Partita> get() = _listaPartite

    fun addPartita (partita: Partita)  {
        _listaPartite.add(partita)
    }

}