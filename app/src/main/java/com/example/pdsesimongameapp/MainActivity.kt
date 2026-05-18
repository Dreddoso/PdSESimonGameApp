package com.example.pdsesimongameapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**TODO: controllare se bestscore scelto va bene => Max sequenza dell'ultimo turno o max sequenza dell'intera partita?
 *       Perche se fosse max sequenza dell'intera partita in qualsiasi momento fa un errore la sequenza piu lunga è sempre quella del turno precedente
 *      in quanto anche se sbaglia nel turno corrente l'ultimo carattere,la sequenza piu lunga sara pari alla difficolta del turno precedente
 *       ovvero la difficolta della sequenza corrente -1 (quindi dovrebbe essere corretto cosi
 **/

class MainActivity : AppCompatActivity() {
    //Componenti UI
    lateinit var griglia : Map<Char, TextView>
    //val outputTV : TextView = findViewById(R.id.outputTV)
    //val finePartitaB : Button = findViewById(R.id.finePartitaB)
    //val pausaB : Button = findViewById(R.id.pausaB)
    //val avviaB : Button = findViewById(R.id.avviaB)

    //JOB per gestire la pausa durante visualizzazione della sequenza
    var turnoJob : Job? = null

    //FLAG di Stato del Gioco
    var isInputAbilitato : Boolean = false
    var partitaInPausa : Boolean = false
    var partitaInCorso : Boolean = false
    var isGameOver : Boolean = false

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
        evidenziaView(griglia[carattere]!!,CoroutineScope(Dispatchers.Main))


        if (!simonGame.controllaCarattere(carattere,countRettangoliPremutiTurno-1)){
            isGameOver = true
            partitaInCorso = false
            isInputAbilitato = false
            Toast.makeText(this, resources.getString(R.string.testo_errore),Toast.LENGTH_SHORT).show()
            val finePartitaButton : Button = findViewById(R.id.finePartitaB)
            finePartitaButton.isEnabled = false
            val pausaButton : Button = findViewById(R.id.pausaB)
            pausaButton.isEnabled = false
            //Salva Partita
            val salvataggio = simonGame.creaSalvataggioPartitaCorrente(simonGame.sequenzaCorrente,countRettangoliPremutiTurno,simonGame.difficoltaSequenza-1)
            RegistroPartite.addPartita(salvataggio)
            //reset
            stringaInput = ""
            countRettangoliPremutiTurno = 0
            countRettangoliPremutiCorrettamente = 0
            precContatoreRettangoliCorretti = 0
            simonGame.resetPartita()
            return
        }

        //Incremento contatore rettangoli corretti
        countRettangoliPremutiCorrettamente++

        if(stringaInput.length == simonGame.difficoltaSequenza){
            completaTurno()
        }
    }

    //Controllo per input utente (utile o rindondante?)
    fun isInputGrigliaAbilitato() : Boolean {
       return isInputAbilitato
    }

    /*TODO meglio togliere la creazione di un altra coroutine e quindi togliere scope, e rendere la fun -> suspend fun ??
            quindi stesso codice senza launch*/
    fun evidenziaView(view: View, scope : CoroutineScope, alpha : Float = 0.4f, durataMs: Long = 150L){
        scope.launch(Dispatchers.Main){
            //abbassa alpha
            view.alpha = alpha
            delay(durataMs)
            //ripristina dopo tot tempo
            view.alpha = 1f
        }
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
        //Salva turno precedente
        precContatoreRettangoliCorretti = countRettangoliPremutiCorrettamente
        //Resetto Input
        isInputAbilitato = false
        countRettangoliPremutiTurno = 0
        countRettangoliPremutiCorrettamente = 0
        stringaInput = ""
        val output : TextView = findViewById(R.id.outputTV)
        output.text = stringaInput
        //Aumenta difficoltà e avvia nuovo turno
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            simonGame.aumentaDifficolta()
            iniziaTurno()
        }
    }

    fun iniziaTurno(){
        //Pulsante pausa serve per stoppare (e poi far ripartire) la visione della sequenza
        val pausaB : Button = findViewById(R.id.pausaB)
        pausaB.isEnabled = true

        turnoJob = CoroutineScope(Dispatchers.Main).launch {

            //ALLA fine ne genero uno alla volta, sono quelli da visualizzare che crescono
            simonGame.sequenzaCorrente += simonGame.generaCarattere()

            for(c in simonGame.sequenzaCorrente){

                //se in pausa aspetta
                while(partitaInPausa){
                    delay(100)
                }

                val view = griglia[c]!!
                evidenziaView(view = view, scope = this)
                delay(1000)
            }

            pausaB.isEnabled = false
            isInputAbilitato = true
        }
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putString("STRINGA_INPUT", stringaInput)
        outState.putBoolean("INPUT_ABILITATO", isInputAbilitato)
        outState.putInt("CONTATORE_RECT", countRettangoliPremutiTurno)
        outState.putBoolean("PARTITA_IN_PAUSA",partitaInPausa)
        outState.putBoolean("PARTITA_IN_CORSO",partitaInCorso)
        outState.putInt("CONTATORE_CORRECT_RECT",countRettangoliPremutiCorrettamente)
        outState.putInt("CONTATORE_TURNO_PRECEDENTE", precContatoreRettangoliCorretti)
        outState.putString("SEQUENZA_CORRENTE_PARTITA", simonGame.sequenzaCorrente)
        outState.putInt("DIFFICOLTA_CORRENTE_PARTITA", simonGame.difficoltaSequenza)
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
            countRettangoliPremutiTurno = savedInstanceState.getInt("CONTATORE_RECT", 0)
            partitaInPausa = savedInstanceState.getBoolean("PARTITA_IN_PAUSA",false)
            partitaInCorso = savedInstanceState.getBoolean("PARTITA_IN_CORSO", false)
            countRettangoliPremutiCorrettamente = savedInstanceState.getInt("CONTATORE_CORRECT_RECT", 0)
            precContatoreRettangoliCorretti = savedInstanceState.getInt("CONTATORE_TURNO_PRECEDENTE",0)
            simonGame.sequenzaCorrente = savedInstanceState.getString("SEQUENZA_CORRENTE_PARTITA","")
            simonGame.difficoltaSequenza = savedInstanceState.getInt("DIFFICOLTA_CORRENTE_PARTITA",simonGame.minDifficoltaSequenza)

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
        }

        finePartitaB.setOnClickListener {
            //Controllo se è primo turno e utente non ha dato input ma è uscito subito
            val primaSequenza = simonGame.difficoltaSequenza == 1 && countRettangoliPremutiTurno == 0
            if(!primaSequenza) {
                //Salva partita
                val salvataggio = simonGame.creaSalvataggioPartitaCorrente(stringaInput,countRettangoliPremutiTurno,simonGame.difficoltaSequenza-1)
                RegistroPartite.addPartita(salvataggio)
            }

            //reset
            stringaInput = ""
            countRettangoliPremutiTurno = 0
            countRettangoliPremutiCorrettamente = 0
            precContatoreRettangoliCorretti = 0
            simonGame.resetPartita()

            //chiamata a seconda schermata
            val intent = Intent(this, Schermata2::class.java)
            startActivity(intent)
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