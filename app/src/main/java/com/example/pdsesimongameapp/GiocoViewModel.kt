package com.example.pdsesimongameapp

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

class GiocoViewModel(private val repository: PartitaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DataUIStatoPartita())
    val uiState = _uiState.asStateFlow() //Variabile pubblica leggibile ma non modificabile da UI

    private val _feedbacks = MutableSharedFlow<FeedbackGioco>() //Per comunciazione con activity
    val feedbacks = _feedbacks.asSharedFlow()

    private var simonGame : SimonGame = SimonGame()

    private var turnoJob : Job? = null
    private var indiceRiproduzione = 0

    private var statoPrePausa : StatoPartita? = null

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
        _uiState.update { currentState ->
            val nuovoChar = simonGame.generaCarattere()
            val nuovaSequenza = currentState.sequenzaComputer + nuovoChar
            currentState.copy(
                statoPartita = StatoPartita.TURNO_COMPUTER,
                sequenzaComputer = nuovaSequenza
            )
        }
        riproduciSequenza()
    }

    fun riproduciSequenza(){
        turnoJob?.cancel()
        if (turnoJob?.isActive == true) return
        turnoJob = viewModelScope.launch{
            delay(700)
            _feedbacks.emit(FeedbackGioco.SequenzaIniziata)
            while (indiceRiproduzione < _uiState.value.sequenzaComputer.length){
                val char = _uiState.value.sequenzaComputer[indiceRiproduzione]
                _feedbacks.emit(FeedbackGioco.Evidenzia(char))

                indiceRiproduzione += 1
                delay(600)

            }

            _uiState.update {
                it.copy(
                    statoPartita = StatoPartita.TURNO_PLAYER,
                )
            }
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
        turnoJob?.cancel() //Rindondante?
        val stato = _uiState.value
        if (stato.statoPartita == StatoPartita.TURNO_COMPUTER && stato.difficoltaPartita <= 1){
            //Come se partita non fosse mai iniziata
            //Non salvo nessuna partita e non aggiungo elemento alla lista
            _uiState.update {
                it.copy(
                    statoPartita = StatoPartita.FINE_PARTITA //Torna a schermata 2 senza salvare
                )
            }
            return
        }
        //Se non era il primo turno salva partita
        val indiceErroreCorretto =
            if (stato.statoPartita == StatoPartita.TURNO_PLAYER)
                stato.indiceInput
            else
                indiceRiproduzione
        _uiState.update {
            it.copy(
                indiceErrore = indiceErroreCorretto
            )
        }
        salvaPartita()
        _uiState.update {
            it.copy(
                statoPartita = StatoPartita.FINE_PARTITA
            )
        }

    }

    fun salvaPartita(){
        val stato = _uiState.value
        //Se prima sequenza => livello 1 e turno computer (sto mostrando la sequenza) -> non salvo
        if(stato.difficoltaPartita == 1 && stato.statoPartita == StatoPartita.TURNO_COMPUTER) return
        val partita = Partita(
            score = stato.bestScore,
            indiceErrore = stato.indiceErrore,
            sequenza = stato.sequenzaComputer
        )
        viewModelScope.launch {
            repository.salvaPartita(partita)
        }
    }

    fun aggiungiInput(char : Char){
        val state = _uiState.value
        if (state.statoPartita != StatoPartita.TURNO_PLAYER) return
        val corretto = simonGame.controllaCarattere(char,state.indiceInput, state.sequenzaComputer)

        if(!corretto){
            _uiState.update {
                it.copy(
                    statoPartita = StatoPartita.GAME_OVER,
                    indiceErrore = it.indiceInput  //TODO controllare se corretto
                )
            }
            viewModelScope.launch {
                _feedbacks.emit(FeedbackGioco.GameOver)
            }

            return
        }

        val nuovoInput = state.stringaInput + char
        _uiState.update {
            it.copy(
                stringaInput = nuovoInput,
                indiceInput =  it.indiceInput + 1
            )
        }
        if (nuovoInput.length == state.sequenzaComputer.length){
            concludiTurno()
        }
    }

    fun togglePausa(){
        _uiState.update {
            when(it.statoPartita){
                StatoPartita.TURNO_COMPUTER ->{
                    turnoJob?.cancel()
                    statoPrePausa = it.statoPartita
                    it.copy(statoPartita = StatoPartita.PAUSA)
                }
                StatoPartita.PAUSA -> {
                    riproduciSequenza()
                    it.copy(
                        statoPartita = statoPrePausa ?: StatoPartita.TURNO_COMPUTER
                    )
                }
                else -> it
            }
        }
    }


}