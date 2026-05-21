package com.example.pdsesimongameapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PartitaDao {
    @Query("SELECT * FROM partiteTable")
    fun getAll() : List<Partita>

    @Query("SELECT * FROM partiteTable WHERE id IN partiteTable")
    fun loadAllByIds(ids : IntArray) : List<Partita>

    @Insert
    fun insertAll(vararg partite: Partita)

    @Insert
    fun insertPartita(vararg partita : Partita)

}