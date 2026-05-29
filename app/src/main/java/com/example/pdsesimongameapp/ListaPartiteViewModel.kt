package com.example.pdsesimongameapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ListaPartiteViewModel(repository: PartitaRepository) : ViewModel() {
    val partite = repository.getPartite().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
}