package pl.babinski.lab.Lab06.FormData

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FormView() {
    var textState: String = ("Hello")
    Scaffold {


    Row(modifier = Modifier.padding(16.dp)) {

        TextField(
            value = textState,
            onValueChange = { textState = it },
            label = { Text("Label") }
        )

        Text(text="abrakadabra")
    }
}
}
