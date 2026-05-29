package com.example.pdsesimongameapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras

class GiocoViewModelFactory(
    private val repository: PartitaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras) : T {
        if(modelClass.isAssignableFrom(GiocoViewModel::class.java)){
            return GiocoViewModel(repository, extras.createSavedStateHandle()) as T
        }
        throw IllegalArgumentException("ViewModel class sconosciuta")
    }
}