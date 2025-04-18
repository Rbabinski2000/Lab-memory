package pl.babinski.lab.Lab06.FormData

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.babinski.lab.Lab06.ListData.Priority
import pl.babinski.lab.Lab06.ListData.TodoTask
import pl.babinski.lab.Lab06.data.CurrentDateProvider
import pl.babinski.lab.Lab06.data.LocalDateConverter
import pl.babinski.lab.Lab06.data.TodoTaskRepository
import java.time.LocalDate

data class TodoTaskForm(
    val id: Int = 0,
    val title: String = "",
    // Deadline zapisujemy jako milisekundy
    val deadline: Long = LocalDateConverter.toMillis(LocalDate.now()),
    val isDone: Boolean = false,
    // Priority jako String, aby wybrać wartość z RadioButton
    val priority: String = Priority.Low.name
)

data class TodoTaskUiState(
    var todoTask: TodoTaskForm = TodoTaskForm(),
    val isValid: Boolean = false
)

// Funkcje rozszerzeń mapujące między modelami
fun TodoTask.toTodoTaskForm(): TodoTaskForm = TodoTaskForm(
    id = id,
    title = title,
    deadline = LocalDateConverter.toMillis(deadline),
    isDone = isDone,
    priority = priority.name
)

fun TodoTaskForm.toTodoTask(): TodoTask = TodoTask(
    id = id,
    title = title,
    deadline = LocalDateConverter.fromMillis(deadline),
    isDone = isDone,
    priority = Priority.valueOf(priority)
)

fun TodoTask.toTodoTaskUiState(isValid: Boolean = false): TodoTaskUiState = TodoTaskUiState(
    todoTask = this.toTodoTaskForm(),
    isValid = isValid
)

class FormViewModel(
    private val repository: TodoTaskRepository,
    private val dateProvider: CurrentDateProvider
) : ViewModel() {

    var todoTaskUiState by mutableStateOf(TodoTaskUiState())
        private set

    suspend fun save() {
        if (validate()) {
            repository.insertItem(todoTaskUiState.todoTask.toTodoTask())
        }
    }

    fun updateUiState(todoTaskForm: TodoTaskForm) {
        todoTaskUiState = TodoTaskUiState(
            todoTask = todoTaskForm,
            isValid = validate(todoTaskForm)
        )
    }

    // Walidacja sprawdza, czy tytuł nie jest pusty i czy deadline (data wybrana) jest późniejszy od bieżącej daty
    private fun validate(uiState: TodoTaskForm = todoTaskUiState.todoTask): Boolean {
        val titleValid = uiState.title.isNotBlank()
        val deadlineValid = LocalDateConverter.fromMillis(uiState.deadline)
            .isAfter(dateProvider.currentDate)
        return titleValid && deadlineValid
    }
}

