package com.example.pdsesimongameapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**TODO: controllare se bestscore scelto va bene => Max sequenza dell'ultimo turno o max sequenza dell'intera partita?
 *       Perche se fosse max sequenza dell'intera partita in qualsiasi momento fa un errore la sequenza piu lunga è sempre quella del turno precedente
 *      in quanto anche se sbaglia nel turno corrente l'ultimo carattere,la sequenza piu lunga sara pari alla difficolta del turno precedente
 *       ovvero la difficolta della sequenza corrente -1 (quindi dovrebbe essere corretto cosi
 **/


class MainActivity : AppCompatActivity() {

     private lateinit var  viewModel : GiocoViewModel

    //Componenti UI
    lateinit var griglia : Map<Char, TextView>
    private lateinit var avviaB : Button
    private lateinit var pausaB : Button
    private lateinit var finePartitaB : Button
    private lateinit var outputTV : TextView
    private var evidenziaJob : Job? = null

    private var riproduciTonoJob : Job? = null

    private lateinit var audioGameManager: AudioGameManager


    fun aggiornaUIStato(stato: DataUIStatoPartita){
        when (stato.statoPartita){
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
                outputTV.text = ""
            }

            StatoPartita.TURNO_PLAYER -> {
                avviaB.isEnabled = false
                pausaB.isEnabled = false
                finePartitaB.isEnabled = true
                outputTV.text = stato.stringaInput
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
            }

            StatoPartita.FINE_PARTITA -> {
                avviaB.isEnabled = false
                pausaB.isEnabled = false
                finePartitaB.isEnabled = false
            }

        }
    }

    fun aggiornaInputAttivo(attivo: Boolean){
        griglia.values.forEach {
            it.isEnabled = attivo
        }
    }

    fun evidenziaView(view: View, alpha : Float = 0.4f, durataMs: Long = 450L){
        evidenziaJob?.cancel()
        
        evidenziaJob = lifecycleScope.launch {
            if(!view.isAttachedToWindow) return@launch //Basta questo controllo?
            //abbassa alpha
            view.alpha = alpha
            delay(durataMs)
            //ripristina dopo tot tempo
            view.alpha = 1f
            evidenziaJob?.cancel()
        }

    }

    fun riproduciTono(char: Char){
        riproduciTonoJob?.cancel()
        riproduciTonoJob = lifecycleScope.launch {
            audioGameManager.riproduciTono(char)
            riproduciTonoJob?.cancel()
        }
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

        val db = PartitaDatabase.getDatabase(this)
        val dao = db.partitaDao()
        val repository = PartitaRepository(dao)
        val factory = GiocoViewModelFactory(repository)

        viewModel = ViewModelProvider(this,factory)[GiocoViewModel::class.java]

        audioGameManager = AudioGameManager()

        avviaB = findViewById(R.id.avviaB)
        pausaB = findViewById(R.id.pausaB)
        finePartitaB = findViewById(R.id.finePartitaB)
        outputTV = findViewById(R.id.outputTV)

        avviaB.setOnClickListener {
            //Avvia partita
            viewModel.avviaPartita()
        }

        pausaB.setOnClickListener {
            viewModel.togglePausa()
        }

        finePartitaB.setOnClickListener {
            viewModel.concludiPartita()
            finish()
        }

        onBackPressedDispatcher.addCallback(this){
            val stato = viewModel.uiState.value.statoPartita
            if (stato == StatoPartita.GAME_OVER){
                viewModel.salvaPartita()
            }else{
                viewModel.concludiPartita()
            }
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
                viewModel.aggiungiInput(char)
                val view = griglia[char]
                if (view != null) {
                    evidenziaView(view)
                }
                audioGameManager.riproduciTono(char)
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.feedbacks.collect { feedback ->
                        when(feedback){
                            is FeedbackGioco.Evidenzia -> {
                                val view = griglia[feedback.char]
                                if (view != null) {
                                    evidenziaView(view)
                                }
                                riproduciTono(feedback.char)
                            }

                            is FeedbackGioco.GameOver ->{
                                Toast.makeText(
                                    this@MainActivity,
                                    "Sequenza Errata!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                //
                            }
                        }
                    }
                }

                launch {
                    viewModel.uiState.collect { stato ->
                        aggiornaUIStato(stato)
                        aggiornaInputAttivo(stato.statoPartita == StatoPartita.TURNO_PLAYER)
                    }
                }
            }
        }
    }
}