package com.example.pdsesimongameapp

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
    GAME_OVER, //errore del giocatore
    FINE_PARTITA //Terminazione volontaria della partita
}

//TODO: refactoring -> spostare la logica per stato e ripristino UI in una viewmodel ??

class MainActivity : AppCompatActivity() {
    //Componenti UI
    lateinit var griglia : Map<Char, TextView>

    //JOB per gestire la pausa durante visualizzazione della sequenza
    var turnoJob : Job? = null
    var riproduzioneIndice = 0
    //STATO DELLA PARTITA
    var statoPartita = StatoPartita.IDLE
        set(value) {  //In questo modo ogni cambio di stato aggiorna automaticamente la UI
            field = value
            if(::griglia.isInitialized){ // :: operatore di riferimento ("puntatore" sicuro)
                aggiornaUIStato()
            }
        }
    //Input
    var stringaInput = ""
    //Contatori del turno
    var countRettangoliPremutiTurno = 0
    var countRettangoliPremutiPartita = 0

    //Istanza del Gioco
    val simonGame : SimonGame = SimonGame()

    fun disattivaInput(){
        for((_,view) in griglia){
            view.isEnabled = false
        }
    }

    fun attivaInput(){
        for((_,view) in griglia){
            view.isEnabled = true
        }
    }

    fun resetDatiGioco(){
        //reset
        stringaInput = ""
        countRettangoliPremutiTurno = 0
        countRettangoliPremutiPartita = 0
        riproduzioneIndice = 0
        simonGame.resetPartita()
    }

    fun ripristinaStatoPartita(){
        turnoJob?.cancel()
        //In base a statoPartita dovrei o non fare niente (Idle -> non aveva ancora cominciato a giocare)
        when(statoPartita){
            StatoPartita.TURNO_COMPUTER -> riprendiTurno()
            StatoPartita.PAUSA -> riprendiTurno()
            StatoPartita.FINE_PARTITA -> disattivaInput()
            else -> aggiornaUIStato()
        }
    }

    fun togglePausa(){
        if (statoPartita != StatoPartita.TURNO_COMPUTER && statoPartita != StatoPartita.PAUSA) return
        val pausaB = findViewById<Button>(R.id.pausaB)
        if (statoPartita == StatoPartita.PAUSA){
            pausaB.text = getString(R.string.pause)
            statoPartita = StatoPartita.TURNO_COMPUTER
        }else{
            pausaB.text = getString(R.string.resume)
            statoPartita = StatoPartita.PAUSA
        }
    }

    fun aggiornaUIStato(){
        val avviaB = findViewById<Button>(R.id.avviaB)
        val pausaB = findViewById<Button>(R.id.pausaB)
        val finePartitaB = findViewById<Button>(R.id.finePartitaB)
        val outputTV = findViewById<TextView>(R.id.outputTV)

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
                pausaB.text = getString(R.string.pause)
                stringaInput = ""
                outputTV.text = stringaInput
            }

            StatoPartita.TURNO_PLAYER -> {
                avviaB.isEnabled = false
                pausaB.isEnabled = false
                finePartitaB.isEnabled = true
                outputTV.text = stringaInput
            }

            StatoPartita.PAUSA -> {
                avviaB.isEnabled = false
                pausaB.isEnabled = true
                finePartitaB.isEnabled = true
                pausaB.text = getString(R.string.resume)
            }

            StatoPartita.GAME_OVER -> {
                avviaB.isEnabled = false
                pausaB.isEnabled = false
                finePartitaB.isEnabled = false
                //Bloccare le visualizzazioni ?
                turnoJob?.cancel()
            }

