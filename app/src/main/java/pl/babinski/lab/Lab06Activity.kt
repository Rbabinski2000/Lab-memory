package pl.babinski.lab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.babinski.lab.ui.theme.LabmemoryTheme

class Lab06Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
        }
    }
    @Preview(showBackground = true)
    @Composable
    fun MainScreenPreview() {
            MainScreen(
            )
    }
    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "list") {
            composable("list") { ListScreen(navController = navController) }
            composable("form") { FormScreen(navController = navController) }
        }
    }

}


@Composable
fun ListScreen(navController: NavController) {
    Text(text = "List")
}

@Composable
fun FormScreen(navController: NavController) {
    Text("Formularz")
}