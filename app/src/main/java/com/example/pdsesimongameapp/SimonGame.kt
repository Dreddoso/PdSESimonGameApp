package com.example.pdsesimongameapp

class SimonGame(private val caratteri : List<Char> = listOf('Y','R','G','B','M','C')) {

     data class SimonGameData(
        val maxLunghezzaSequenzaCorretta : Int = 0,
        val sequenza : String = "",
        val indexFirstWrongChar : Int = 0
    )

    fun generaCarattere(): Char {
        return caratteri.random()
    }

    fun controllaCarattere(c: Char, index : Int, sequenza: String) : Boolean {
        return index in sequenza.indices && c == sequenza[index]
    }

}