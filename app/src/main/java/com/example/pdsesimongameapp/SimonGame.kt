package com.example.pdsesimongameapp

import android.widget.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


//Come funziona una partita?
//Viene data una sequenza

//Il dispositivo crea una serie di toni e luci e richiede all'utente di ripetere la sequenza.
//Se l'utente ha successo, la serie diventa progressivamente più lunga e complessa.
//Quando l'utente fallisce o il limite di tempo scade, il gioco finisce.

class SimonGame {

    var sequenzaCorrente = ""
    var difficoltaSequenza = 1 //Lunghezza sequenza (Minimo 1)


    fun resetPartita(){
        sequenzaCorrente = ""
        difficoltaSequenza = 1
    }

    //cio nuovo livello +1 su lunghezza sequenza
    fun aumentaDifficolta(){
        difficoltaSequenza++
        //la sequenza rimane uguale e genera un nuovo carattere
    }

    fun generaCarattere(){

    }

    fun evidenziaButton(button: Button, scope : CoroutineScope, alpha : Float = 0.4f, durataMs: Long = 150L){
        scope.launch(Dispatchers.Main){
            //abbassa alpha
            button.alpha = alpha
            delay(durataMs)
            //ripristina dopo tot tempo
            button.alpha = 1f
        }
    }

}