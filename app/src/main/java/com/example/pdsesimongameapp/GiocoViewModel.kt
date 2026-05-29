package com.example.pdsesimongameapp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class  FeedbackGioco {
    data class Evidenzia(val char: Char) : FeedbackGioco()
    object SequenzaIniziata : FeedbackGioco()
    object  SequenzaFinita : FeedbackGioco()

    object GameOver : FeedbackGioco()
}

class GiocoViewModel(private val repository: PartitaRepository,
                     private val savedState: SavedStateHandle
) : ViewModel() {
    //Costante per la chiave di salvataggio
    companion object{
        private const val STATO_KEY = "stato_partita"
    }
    private val _uiState = MutableStateFlow(
        savedState.get<DataUIStatoPartita>(STATO_KEY) ?: DataUIStatoPartita()
    )
    val uiState = _uiState.asStateFlow() //Variabile pubblica leggibile ma non modificabile da UI

    private val _feedbacks = MutableSharedFlow<FeedbackGioco>() //Per comunciazione con activity
    val feedbacks = _feedbacks.asSharedFlow()

    private var simonGame : SimonGame = SimonGame()

    private var turnoJob : Job? = null
    private var indiceRiproduzione = 0

    private var statoPrePausa : StatoPartita? = null

    init {
        //Controllo se stato riproducendo la sequenza o era in pausa la sequenza (senno rimango bloccato in un loop)
        if(_uiState.value.statoPartita == StatoPartita.TURNO_COMPUTER ||
            _uiState.value.statoPartita == StatoPartita.PAUSA
        ){
            riproduciSequenza()
        }
    }


    //utile per aggiornare stato e SavedStateHandle contemporeanemente
    private fun aggiornaUIStato(nuovoStato: DataUIStatoPartita){
        _uiState.value = nuovoStato
        savedState[STATO_KEY] = nuovoStato
    }

    private  fun aggiornaBestScore(){
        _uiState.update {
            it.copy(
                bestScore = maxOf(it.bestScore,it.difficoltaPartita)
            )
        }
    }

    fun avviaPartita(){
        avviaNuovoTurno()
    }

    fun avviaNuovoTurno(){
        val currentState = _uiState.value
        val nuovoChar = simonGame.generaCarattere()
        val nuovaSequenza = currentState.sequenzaComputer + nuovoChar

        aggiornaUIStato(currentState.copy(
            statoPartita = StatoPartita.TURNO_COMPUTER,
            sequenzaComputer = nuovaSequenza
        ))
        riproduciSequenza()
    }

    fun riproduciSequenza(){
        turnoJob?.cancel()
        turnoJob = viewModelScope.launch{
            delay(700)
            _feedbacks.emit(FeedbackGioco.SequenzaIniziata)
            while (indiceRiproduzione < _uiState.value.sequenzaComputer.length){
                if(_uiState.value.statoPartita == StatoPartita.PAUSA) return@launch

                val char = _uiState.value.sequenzaComputer[indiceRiproduzione]
                _feedbacks.emit(FeedbackGioco.Evidenzia(char))

                indiceRiproduzione += 1
                delay(600)

            }

            aggiornaUIStato(_uiState.value.copy(statoPartita = StatoPartita.TURNO_PLAYER))
            indiceRiproduzione = 0
            _feedbacks.emit(FeedbackGioco.SequenzaFinita)
        }
    }

    fun concludiTurno(){
        _uiState.update {
            it.copy(
                statoPartita = StatoPartita.TURNO_COMPUTER
            )
        }
        //Turno completato aggiorna max sequenza corretta
        aggiornaBestScore()
        //Aumenta difficolta
        _uiState.update {
            it.copy(
                difficoltaPartita = it.difficoltaPartita + 1,
                stringaInput = "",
                indiceInput = 0
            )
        }
        viewModelScope.launch {
            delay(700)
            avviaNuovoTurno()
        }
    }

    fun concludiPartita(){
        val stato = _uiState.value
        if(stato.difficoltaPartita == 1 && stato.statoPartita == StatoPartita.TURNO_COMPUTER) {
            aggiornaUIStato(stato.copy(statoPartita = StatoPartita.FINE_PARTITA))
            return
        }
        val indiceErroreCorretto =
            if (stato.statoPartita == StatoPartita.TURNO_PLAYER)
                stato.indiceInput
            else
                indiceRiproduzione
        val punteggioFinale = if (stato.statoPartita == StatoPartita.GAME_OVER){
            stato.bestScore
        }else{
            maxOf(stato.bestScore,stato.difficoltaPartita-1)
        }
        val partita = Partita(
            score = punteggioFinale,
            indiceErrore = indiceErroreCorretto,
            sequenza = stato.sequenzaComputer
        )
        viewModelScope.launch {
            repository.salvaPartita(partita = partita)
            aggiornaUIStato(stato.copy(statoPartita = StatoPartita.FINE_PARTITA))
        }

    }

    fun aggiungiInput(char : Char){
        val state = _uiState.value
        if (state.statoPartita != StatoPartita.TURNO_PLAYER) return
        val corretto = simonGame.controllaCarattere(char,state.indiceInput, state.sequenzaComputer)

        if(!corretto){
            viewModelScope.launch {
                _feedbacks.emit(FeedbackGioco.GameOver)
                aggiornaUIStato(state.copy(
                    statoPartita = StatoPartita.GAME_OVER,
                    indiceErrore = state.indiceInput
                ))
            }
            return
        }

        val nuovoInput = state.stringaInput + char
        aggiornaUIStato(state.copy(
            stringaInput = nuovoInput,
            indiceInput =  state.indiceInput + 1
        ))
        if (nuovoInput.length == state.sequenzaComputer.length){
            concludiTurno()
        }
    }

    fun togglePausa(){
        val stato = uiState.value
        when(stato.statoPartita){
            StatoPartita.TURNO_COMPUTER -> {
                turnoJob?.cancel()
                statoPrePausa = stato.statoPartita
                aggiornaUIStato(stato.copy(statoPartita = StatoPartita.PAUSA))
            }
            StatoPartita.PAUSA -> {
                riproduciSequenza()
                aggiornaUIStato(stato.copy(statoPartita = statoPrePausa ?: StatoPartita.TURNO_COMPUTER))
            }
            else -> {
                //
            }
        }
    }


}