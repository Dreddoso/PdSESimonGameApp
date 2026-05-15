package com.example.pdsesimongameapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    //devo controllare quando l'input della griglia mi serve (perche l'utente deve inserire la sequenza)
    //o non mi serve perchè devo mostrare la sequenza all'utente o perché sono in fine partita
    var isInputAbilitato : Boolean = false
    var partitaInPausa : Boolean = false
    var partitaInCorso : Boolean = false
    //input di una partita = pressione dei rettangoli sulla griglia
    //pulsante di cancella + pulsante fine partita
    var stringaInput = ""
    var countRettangoliPremuti = 0


    fun aggiungiInput(carattere: Char, outputTextView: TextView){
        if (isInputGrigliaAbilitato()){
            stringaInput += carattere
            outputTextView.text = stringaInput
        }
    }

    //Controllo per input utente (utile o rindondante?)
    fun isInputGrigliaAbilitato() : Boolean {
       return isInputAbilitato
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putString("STRINGA_INPUT", stringaInput)
        outState.putBoolean("INPUT_ABILITATO", isInputAbilitato)
        outState.putInt("CONTATORE_RECT", countRettangoliPremuti)
        outState.putBoolean("PARTITA_IN_PAUSA",partitaInPausa)
        outState.putBoolean("PARTITA_IN_CORSO",partitaInCorso)
    }

    //è necessario? o lascio come è stato lasciato dalla partita precedente la ui
    //                  mentre il cervello/gioco dietro è stato resettato
    override fun onResume(){
        super.onResume()
        val outputTV = findViewById<TextView>(R.id.outputTV)
        outputTV.text = stringaInput
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

        val outputTV : TextView = findViewById(R.id.outputTV)
        val finePartitaB : Button = findViewById(R.id.finePartitaB)
        val pausaB : Button = findViewById(R.id.pausaB)
        val avviaB : Button = findViewById(R.id.avviaB)


        //controllo se esiste un stato precedente
        if (savedInstanceState != null) {
            stringaInput = savedInstanceState.getString("STRINGA_INPUT","")
            isInputAbilitato = savedInstanceState.getBoolean("INPUT_ABILITATO",false)
            countRettangoliPremuti = savedInstanceState.getInt("CONTATORE_RECT", 0)
            partitaInPausa = savedInstanceState.getBoolean("PARTITA_IN_PAUSA",false)
            partitaInCorso = savedInstanceState.getBoolean("PARTITA_IN_CORSO", false)
            //aggiorno il testo della textview
            outputTV.text = stringaInput
            //Manca gestione stato del gioco (qui sistema solo il testo del button)
            if (partitaInPausa){
                pausaB.text = resources.getString(R.string.resume)
            }else{
                pausaB.text = resources.getString(R.string.pause)
            }
        }

        avviaB.setOnClickListener {
            partitaInCorso = true
            //Disattiva Button
            avviaB.isEnabled = false
            //Attiva button fine partita
            finePartitaB.isEnabled = true
            //Aggiungere logica avvia partita

        }
        //Nel momento della creazione della schermata il pulsante di Pausa è disattivato
        pausaB.isEnabled = false
        //Attivo solo quando il computer propone
        pausaB.setOnClickListener {
            partitaInPausa = !partitaInPausa //cambio stato
            if (partitaInPausa){
                pausaB.text = resources.getString(R.string.resume)
                //logica per pausa della partita
            }else{
                pausaB.text = resources.getString(R.string.pause)
                //logica per riprendere la partita
            }
        }

        finePartitaB.isEnabled = false //Attivo solo durante una partita
        finePartitaB.setOnClickListener {
            //isInputAbilitato = false
            //salvo partita
            RegistroPartite.addPartita(countRettangoliPremuti, stringaInput)
            //chiamata a seconda schermata
            val intent = Intent(this, Schermata2::class.java)
            startActivity(intent)
            //reset
            stringaInput = ""
            countRettangoliPremuti = 0

        }

        val griglia = mapOf<Char,TextView>(
            'R' to findViewById(R.id.redV),
            'G' to findViewById(R.id.greenV),
            'B' to findViewById(R.id.blueV),
            'M' to findViewById(R.id.magentaV),
            'Y' to findViewById(R.id.yellowV),
            'C' to findViewById(R.id.cyanV)
        )

        for ((char,view) in griglia){
            view.setOnClickListener {
                aggiungiInput(char, outputTV)
            }
        }

    }
}