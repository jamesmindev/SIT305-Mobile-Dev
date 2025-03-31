package com.example.task4.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.task4.model.Task

// Data Access Object - defining how to interact with the database.
@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): kotlinx.coroutines.flow.Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: Int): LiveData<Task>
}