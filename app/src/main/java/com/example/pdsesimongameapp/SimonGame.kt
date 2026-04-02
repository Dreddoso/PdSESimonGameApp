package com.example.pdsesimongameapp

import android.widget.Button

class SimonGame {
    var mappaSimonColors : Map<Chat,R.color> = {
         (G,R.color.Green)
         (R,...
    }

    fun getSimonColor(btn : Button){
      //switch case
      val char = btn.text.toString()[0]
      if (mappaSimonColors.hasKey(char)){
          return char
      }else return ""
    }

    val maxElementiSequenza = 10
    var numElementiSequenzaCorrente = 0
    var sequenzaCorrenteDaRiprodurre = ""
    //funzione per illuminare un bottone
    fun illuminaPulsante(button: Button){
        button.alpha = 0.5f
        button.postDelayed({
            button.alpha = 1f
        }, 300)
    }
    fun impostaElementoSequenza(button: Button){
        //dato un pulsante quando imposto la sequenza da riprodurre
        //illumino l'elemento e salvo il primo carattere nella stringa
        if(numElementiSequenzaCorrente >= maxElementiSequenza){
            return
        }
        numElementiSequenzaCorrente++
        illuminaPulsante(button)
        sequenzaCorrenteDaRiprodurre += button.text.toString()[0]
    }
    fun impostaSequenza(mappaPulsanti : Map<Char, Button>, livello : Int) {
        //imposta e visualizza una sequenza di pulsanti da riprodurre
        //per tot volte quante il livello fino a 10
        val lunghezzaSequenza = if(livello > 10) 10 else livello
        for(i in 1..lunghezzaSequenza){
            impostaElementoSequenza(mappaPulsanti.values.random())
        }
    }

    fun resettaSequenza(){
        //salva dati partita
        numElementiSequenzaCorrente = 0
        sequenzaCorrenteDaRiprodurre = ""
    }

    fun creaSequenza(){
       
    }

    fun startGame(){
        resettaSequenza()
        creaSequenza()
    }
}