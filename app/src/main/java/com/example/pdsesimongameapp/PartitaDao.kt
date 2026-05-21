package com.example.pdsesimongameapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PartitaDao {
    @Query("SELECT * FROM partiteTable")
    suspend fun getAll() : List<Partita>

    @Insert
    suspend fun insertPartita(vararg partita : Partita)

}