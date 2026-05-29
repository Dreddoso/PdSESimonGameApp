package com.example.pdsesimongameapp

class PartitaRepository(private val dao: PartitaDao) {
    fun getPartite() = dao.getAll()

    suspend fun salvaPartita(partita: Partita){
        dao.insertPartita(partita)
    }
}