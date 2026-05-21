package com.example.pdsesimongameapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "partiteTable")
data class Partita(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "punteggio") val score : Int?,
    @Ignore val indiceErrore : Int?,
    @ColumnInfo("sequenza") val sequenza : String?
)
