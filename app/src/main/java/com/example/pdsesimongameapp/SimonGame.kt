package com.example.pdsesimongameapp

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SimonGame(listaColoriChar : List<Char> = listOf('Y','R','G','B','M','C')) {

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

    //TODO: forse ha piu senso metterlo in mainactivity
    fun evidenziaView(view: View, scope : CoroutineScope, alpha : Float = 0.4f, durataMs: Long = 150L){
        scope.launch(Dispatchers.Main){
            //abbassa alpha
            view.alpha = alpha
            delay(durataMs)
            //ripristina dopo tot tempo
            view.alpha = 1f
        }
    }

}