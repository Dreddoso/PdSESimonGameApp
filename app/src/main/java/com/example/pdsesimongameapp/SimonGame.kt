package com.example.pdsesimongameapp

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

class SimonGame(listaColoriChar : List<Char> = listOf('Y','R','G','B','M','C')) {

    class SimonGameData(
        val maxLunghezzaSequenzaCorretta : Int = 0,
        val sequenza : String = "",
        val indexLastCorrectChar : Int = 0
    ){

        fun getEditString() : SpannableString{
            val spannableString = SpannableString(sequenza)
            spannableString.setSpan(
                ForegroundColorSpan(Color.RED),
                indexLastCorrectChar, //TODO controllare se è corretto partire da questo
                sequenza.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return  spannableString
        }
    }

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
        return SimonGameData(maxLunghezzaSequenzaCorretta = bestScore, sequenza = sequenzaInput, indexLastCorrectChar = indice)
    }
}