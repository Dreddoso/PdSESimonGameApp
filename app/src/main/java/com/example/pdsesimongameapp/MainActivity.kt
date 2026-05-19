package com.example.pdsesimongameapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**TODO: controllare se bestscore scelto va bene => Max sequenza dell'ultimo turno o max sequenza dell'intera partita?
 *       Perche se fosse max sequenza dell'intera partita in qualsiasi momento fa un errore la sequenza piu lunga è sempre quella del turno precedente
 *      in quanto anche se sbaglia nel turno corrente l'ultimo carattere,la sequenza piu lunga sara pari alla difficolta del turno precedente
 *       ovvero la difficolta della sequenza corrente -1 (quindi dovrebbe essere corretto cosi
 **/


//REFACTORING AL POSTO DI BOOLEAN SPARSI E CHE SOVRAPPONGONO LOGICA un enum STATOPARTITA (tipo state machine)
enum class StatoPartita{
    IDLE, //nessuna partita iniziata
    TURNO_COMPUTER, //computer mostra la sequenza
    TURNO_PLAYER, //giocatore inserisce input
    PAUSA, //pausa durante turno computer
    FINE_TURNO, //turn player finito (stato intermedio tra turno player e turno computer
    GAME_OVER //errore del giocatore
}

class MainActivity : AppCompatActivity() {
    //Componenti UI
    lateinit var griglia : Map<Char, TextView>
    //val outputTV : TextView = findViewById(R.id.outputTV)
    //val finePartitaB : Button = findViewById(R.id.finePartitaB)
    //val pausaB : Button = findViewById(R.id.pausaB)
    //val avviaB : Button = findViewById(R.id.avviaB)

    //JOB per gestire la pausa durante visualizzazione della sequenza
    var turnoJob : Job? = null

    //STATO DELLA PARTITA
    var statoPartita = StatoPartita.IDLE
        set(value) {  //In questo modo ogni cambio di stato aggiorna automaticamente la UI
            field = value
            aggiornaUIStato()
        }

    //Input
    var stringaInput = ""
    //Contatori del turno
    var countRettangoliPremutiTurno = 0
    var countRettangoliPremutiCorrettamente = 0
    var countRettangoliPremutiPartita = 0
    //Cache per ultimo turno
    var precContatoreRettangoliCorretti = 0

    //Istanza del Gioco
    val simonGame : SimonGame = SimonGame()

    fun aggiungiInput(carattere: Char, outputTextView: TextView){
        if (!isInputGrigliaAbilitato()) return
        countRettangoliPremutiPartita += 1 //incremento sempre

        stringaInput += carattere
        outputTextView.text = stringaInput
        countRettangoliPremutiTurno += 1

        lifecycleScope.launch {
            evidenziaView(griglia[carattere]!!)
        }
        if (!simonGame.controllaCarattere(carattere,countRettangoliPremutiTurno-1)){
            //PARTITA TERMINA CAMBIO FLAG, MESSAGGIO DI ERRORE, DISATTIVO BUTTON
            statoPartita = StatoPartita.GAME_OVER
            Toast.makeText(this, resources.getString(R.string.testo_errore),Toast.LENGTH_SHORT).show()
            return
        }

        //Incremento contatore rettangoli corretti
        countRettangoliPremutiCorrettamente++

        if(stringaInput.length == simonGame.difficoltaSequenza){
            completaTurno()
        }
    }

    fun aggiornaUIStato(){
        val avviaB = findViewById<Button>(R.id.avviaB)
        val pausaB = findViewById<Button>(R.id.pausaB)
        val finePartitaB = findViewById<Button>(R.id.finePartitaB)

        when (statoPartita){
            StatoPartita.IDLE -> {
                avviaB.isEnabled = true
                pausaB.isEnabled = false
                finePartitaB.isEnabled = false
            }

            StatoPartita.TURNO_COMPUTER -> {
                avviaB.isEnabled = false
                pausaB.isEnabled = true
                finePartitaB.isEnabled = true
            }

            StatoPartita.TURNO_PLAYER -> {
                avviaB.isEnabled = false
                pausaB.isEnabled = false
                finePartitaB.isEnabled = true
            }

            StatoPartita.PAUSA -> {
                avviaB.isEnabled = false
                pausaB.isEnabled = true
                finePartitaB.isEnabled = true
            }

            StatoPartita.GAME_OVER -> {
                avviaB.isEnabled = false
                pausaB.isEnabled = false
                finePartitaB.isEnabled = false
            }

            StatoPartita.FINE_TURNO -> {
                avviaB.isEnabled = false
                pausaB.isEnabled = false
                finePartitaB.isEnabled = false
            }

        }
    }

    //Controllo per input utente (utile o rindondante?)
    fun isInputGrigliaAbilitato() : Boolean {
       return statoPartita == StatoPartita.TURNO_PLAYER
    }

    suspend fun evidenziaView(view: View, alpha : Float = 0.4f, durataMs: Long = 150L){
        //abbassa alpha
        view.alpha = alpha
        delay(durataMs)
        //ripristina dopo tot tempo
        view.alpha = 1f
    }

    fun togglePausa(){
        val pausaB = findViewById<Button>(R.id.pausaB)
        if (statoPartita == StatoPartita.PAUSA){
            pausaB.text = getString(R.string.pause)
            statoPartita = StatoPartita.TURNO_COMPUTER
        }else{
            pausaB.text = getString(R.string.resume)
            statoPartita = StatoPartita.PAUSA
        }
    }

    fun completaTurno(){
        //Salva turno precedente
        precContatoreRettangoliCorretti = countRettangoliPremutiCorrettamente
        statoPartita = StatoPartita.FINE_TURNO
        countRettangoliPremutiTurno = 0
        countRettangoliPremutiCorrettamente = 0
        stringaInput = ""
        val output : TextView = findViewById(R.id.outputTV)
        output.text = stringaInput
        //Aumenta difficoltà e avvia nuovo turno
        lifecycleScope.launch {
            delay(500)
            simonGame.aumentaDifficolta()
            iniziaTurno()
        }
    }

    fun finePartita(){
        //Controllo se è primo turno e utente non ha dato input ma è uscito subito
        val primaSequenza = simonGame.difficoltaSequenza == 1 && countRettangoliPremutiTurno == 0
        if(!primaSequenza) {
            //Salva partita
            val salvataggio = simonGame.creaSalvataggioPartitaCorrente(simonGame.sequenzaCorrente,countRettangoliPremutiTurno,simonGame.difficoltaSequenza-1)
            RegistroPartite.addPartita(salvataggio)
        }
        statoPartita = StatoPartita.GAME_OVER
        turnoJob?.cancel()
        resetDatiGioco()
    }

    fun resetDatiGioco(){
        //reset
        stringaInput = ""
        countRettangoliPremutiTurno = 0
        countRettangoliPremutiCorrettamente = 0
        precContatoreRettangoliCorretti = 0
        countRettangoliPremutiPartita = 0
        simonGame.resetPartita()
    }

    fun iniziaTurno(){

        statoPartita = StatoPartita.TURNO_COMPUTER


        turnoJob = lifecycleScope.launch {

            //ALLA fine ne genero uno alla volta, sono quelli da visualizzare che crescono
            simonGame.sequenzaCorrente += simonGame.generaCarattere()

            for(c in simonGame.sequenzaCorrente){

                //se in pausa aspetta
                while(statoPartita == StatoPartita.PAUSA){
                    delay(100)
                }

                val view = griglia[c]!!
                evidenziaView(view = view)
                delay(1000)
            }

            statoPartita = StatoPartita.TURNO_PLAYER
        }
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putString("STRINGA_INPUT", stringaInput)
        outState.putInt("CONTATORE_RECT", countRettangoliPremutiTurno)
        outState.putInt("CONTATORE_CORRECT_RECT",countRettangoliPremutiCorrettamente)
        outState.putInt("CONTATORE_TURNO_PRECEDENTE", precContatoreRettangoliCorretti)
        outState.putString("SEQUENZA_CORRENTE_PARTITA", simonGame.sequenzaCorrente)
        outState.putInt("DIFFICOLTA_CORRENTE_PARTITA", simonGame.difficoltaSequenza)
        outState.putString("STATO_PARTITA",statoPartita.name)
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
            countRettangoliPremutiTurno = savedInstanceState.getInt("CONTATORE_RECT", 0)
            countRettangoliPremutiCorrettamente = savedInstanceState.getInt("CONTATORE_CORRECT_RECT", 0)
            precContatoreRettangoliCorretti = savedInstanceState.getInt("CONTATORE_TURNO_PRECEDENTE",0)
            simonGame.sequenzaCorrente = savedInstanceState.getString("SEQUENZA_CORRENTE_PARTITA","")
            simonGame.difficoltaSequenza = savedInstanceState.getInt("DIFFICOLTA_CORRENTE_PARTITA",simonGame.minDifficoltaSequenza)
            val statoString = savedInstanceState.getString("STATO_PARTITA")
            statoPartita = statoString?.let {
                StatoPartita.valueOf(it)
            } ?: StatoPartita.IDLE
            //aggiorno il testo della textview
            outputTV.text = stringaInput
        }

        avviaB.setOnClickListener {

            //Avvia partita
            iniziaTurno()
        }

        pausaB.setOnClickListener {
            togglePausa()
        }

        finePartitaB.setOnClickListener {
            finePartita()
            //chiamata a seconda schermata
            val intent = Intent(this, Schermata2::class.java)
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this){
            if (statoPartita == StatoPartita.GAME_OVER) {
                //Salva Partita
                val salvataggio = simonGame.creaSalvataggioPartitaCorrente(
                    simonGame.sequenzaCorrente,
                    countRettangoliPremutiTurno,
                    simonGame.difficoltaSequenza - 1
                )
                RegistroPartite.addPartita(salvataggio)
                resetDatiGioco()
            }else{
                //SI Comporta come fine partita
                finePartita()
            }
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