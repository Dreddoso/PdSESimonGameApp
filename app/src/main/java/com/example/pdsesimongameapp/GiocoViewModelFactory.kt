package com.example.pdsesimongameapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GiocoViewModelFactory(
    private val repository: PartitaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) : T {
        if(modelClass.isAssignableFrom(GiocoViewModel::class.java)){
            return GiocoViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel class sconosciuta")
    }
}