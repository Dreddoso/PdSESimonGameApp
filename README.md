##Stato del progetto
App Funzionante ma ancora grezza 
Le specifiche mi sembrano essere state implementate correttamente.
Utilizzo di Room per implementazione di un database e salvataggio locale delle partite.
Gestione dei toni audio tramite AudioTrack (vengono generati come sinusoidi da una classe).
Manca un miglioramento da un punto di vista stilistico/design.

##Informazioni Android
- Min SDK: API 26 (Android 8.0)
- Target SDK: API 35 (Android 15.0)
- Compile SDK: API 35 

##Emulatore utilizzato per test
- Nome: "Medium Phone API 35" 
- Sistema operativo: Android 15
- Risoluzione: 1080 x 2400 
- Architettura: x86_64


##Note
Funziona tutto fino a questo punto
I theme presenti non vengono utilizzati. Non è stato implementato il dark mode.
Forse i tempi tra un evento e un altro (evidenzziazione di una view durante la visualizzazione della sequenza, ecc) 
    sono troppo brevi, aumentare delay tra eventi. 
I toni potrebbero interrompersi in modo anomalo o essere troncati durante rotazioni molto veloci.