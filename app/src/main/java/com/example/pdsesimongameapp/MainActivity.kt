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
    fun puoInserire() : Boolean {
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

        val pulsantiInput = listOf(
            findViewById<Button>(R.id.redB),
            findViewById<Button>(R.id.blueB),
            findViewById<Button>(R.id.greenB),
            findViewById<Button>(R.id.yellowB),
            findViewById<Button>(R.id.magentaB),
            findViewById<Button>(R.id.cyanB)
        )


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

        pulsantiInput.forEach { button -> button.setOnClickListener {
            //ogni bottone della griglia (se siamo in fase di inserimento sequenza)
            //deve prendere il primo carattere della stringa contenuta nel suo text e inserirla nella stringa di input
            //e nel valore text di outputTV
            val stringaB = button.text.toString()
            if (puoInserire()){
                stringaInput += stringaB[0]
                outputTV.text = stringaInput
            }
        } }

        //solo per debug adesso
        isInputAbilitato = true



    }
}