package com.example.task4

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.task4.data.TaskDatabase
import com.example.task4.data.TaskRepository
import com.example.task4.model.Task
import com.example.task4.ui.theme.Task4Theme
import com.example.task4.ui.viewmodel.TaskViewModel
import com.example.task4.ui.viewmodel.TaskViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CreateTaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val taskViewModel: TaskViewModel by viewModels {
            TaskViewModelFactory(TaskRepository(TaskDatabase.getDatabase(this).taskDao()))
        }

        setContent {
            Task4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CreateTaskScreen(viewModel = taskViewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(viewModel: TaskViewModel, modifier: Modifier) {
    val context = LocalContext.current;

    var taskName by remember { mutableStateOf("") }
    var taskDueDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var taskDescription by remember { mutableStateOf("") }

    var error by remember { mutableStateOf(false) }

    Column (
        modifier = modifier.padding(horizontal = 16.dp).verticalScroll(rememberScrollState())
    ) {
        TextButton(onClick = {
            (context as? ComponentActivity)?.finish()
        }) {
            Text("Back")
        }

        // Heading
        Text("Create Task", fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp))

        TextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = taskDescription,
            onValueChange = { taskDescription = it },
            label = { Text("Description")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        DatePickerField(
            selectedDate = taskDueDate,
            onDateSelected = { taskDueDate = it }
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (error) {
            Text(text = "Please fill in the name and description.", color = Color.Red)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Button to add task
        Button(onClick = {
            error = false
            if (taskName.isNotEmpty() && taskDueDate > 0 && taskDescription.isNotEmpty()) {
                val task = Task(name = taskName, dueDate = taskDueDate, description = taskDescription)
                viewModel.addTask(task)

                // Once the task is added, finish the activity to go back to the previous screen
                (context as? ComponentActivity)?.finish()
            } else {
                error = true;
            }
        }) {
            Text("Add Task")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(selectedDate: Long, onDateSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            onDateSelected(selectedCalendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Due Date", fontWeight = FontWeight.SemiBold)
            Text(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate)))
        }

        TextButton (onClick = { datePickerDialog.show() }) {
            Text("Change Due Date")
        }
    }
}

