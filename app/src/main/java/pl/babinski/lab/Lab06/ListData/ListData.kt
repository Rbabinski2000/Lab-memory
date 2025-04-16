package pl.babinski.lab.Lab06.ListData

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pl.babinski.lab.Lab06.data.TodoTaskRepository
import java.time.LocalDate


fun todoTasks(): List<TodoTask> {
    return listOf(
        TodoTask(1,"Programming", LocalDate.of(2024, 4, 18), false, Priority.Low),
        TodoTask(2,"Teaching", LocalDate.of(2024, 5, 12), false, Priority.High),
        TodoTask(3,"Learning", LocalDate.of(2024, 6, 28), true, Priority.Low),
        TodoTask(4,"Cooking", LocalDate.of(2024, 8, 18), false, Priority.Medium),
    )
}

enum class Priority() {
    High, Medium, Low
}

data class TodoTask(
    val id:Int,
    val title: String,
    val deadline: LocalDate,
    val isDone: Boolean,
    val priority: Priority
)
class ListViewModel(val repository: TodoTaskRepository) : ViewModel() {
    val listUiState: StateFlow<ListUiState>
        get() {
            return repository.getAllAsStream().map { ListUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = ListUiState()
                )
        }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ListUiState(val items: List<TodoTask> = listOf())