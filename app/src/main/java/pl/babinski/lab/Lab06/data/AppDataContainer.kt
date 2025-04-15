package pl.babinski.lab.Lab06.data

import android.app.Application
import android.content.Context

interface AppContainer {
    // właściwości zwracające wymagane instancje obiektów w aplikacji
}

class AppDataContainer(private val context: Context): AppContainer{
    //implementacja własciwości
}

class TodoApplication: Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this.applicationContext)
    }
}