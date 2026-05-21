package com.example.pdsesimongameapp

enum class StatoPartita{
    IDLE, //nessuna partita iniziata
    TURNO_COMPUTER, //computer mostra la sequenza
    TURNO_PLAYER, //giocatore inserisce input
    PAUSA, //pausa durante turno computer
    GAME_OVER, //errore del giocatore
    FINE_PARTITA //Terminazione volontaria della partita
}