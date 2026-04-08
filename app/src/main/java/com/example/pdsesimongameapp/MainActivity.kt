package com.example.pdsesimongameapp

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {

    //devo controllare quando l'input della griglia mi serve (perche l'utente deve inserire la sequenza)
    //o non mi serve perchè devo mostrare la sequenza all'utente o perché sono in fine partita
    var isInputAbilitato = false
    var stringaInput = ""



    //Controllo per input utente
    fun isInputGrigliaAbilitato() : Boolean {
       return isInputAbilitato
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val cancellaB = findViewById<Button>(R.id.cancellaB)
        val finePartitaB = findViewById<Button>(R.id.finePartitaB)
        val outputTV = findViewById<TextView>(R.id.outputTV)

        cancellaB.setOnClickListener {
            //contenuto area di testo si azzera
            //sequenza in corso azzerata? intende la sequenza inserita
            outputTV.text = ""
            stringaInput = ""
        }

        finePartitaB.setOnClickListener {
            //isInputAbilitato = false
            //chiamata a seconda schermata

        }

val redV = findViewById<View>(R.id.redV)
redV.setOnClickListener{
     if (isInputAbilitato){
          stringaInput += "R"
     }
}
val greenV = findViewById<View>(R.id.greenV)
redV.setOnClickListener{
     if (isInputAbilitato){
          stringaInput += "G"
     }
}
val yellowV = findViewById<View>(R.id.yellowV)
redV.setOnClickListener{
     if (isInputAbilitato){
          stringaInput += "Y"
     }
}
val blueV = findViewById<View>(R.id.blueV)
blueV.setOnClickListener{
     if (isInputAbilitato){
          stringaInput += "B"
     }
}
val magentaV = findViewById<View>(R.id.magentaV)
redV.setOnClickListener{
     if (isInputAbilitato){
          stringaInput += "M"
     }
}
val cyanV = findViewById<View>(R.id.cyanV)
redV.setOnClickListener{
     if (isInputAbilitato){
          stringaInput += "C"
     }
}
        //solo per debug adesso
        isInputAbilitato = true



    }
}