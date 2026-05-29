package com.example.pdsesimongameapp

import android.content.Context

object AppContainer {
    lateinit var repository: PartitaRepository
        private set

    fun init(context : Context){
        val db = PartitaDatabase.getDatabase(context = context)
        repository = PartitaRepository(db.partitaDao())
    }
}