package pl.babinski.lab.Lab06.data

import android.app.Application
import android.content.Context

interface AppContainer {
    val todoTaskRepository: TodoTaskRepository
}

class AppDataContainer(private val context: Context): AppContainer{
    override val todoTaskRepository: TodoTaskRepository by lazy{
        DatabaseTodoTaskRepository(AppDatabase.getInstance(context).taskDao())
    }
}

class TodoApplication: Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this.applicationContext)
    }
}