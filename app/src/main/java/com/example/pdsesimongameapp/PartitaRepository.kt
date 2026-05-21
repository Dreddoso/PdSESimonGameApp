package com.example.pdsesimongameapp

class PartitaRepository(private val dao: PartitaDao) {
    suspend fun getPartite() : List<Partita>{
        return dao.getAll()
    }

    suspend fun salvaPartita(partita: Partita){
        dao.insertPartita(partita)
    }
}