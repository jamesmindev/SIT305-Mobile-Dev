package com.example.task4.data

import androidx.lifecycle.LiveData
import com.example.task4.model.Task

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: kotlinx.coroutines.flow.Flow<List<Task>> = taskDao.getAllTasks()

    fun getTaskById(taskId: Int): LiveData<Task> {
        return taskDao.getTaskById(taskId)
    }

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }
}