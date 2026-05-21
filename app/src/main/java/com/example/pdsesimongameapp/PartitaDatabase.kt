package com.example.pdsesimongameapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Partita::class], version = 1)
abstract class PartitaDatabase : RoomDatabase() {
    abstract fun partitaDao() : PartitaDao
}