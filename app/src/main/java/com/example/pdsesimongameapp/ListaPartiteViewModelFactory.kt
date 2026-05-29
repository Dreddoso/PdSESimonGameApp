package com.example.pdsesimongameapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ListaPartiteViewModelFactory(
    private val repository: PartitaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListaPartiteViewModel::class.java)){
            return ListaPartiteViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel sconosciuta")
    }
}