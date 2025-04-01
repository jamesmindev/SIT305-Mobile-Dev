package com.example.task4

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskDetailActivity : ComponentActivity() {
//    private lateinit var task: Task // To store the task passed from the previous activity
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(TaskDatabase.getDatabase(this).taskDao()))
    }
    private var taskId: Int = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get data from intent
//        task = intent.getParcelableExtra("task")!!
        taskId = intent.getIntExtra("taskId", -1)
        if (taskId == -1) throw IllegalArgumentException("Invalid task ID.")

//        val taskName = intent.getStringExtra("name") ?: "(Untitled)"
//        val taskDueDate = intent.getStringExtra("dueDate") ?: "No Due Date"
//        val taskDescription = intent.getStringExtra("description") ?: ""

        setContent {
            Task4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val task by taskViewModel.getTaskById(taskId).observeAsState()

                    task?.let { taskData ->
                        TaskDetailScreen(
                            task = taskData,
                            viewModel = taskViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskDetailScreen(task: Task, viewModel: TaskViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current;

    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        TextButton(onClick = {
            (context as? ComponentActivity)?.finish()
        }) {
            Text("Back")
        }

        Spacer(modifier= Modifier.height(8.dp))

        Text(text = task.name, fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier= Modifier.height(16.dp))

        Text("Description", fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
        Text(text = task.description)
        Spacer(modifier= Modifier.height(16.dp))

        Text("Due Date", fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
        Text(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(task.dueDate)))
        Spacer(modifier= Modifier.height(24.dp))

        Button(onClick = {
            val intent = Intent(context, EditTaskActivity::class.java)
            intent.putExtra("task", task)
            context.startActivity(intent)
        }) {
            Text("Edit")
        }
        Button(onClick = {
            viewModel.deleteTask(task)
            (context as? ComponentActivity)?.finish()
        },
            colors = ButtonDefaults.filledTonalButtonColors()
        ) {
            Text("Delete")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskDetailScreenPreview() {
//    TaskDetailScreen("Buy Eggs", "2025-04-10", "Buy a dozen of eggs.")
}
