package com.example.pdsesimongameapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Partita::class], version = 1)
abstract class PartitaDatabase : RoomDatabase() {
    abstract fun partitaDao() : PartitaDao

    companion object {
        @Volatile
        private var INSTANCE : PartitaDatabase? = null

        fun getDatabase(context : Context): PartitaDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PartitaDatabase::class.java,
                    "partite_simon_database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}