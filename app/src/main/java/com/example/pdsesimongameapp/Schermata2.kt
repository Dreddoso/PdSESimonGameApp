package com.example.pdsesimongameapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class Schermata2 : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adattatoreRV : AdattatoreRV



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_schermata2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pulsanteNext : View = findViewById(R.id.pulsanteNext)
        pulsanteNext.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val db = PartitaDatabase.getDatabase(this@Schermata2)
        val repository = PartitaRepository(db.partitaDao())
        lifecycleScope.launch {
            val lista = repository.getPartite()
            adattatoreRV = AdattatoreRV(lista)
            //collegamento RecyclerView
            recyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this@Schermata2)
            recyclerView.adapter = adattatoreRV

        }


    }


}

