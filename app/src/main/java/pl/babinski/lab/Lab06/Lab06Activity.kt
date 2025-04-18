package pl.babinski.lab.Lab06

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import pl.babinski.lab.Lab06.FormData.FormView
import pl.babinski.lab.Lab06.FormData.FormViewModel
import pl.babinski.lab.Lab06.FormData.TodoTaskInputBody
import pl.babinski.lab.Lab06.ListData.ListItem
import pl.babinski.lab.Lab06.ListData.ListViewModel
import pl.babinski.lab.Lab06.ListData.TodoTask
import pl.babinski.lab.Lab06.ListData.todoTasks
import pl.babinski.lab.Lab06.VModelProvider.AppViewModelProvider
import pl.babinski.lab.Lab06.components.NotificationBroadcastReceiver
import pl.babinski.lab.Lab06.data.AppContainer
import pl.babinski.lab.Lab06.data.LocalDateConverter
import pl.babinski.lab.Lab06.data.TodoApplication
import pl.babinski.lab.ui.theme.LabmemoryTheme

const val notificationID = 121
const val channelID = "Lab06_channel"
const val titleExtra = "ExtraTitle"
const val messageExtra = "extra"

class Lab06Activity : ComponentActivity() {
    companion object {
        lateinit var container: AppContainer
        var currentAlarmPendingIntent: PendingIntent? = null
        var currentAlarmTime: Long = Long.MAX_VALUE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        createNotificationChannel()
        container = (application as TodoApplication).container
        setContent {
            LabmemoryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
    private fun createNotificationChannel() {
        val name = "Lab06 Notifications"
        val descriptionText = "Notifications for approaching tasks."
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        Log.d("Alarm", "Notification channel created")
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun scheduleAlarmForTask(task: TodoTask) {
        val deadlineMillis = LocalDateConverter.toMillis(task.deadline)

        // 🔧 Dla testów – ustaw alarm na 10 sekund od teraz:
        val alarmTriggerTime = System.currentTimeMillis() + 10_000L
        Log.d("Alarm", "Scheduling alarm at: $alarmTriggerTime (${task.title})")

        if (currentAlarmPendingIntent != null && alarmTriggerTime < currentAlarmTime) {
            cancelAlarm()
            Log.d("Alarm", "Previous alarm canceled")
        }

        currentAlarmTime = alarmTriggerTime

        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java).apply {
            putExtra(titleExtra, "Deadline Alert")
            putExtra(messageExtra, "Zbliża się termin zadania: ${task.title}")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        currentAlarmPendingIntent = pendingIntent

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        // ⏰ Set one-time exact alarm (for testing)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTriggerTime,
            pendingIntent
        )

        Log.d("Alarm", "Alarm set successfully")
    }

    fun cancelAlarm() {
        currentAlarmPendingIntent?.let {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(it)
            currentAlarmPendingIntent = null
            currentAlarmTime = Long.MAX_VALUE
            Log.d("Alarm", "Alarm canceled")
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainScreenPreview() {
        LabmemoryTheme{
            MainScreen(
            )
        }
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
    title: String,
    showBackIcon: Boolean,
    route: String,
    onSaveClick: () -> Unit = { }) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = { Text(text = title) },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = { navController.navigate(route) }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (route !== "form") {
                OutlinedButton(
                    onClick = { navController.navigate("list") }
                )
                {
                    Text(
                        text = "Zapisz"
                    )
                }
            } else {
                IconButton(onClick = { /*TODO navController.navigate(route)*/ }) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "")
                }
            }
        }
    )
}

@Composable
fun ListScreen(
    navController: NavController,
    viewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val listUiState by viewModel.listUiState.collectAsState()
    val context = LocalContext.current

    // Gdy zmieni się lista zadań, szukamy niewykonanych z najbliższym terminem
    LaunchedEffect(listUiState.items) {
        val undoneTasks = listUiState.items.filter { !it.isDone }
        // Jeśli lista niewykonanych zadań nie jest pusta, wyszukujemy zadanie z najbliższym terminem:
        if (undoneTasks.isNotEmpty()) {
            val nearestTask = undoneTasks.minByOrNull { task ->
                LocalDateConverter.toMillis(task.deadline)
            }
            nearestTask?.let { task ->
                // Musimy uzyskać instancję MainActivity, by wywołać metodę scheduleAlarmForTask.
                // Zakładamy, że LocalContext.current wskazuje na aktywność.
                if (context is Lab06Activity) {
                    context.scheduleAlarmForTask(task)
                }
            }
        } else {
            // Brak niewykonanych zadań – anulujemy alarm
            if (context is Lab06Activity) {
                context.cancelAlarm()
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("form") }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add task",
                    modifier = Modifier.scale(1.5f)
                )
            }
        },
        topBar = {
            AppTopBar(
                navController = navController,
                title = "List",
                showBackIcon = false,
                route = "form"
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(
                items = listUiState.items,
                key = { it.id }
            ) {
                ListItem(item = it)
            }
        }
    }
}

@Composable
fun FormScreen(
    navController: NavController,
    viewModel: FormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AppTopBar(
                navController = navController,
                title = "Form",
                showBackIcon = true,
                route = "list",
                onSaveClick = {
                    coroutineScope.launch {
                        viewModel.save()
                        navController.navigate("list")
                    }
                }
            )
        }
    ) { innerPadding ->
        TodoTaskInputBody(
            todoUiState = viewModel.todoTaskUiState,
            onItemValueChange = viewModel::updateUiState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}