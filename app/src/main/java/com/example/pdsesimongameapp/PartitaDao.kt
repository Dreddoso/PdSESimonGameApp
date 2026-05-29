package com.example.pdsesimongameapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PartitaDao {
    @Query("SELECT * FROM partiteTable")
    fun getAll() : Flow<List<Partita>>

    @Insert
    suspend fun insertPartita(vararg partita : Partita)

}