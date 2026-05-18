package com.example.pdsesimongameapp

class SimonGame(listaColoriChar : List<Char> = listOf('Y','R','G','B','M','C')) {

     data class SimonGameData(
        val maxLunghezzaSequenzaCorretta : Int = 0,
        val sequenza : String = "",
        val indexFirstWrongChar : Int = 0
    )

    val caratteri = listaColoriChar
    var sequenzaCorrente : String = ""
    var difficoltaSequenza : Int = 1 //Lunghezza sequenza (Minimo 1)
    var minDifficoltaSequenza : Int = 1

    fun resetPartita(){
        sequenzaCorrente = ""
        difficoltaSequenza = minDifficoltaSequenza
    }

    //nuovo livello +1 su lunghezza sequenza
    fun aumentaDifficolta(){
        difficoltaSequenza++
        //la sequenza rimane uguale e genera un nuovo carattere
    }

    //RESTITUISCE UN CARATTERE CASUALE
    fun generaCarattere(): Char {
        return caratteri.random()
    }

    fun controllaUltimoCarattere(c : Char) : Boolean{
        if (sequenzaCorrente.isEmpty()) return false
        return c == sequenzaCorrente[sequenzaCorrente.length-1]
    }

    fun controllaCarattere(c: Char, index : Int) : Boolean {
        if(sequenzaCorrente.isEmpty()) return false
        if(index >= sequenzaCorrente.length || index < 0) return false
        return c == sequenzaCorrente[index]
    }

    fun creaSalvataggioPartitaCorrente(sequenzaInput : String = "", indice : Int = 0, bestScore : Int = 0) : SimonGameData {
        return SimonGameData(maxLunghezzaSequenzaCorretta = bestScore, sequenza = sequenzaInput, indexFirstWrongChar = indice)
    }
}