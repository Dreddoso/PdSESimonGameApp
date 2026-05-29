package com.example.pdsesimongameapp

import android.app.Application

class SimonApp : Application() {
    override  fun onCreate(){
        super.onCreate()
        AppContainer.init(this)
    }
}