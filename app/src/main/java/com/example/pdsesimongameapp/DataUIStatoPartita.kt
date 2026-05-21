package com.example.pdsesimongameapp

data class DataUIStatoPartita(
    val statoPartita: StatoPartita = StatoPartita.IDLE,
    val sequenzaComputer: String = "",
    val difficoltaPartita: Int = 1,
    val stringaInput: String = "",
    val indiceInput: Int = 0,
    val indiceErrore: Int = 0,
    val bestScore: Int = 0
)
