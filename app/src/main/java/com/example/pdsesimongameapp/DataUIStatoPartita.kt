package com.example.pdsesimongameapp

data class DataUIStatoPartita (
    val statoPartita: StatoPartita = StatoPartita.IDLE,
    val sequenzaComputer : String = "",
    val difficoltaPartita: Int = 1,
    val stringaInput : String = "",
    val indiceInput : Int = 0,
    val indiceErrore : Int = 0,
    val indiceRiproduzione : Int = 0,
    val isPausa : Boolean = false,
    val isGameOver : Boolean = false,
    val bestScore : Int = 0
)
