package pl.babinski.lab.Lab06.FormData

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.babinski.lab.Lab06.ListData.Priority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTaskInputForm(
    item: TodoTaskForm,
    modifier: Modifier = Modifier,
    onValueChange: (TodoTaskForm) -> Unit = {},
    enabled: Boolean = true
) {
    // Title input
    Text("Tytuł zadania")
    TextField(
        value = item.title,
        onValueChange = { onValueChange(item.copy(title = it)) },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled
    )

    // Date Picker field for deadline
    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        yearRange = IntRange(2000, 2030),
        initialSelectedDateMillis = item.deadline
    )
    var showDialog by remember { mutableStateOf(false) }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        text = "Data: " + SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date(item.deadline)),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineSmall
    )
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    datePickerState.selectedDateMillis?.let {
                        onValueChange(item.copy(deadline = it))
                    }
                }) {
                    Text("Wybierz")
                }
            }
        ) {
            DatePicker(state = datePickerState, showModeToggle = true)
        }
    }

    // Switch for isDone
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Zadanie wykonane")
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = item.isDone,
            onCheckedChange = { onValueChange(item.copy(isDone = it)) }
        )
    }

    // RadioButtons for priority selection
    Text("Priorytet:")
    Column {
        listOf(Priority.High.name, Priority.Medium.name, Priority.Low.name).forEach { prio ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = item.priority == prio,
                    onClick = { onValueChange(item.copy(priority = prio)) }
                )
                Text(
                    text = prio,
                    modifier = Modifier.clickable { onValueChange(item.copy(priority = prio)) }
                )
            }
        }
    }
}