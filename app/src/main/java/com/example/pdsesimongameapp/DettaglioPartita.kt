package com.example.pdsesimongameapp

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
        val spannableString = SpannableString(sequenza)
        spannableString.setSpan(
            ForegroundColorSpan(Color.RED),
            contatore,
            sequenza!!.length, //Non dovrebbe essere mai nullo perchè sopra definisco un valore di default 0 in caso non trovasse "contatore"
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val contatoreTV : TextView = findViewById(R.id.dettagliContatoreTV)
        contatoreTV.text = contatore.toString()
        val sequenzaTV : TextView = findViewById(R.id.dettagliSequenzaTV)
        sequenzaTV.text = spannableString
    }
}