            StatoPartita.FINE_PARTITA -> {
                avviaB.isEnabled = false
                pausaB.isEnabled = false
                finePartitaB.isEnabled = false
                turnoJob?.cancel()
            }

        }
    }

    suspend fun evidenziaView(view: View, alpha : Float = 0.4f, durataMs: Long = 450L){
        if(!view.isAttachedToWindow) return //Basta questo controllo?
        //abbassa alpha
        view.alpha = alpha
        delay(durataMs)
        //ripristina dopo tot tempo
        view.alpha = 1f
    }

    fun riproduciSequenza(){
        turnoJob?.cancel()

        disattivaInput()

        turnoJob = lifecycleScope.launch {
            delay(700)

            while(riproduzioneIndice < simonGame.sequenzaCorrente.length){
                if(statoPartita == StatoPartita.FINE_PARTITA ||
                    statoPartita == StatoPartita.GAME_OVER){
                    return@launch
                } //NEcessario?
                while (statoPartita == StatoPartita.PAUSA){
                    delay(100)
                }

                val view = griglia[simonGame.sequenzaCorrente[riproduzioneIndice]]!!
                evidenziaView(view = view)
                delay(250)
                riproduzioneIndice += 1
            }
            riproduzioneIndice = 0
            if (statoPartita == StatoPartita.TURNO_COMPUTER) {
                statoPartita = StatoPartita.TURNO_PLAYER
                attivaInput()
            }
        }
    }

    fun iniziaPartita(){
        //Reset
        simonGame.resetPartita()
        resetDatiGioco()
        //Avvio turno
        iniziaNuovoTurno()
    }

    fun iniziaNuovoTurno(){
        statoPartita = StatoPartita.TURNO_COMPUTER
        simonGame.sequenzaCorrente += simonGame.generaCarattere()
        riproduzioneIndice = 0
        riproduciSequenza()
    }

    fun riprendiTurno(){
        statoPartita = StatoPartita.TURNO_COMPUTER
        riproduciSequenza()
    }

    fun aggiungiInput(carattere: Char){
        //Controllo stato partita
        if (statoPartita != StatoPartita.TURNO_PLAYER) return
        countRettangoliPremutiPartita += 1 //incremento sempre
        //Per feedback visivo pure in input
        lifecycleScope.launch {
            evidenziaView(griglia[carattere]!!) //TODO: rischio? !!
        }
        stringaInput += carattere
        findViewById<TextView>(R.id.outputTV).text = stringaInput //Invece di chiamare metodo aggiornaUIStato()
        countRettangoliPremutiTurno += 1
        //Controllo dinamico dell'input
        if (!simonGame.controllaCarattere(carattere,countRettangoliPremutiTurno-1)){
            //ERRORE -> PARTITA TERMINA
            //  CAMBIO FLAG, MESSAGGIO DI ERRORE, DISATTIVO BUTTON
            statoPartita = StatoPartita.GAME_OVER
            disattivaInput()
            salvaPartita()
            Toast.makeText(this, resources.getString(R.string.testo_errore),Toast.LENGTH_SHORT).show()
            return
        }
        //Nessun errore
        //Controllo se ha inserito tutto
        if(stringaInput.length == simonGame.sequenzaCorrente.length){
            completaTurno()
        }
    }

    fun completaTurno(){
        statoPartita = StatoPartita.TURNO_COMPUTER //per bloccare input utente
        turnoJob?.cancel()
        lifecycleScope.launch {
            delay(500)
            //Resetto variabili del turno e sistemo
            countRettangoliPremutiTurno = 0
            //Aumenta difficoltà e avvia nuovo turno
            simonGame.aumentaDifficolta()
            iniziaNuovoTurno()
        }
    }

    fun finePartita(){
        //Se gameover ha gia salvato quando è avvenuto il fatto
        if (statoPartita == StatoPartita.GAME_OVER){
            statoPartita = StatoPartita.FINE_PARTITA
            turnoJob?.cancel()
            resetDatiGioco()
            return
        }

        statoPartita = StatoPartita.FINE_PARTITA
        salvaPartita() //Salva abbandono volontario
        turnoJob?.cancel()
        resetDatiGioco()
    }

    fun salvaPartita(){
        val primaSequenza = simonGame.difficoltaSequenza == 1 && countRettangoliPremutiTurno == 0
        if(primaSequenza){
            return
        }
        val indiceErrore = if(statoPartita == StatoPartita.GAME_OVER){
            (countRettangoliPremutiTurno-1).coerceAtLeast(0)
        }else{
            countRettangoliPremutiTurno
        }
        val salvataggio = simonGame.creaSalvataggioPartita(
            bestScore = simonGame.difficoltaSequenza-1,
            sequenzaInput = simonGame.sequenzaCorrente,
            indiceErrore = indiceErrore
        )
        RegistroPartite.addPartita(salvataggio)
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putString("STRINGA_INPUT", stringaInput)
        outState.putInt("CONTATORE_RECT", countRettangoliPremutiTurno)
        outState.putString("SEQUENZA_CORRENTE_PARTITA", simonGame.sequenzaCorrente)
        outState.putInt("DIFFICOLTA_CORRENTE_PARTITA", simonGame.difficoltaSequenza)
        outState.putString("STATO_PARTITA",statoPartita.name)
        outState.putInt("RIPRODUZIONE_INDICE", riproduzioneIndice)
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

        val finePartitaB : Button = findViewById(R.id.finePartitaB)
        val pausaB : Button = findViewById(R.id.pausaB)
        val avviaB : Button = findViewById(R.id.avviaB)

        avviaB.setOnClickListener {
            //Avvia partita
            iniziaPartita()
        }

        pausaB.setOnClickListener {
            togglePausa()
        }

        finePartitaB.setOnClickListener {
            finePartita()
            finish()
        }

        onBackPressedDispatcher.addCallback(this){
            finePartita()
            finish()
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
                aggiungiInput(char)
            }
        }

        //controllo se esiste un stato precedente
        if (savedInstanceState != null) {
            //Ripristino variabili
            stringaInput = savedInstanceState.getString("STRINGA_INPUT","")
            countRettangoliPremutiTurno = savedInstanceState.getInt("CONTATORE_RECT", 0)
            simonGame.sequenzaCorrente = savedInstanceState.getString("SEQUENZA_CORRENTE_PARTITA","")
            simonGame.difficoltaSequenza = savedInstanceState.getInt("DIFFICOLTA_CORRENTE_PARTITA",simonGame.minDifficoltaSequenza)
            riproduzioneIndice = savedInstanceState.getInt("RIPRODUZIONE_INDICE",0)
            val statoString = savedInstanceState.getString("STATO_PARTITA")
            statoPartita = statoString?.let {
                StatoPartita.valueOf(it)
            } ?: StatoPartita.IDLE
            //Ripristinare lo stato
            ripristinaStatoPartita()
        }
        //Refresh iniziale (necessario?)
        aggiornaUIStato()
    }
}