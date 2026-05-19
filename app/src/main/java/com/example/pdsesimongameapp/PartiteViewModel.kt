package com.example.pdsesimongameapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PartiteViewModel : ViewModel() {
    val listaPartite = MutableLiveData<MutableList<SimonGame.SimonGameData>>()

    init{
        listaPartite.value = RegistroPartite.listaPartite.toMutableList()
    }
}