package com.example.pdsesimongameapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.addCallback
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

        //TODO necessario rendere seryalizable simongamedata per poterla passare per intent con putExtra
        val contatore = intent.getIntExtra("bestscore",0)
        val index = intent.getIntExtra("indice",0)
        val sequenza : String = intent.getStringExtra("sequenza") ?: ""


        val contatoreTV : TextView = findViewById(R.id.dettagliContatoreTV)
        contatoreTV.text = contatore.toString()
        val sequenzaTV : TextView = findViewById(R.id.dettagliSequenzaTV)
        sequenzaTV.text = EditTextUtilis.getEditString(sequenza,index)

        onBackPressedDispatcher.addCallback{
            finish()
        }
    }
}