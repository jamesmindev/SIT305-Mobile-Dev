package com.example.task4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.task4.data.TaskDatabase
import com.example.task4.data.TaskRepository
import com.example.task4.model.Task
import com.example.task4.ui.theme.Task4Theme
import com.example.task4.ui.viewmodel.TaskViewModel
import com.example.task4.ui.viewmodel.TaskViewModelFactory

class EditTaskActivity : ComponentActivity() {
    private lateinit var task: Task // To store the task passed from the previous activity
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(TaskDatabase.getDatabase(this).taskDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get data from intent
        task = intent.getParcelableExtra("task")!!

        setContent {
            Task4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EditTaskActivityScreen(
                        task = task,
                        viewModel = taskViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun EditTaskActivityScreen(task: Task, viewModel: TaskViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current;

    var taskName by remember { mutableStateOf(task.name) }
    var taskDueDate by remember { mutableLongStateOf(task.dueDate) }
    var taskDescription by remember { mutableStateOf(task.description) }

    Column (
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        TextButton(onClick = {
            (context as? ComponentActivity)?.finish()
        }) {
            Text("Back")
        }

        // Heading
        Text("Update Task", fontSize = 24.sp,
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

        // Button to add task
        Button(onClick = {
            if (taskName.isNotEmpty() && taskDueDate > 0 && taskDescription.isNotEmpty()) {
                val updatedTask = task.copy(name = taskName, dueDate = taskDueDate, description = taskDescription)
                viewModel.updateTask(updatedTask)

                // Once the task is updated, go back to the previous screen
                (context as? ComponentActivity)?.finish()
            }
        }) {
            Text("Update Task")
        }
    }
}

