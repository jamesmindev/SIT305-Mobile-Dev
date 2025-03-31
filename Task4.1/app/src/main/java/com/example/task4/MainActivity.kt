package com.example.task4

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TaskDatabase.getDatabase(this)
        val repository = TaskRepository(database.taskDao())
        val viewModel = TaskViewModel(repository)

        setContent {
            Task4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    TaskManager(
                        viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskManager(viewModel: TaskViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current;
    val tasks by viewModel.allTasks.collectAsState(initial = emptyList())

    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Task Manager",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
        )


        HorizontalDivider()
        // Should list out all the tasks
        Column(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            LazyColumn {
                items(tasks) { task ->
                    TaskItem(task) { selectedTask ->
                        val intent = Intent(context, TaskDetailActivity::class.java)
                        intent.putExtra("taskId", selectedTask.id)
//                        intent.putExtra("task", selectedTask)
                        context.startActivity(intent)
                    }
                }
            }
        }

        Button(onClick = {
            val intent = Intent(context, CreateTaskActivity::class.java)
            context.startActivity(intent);
        }) {
            Text("Create Task")
        }
    }
}

@Composable
fun TaskItem(task: Task, onClick: (Task) -> Unit) {

    Column (
        modifier = Modifier.clickable { onClick(task) }
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Column(
            ) {
                Text(text = task.name, fontWeight = FontWeight.SemiBold)
                Text(text = "Due: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(task.dueDate))}", fontSize = 14.sp)
            }
        }

        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
fun TaskManagerPreview() {
    Task4Theme {
//        TaskManager(dummyTasks)
    }
}