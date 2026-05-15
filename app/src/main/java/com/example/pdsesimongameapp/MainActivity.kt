package com.example.pdsesimongameapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    //Componenti UI
    lateinit var griglia : Map<Char, TextView>
    //val outputTV : TextView = findViewById(R.id.outputTV)
    //val finePartitaB : Button = findViewById(R.id.finePartitaB)
    //val pausaB : Button = findViewById(R.id.pausaB)
    //val avviaB : Button = findViewById(R.id.avviaB)

    //FLAG di Stato del Gioco
    var isInputAbilitato : Boolean = false
    var partitaInPausa : Boolean = false
    var partitaInCorso : Boolean = false
    var isGameOver : Boolean = false

    //Input
    var stringaInput = ""
    var countRettangoliPremuti = 0
    var countRettangoliPremutiCorrettamente = 0

    //Cache per ultimo turno
    var precContatoreRettangoliCorretti = 0

    //Istanza del Gioco
    val simonGame : SimonGame = SimonGame()

    fun aggiungiInput(carattere: Char, outputTextView: TextView){
        if (!isInputGrigliaAbilitato()) return

        stringaInput += carattere
        outputTextView.text = stringaInput
        countRettangoliPremuti++

        if (!simonGame.controllaUltimoCarattere(carattere)){
            isGameOver = true
            Toast.makeText(this, resources.getString(R.string.testo_errore),Toast.LENGTH_SHORT).show()
            return
        }

        if(stringaInput.length == simonGame.difficoltaSequenza){
            //Incremento contatore rettangoli corretti
            countRettangoliPremutiCorrettamente++
            completaTurno()
        }
    }

    //Controllo per input utente (utile o rindondante?)
    fun isInputGrigliaAbilitato() : Boolean {
       return isInputAbilitato
    }

    fun togglePausa(){
        val pausaB = findViewById<Button>(R.id.pausaB)
        partitaInPausa = !partitaInPausa
        if (partitaInPausa){
            pausaB.text = resources.getString(R.string.resume)
        }else{
            pausaB.text = resources.getString(R.string.pause)
        }
    }

    fun completaTurno(){
        isInputAbilitato = false
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            simonGame.aumentaDifficolta()
            iniziaTurno()
        }
    }

    fun iniziaTurno(){
        if (countRettangoliPremuti != 0) precContatoreRettangoliCorretti = countRettangoliPremutiCorrettamente //se ho premuto almeno un elemento che sia giusto o sbagliato mi salvo quelli giusti

        //Resetto input
        isInputAbilitato = false
        countRettangoliPremuti = 0
        countRettangoliPremutiCorrettamente = 0

        //Pulsante pausa serve per stoppare (e poi far ripartire) la visione della sequenza
        val pausaB : Button = findViewById(R.id.pausaB)
        pausaB.isEnabled = true

        CoroutineScope(Dispatchers.Main).launch {

            //ALLA fine ne genero uno alla volta, sono quelli da visualizzare che crescono
            simonGame.sequenzaCorrente += simonGame.generaCarattere()

            for(c in simonGame.sequenzaCorrente){
                val view = griglia[c]!!
                simonGame.evidenziaView(view = view, scope = this)
                delay(1000)
            }

            pausaB.isEnabled = false
            isInputAbilitato = true
            stringaInput = ""
            val output : TextView = findViewById(R.id.outputTV)
            output.text = stringaInput
        }
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putString("STRINGA_INPUT", stringaInput)
        outState.putBoolean("INPUT_ABILITATO", isInputAbilitato)
        outState.putInt("CONTATORE_RECT", countRettangoliPremuti)
        outState.putBoolean("PARTITA_IN_PAUSA",partitaInPausa)
        outState.putBoolean("PARTITA_IN_CORSO",partitaInCorso)
        outState.putInt("CONTATORE_CORRECT_RECT",countRettangoliPremutiCorrettamente)
        outState.putInt("CONTATORE_TURNO_PRECEDENTE", precContatoreRettangoliCorretti)
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
            countRettangoliPremutiCorrettamente = savedInstanceState.getInt("CONTATORE_CORRECT_RECT", 0)
            precContatoreRettangoliCorretti = savedInstanceState.getInt("CONTATORE_TURNO_PRECEDENTE",0)
            //aggiorno il testo della textview
            outputTV.text = stringaInput
        }

        avviaB.setOnClickListener {
            partitaInCorso = true
            //Disattiva Button
            avviaB.isEnabled = false
            //Attiva button fine partita
            finePartitaB.isEnabled = true

            //Avvia partita
            iniziaTurno()
        }

        pausaB.setOnClickListener {
            togglePausa()
            //TODO capire come stoppare coroutine e tenere traccia del punto in cui sono nel mostrare la sequenza
        }

        finePartitaB.setOnClickListener {
            //isInputAbilitato = false
            //salvo partita
            RegistroPartite.addPartita(countRettangoliPremutiCorrettamente, stringaInput)
            //chiamata a seconda schermata
            val intent = Intent(this, Schermata2::class.java)
            startActivity(intent)
            //reset
            stringaInput = ""
            countRettangoliPremuti = 0
            countRettangoliPremutiCorrettamente = 0
            precContatoreRettangoliCorretti = 0
            simonGame.resetPartita()
        }

        griglia = mapOf<Char,TextView>(
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

        //TODO giusto disattivare qui buttons finepartita e pausa
        pausaB.isEnabled = false
        finePartitaB.isEnabled = false
    }
}