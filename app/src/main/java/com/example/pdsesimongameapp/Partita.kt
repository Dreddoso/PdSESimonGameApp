package com.example.pdsesimongameapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partiteTable")
data class Partita(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "punteggio") val score : Int,
    @ColumnInfo(name = "indiceErrore") val indiceErrore : Int,
    @ColumnInfo("sequenza") val sequenza : String
)
