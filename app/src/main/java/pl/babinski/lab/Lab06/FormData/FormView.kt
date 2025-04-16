package pl.babinski.lab.Lab06.FormData

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch


import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import pl.babinski.lab.Lab06.ListData.Priority
import pl.babinski.lab.Lab06.ListData.TodoTask
import java.time.LocalDate




@Composable
fun FormView() {
    val context = LocalContext.current


    val title = remember { mutableStateOf("") }
    val date = remember { mutableStateOf(LocalDate.now()) }
    val isDone = remember { mutableStateOf(false) }
    val priority = remember { mutableStateOf(Priority.Medium) }

    // DatePickerDialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            date.value = LocalDate.of(year, month + 1, day)
        },
        date.value.year,
        date.value.monthValue - 1,
        date.value.dayOfMonth
    )


    fun SaveItem(item:TodoTask){
        //todo
    }
    // Layout
    Column(modifier = Modifier
        .fillMaxSize()

        .padding(16.dp),

        ) {
        Row {
            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }


        Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = "${date.value}",
                onValueChange = { title.value = it },
                label={ Text("Deadline")},
                modifier = Modifier
                    /*.clickable { datePickerDialog.show() }*/
                    .fillMaxWidth(),
                readOnly = true,
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    datePickerDialog.show()
                                }
                            }
                        }
                    }
            )


        Spacer(modifier = Modifier.height(8.dp))

        Text("Priority:")
        Row {
            Priority.entries.forEach { p ->
                Row(
                    modifier = Modifier
                        .clickable { priority.value = p }
                        .padding(end = 8.dp)
                ) {
                    RadioButton(
                        selected = priority.value == p,
                        onClick = { priority.value = p }
                    )
                    Text(p.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Completed")
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = isDone.value,
                onCheckedChange = { isDone.value = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                /*SaveItem(
                    TodoTask(
                        title = title.value,
                        deadline = date.value,
                        isDone = isDone.value,
                        priority = priority.value
                    )
                )*/
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    /*}*/
    }
}