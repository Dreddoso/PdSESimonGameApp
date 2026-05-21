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
    object  SequenzaFinita : FeedbackGioco()
}

class GiocoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DataUIStatoPartita())
    val uiState = _uiState.asStateFlow() //Variabile pubblica leggibile ma non modificabile da UI

    private val _feedbacks = MutableSharedFlow<FeedbackGioco>() //Per comunciazione con activity
    val feedbacks = _feedbacks.asSharedFlow()

    private lateinit var simonGame : SimonGame

    private var turnoJob : Job? = null

    fun avviaPartita(){
        simonGame = SimonGame()
        avviaNuovoTurno()
    }

    fun avviaNuovoTurno(){
        _uiState.update { currentState ->
            currentState.copy(
                statoPartita = StatoPartita.TURNO_COMPUTER,
                sequenzaComputer = currentState.sequenzaComputer + simonGame.generaCarattere()
            )
        }
        riproduciSequenza()
    }

    fun riprendiTurno(){
        _uiState.update {
            it.copy(
                statoPartita = StatoPartita.TURNO_COMPUTER
            )
        }
        riproduciSequenza()
    }

    fun riproduciSequenza(){
        turnoJob?.cancel()

        turnoJob = viewModelScope.launch{
            val state = _uiState.value
            delay(700)

            for (i in state.indiceRiproduzione until state.sequenzaComputer.length){
                val char = state.sequenzaComputer[i]
                _feedbacks.emit(FeedbackGioco.Evidenzia(char))

                delay(600)

                _uiState.update {
                    it.copy(indiceRiproduzione = i + 1)
                }
            }

            _uiState.update {
                it.copy(
                    statoPartita = StatoPartita.TURNO_PLAYER,
                    indiceRiproduzione = 0
                )
            }
        }
    }

    fun concludiTurno(){
        turnoJob?.cancel()
        simonGame.aumentaDifficolta()
        _uiState.update {
            it.copy( //TODO: controlla se corretto
                difficoltaPartita = if(it.difficoltaPartita < simonGame.difficoltaSequenza) simonGame.difficoltaSequenza else it.difficoltaPartita + 1
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
        val salvataggio = simonGame.creaSalvataggioPartita(
            bestScore = stato.bestScore,
            sequenzaPartita = stato.sequenzaComputer,
            indiceErrore = stato.indiceErrore
        )
        RegistroPartite.addPartita(salvataggio)
    }

    fun aggiungiInput(char : Char){
        val state = _uiState.value
        val corretto = simonGame.controllaCarattere(char,state.indiceInput)

        if(!corretto){
            _uiState.update {
                it.copy(
                    statoPartita = StatoPartita.GAME_OVER,
                    indiceErrore = it.indiceInput
                )
            }
            return
        }

        val nuovoInput = state.stringaInput + char
        _uiState.update {
            it.copy(
                stringaInput = nuovoInput
            )
        }
        if (nuovoInput.length == state.sequenzaComputer.length){
            concludiTurno()
        }
    }
}