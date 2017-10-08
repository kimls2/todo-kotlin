package com.example.android.architecture.blueprints.todoapp.data.source.local

import android.arch.persistence.room.*
import com.example.android.architecture.blueprints.todoapp.data.Task
import io.reactivex.Flowable

@Dao
interface TasksDao {

    @Query("SELECT * FROM Tasks")
    fun getTasks(): Flowable<List<Task>>

    @Query("SELECT * FROM Tasks WHERE entryid = :taskId")
    fun getTaskById(taskId: String): Flowable<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: Task)

    @Update
    fun updateTask(task: Task): Int

    @Query("UPDATE tasks SET completed = :completed WHERE entryid = :taskId")
    fun updateCompleted(taskId: String, completed: Boolean)

    @Query("DELETE FROM Tasks WHERE entryid = :taskId")
    fun deleteTaskById(taskId: String): Int

    @Query("DELETE FROM Tasks")
    fun deleteTasks()

    @Query("DELETE FROM Tasks WHERE completed = 1")
    fun deleteCompletedTasks(): Int
}