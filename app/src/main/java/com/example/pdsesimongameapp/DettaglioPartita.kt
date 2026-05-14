package com.example.pdsesimongameapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DettaglioPartita : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dettaglio_partita)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val contatore = intent.getIntExtra("contatore",0)
        val sequenza = intent.getStringExtra("sequenza")

        val contatoreTV : TextView = findViewById(R.id.dettagliContatoreTV)
        contatoreTV.text = contatore.toString()
        val sequenzaTV : TextView = findViewById(R.id.dettagliSequenzaTV)
        sequenzaTV.text = sequenza
    }
}