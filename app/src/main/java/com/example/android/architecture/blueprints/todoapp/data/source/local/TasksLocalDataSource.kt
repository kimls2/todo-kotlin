/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp.data.source.local

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
class TasksLocalDataSource @Inject constructor(private val tasksDao: TasksDao) : TasksDataSource {

//    private val tasksDao: TasksDao = ToDoDatabase.getInstance(context).taskDao()

    override fun getTasks(): Flowable<List<Task>> {
        return tasksDao.getTasks()
    }

    override fun getTask(taskId: String): Flowable<Task> {
        return tasksDao.getTaskById(taskId)
    }


    override fun saveTask(task: Task) {
        Completable.fromAction {
            tasksDao.insertTask(task)
        }.subscribeOn(Schedulers.io())

    }

    override fun completeTask(task: Task) {
        Completable.fromAction {
            tasksDao.updateTask(task)
        }.subscribeOn(Schedulers.io())
    }

    override fun completeTask(taskId: String) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun activateTask(task: Task) {
        Completable.fromAction {
            tasksDao.updateCompleted(task.id, false)
        }.subscribeOn(Schedulers.io())
    }

    override fun activateTask(taskId: String) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun clearCompletedTasks() {
        Completable.fromAction {
            tasksDao.deleteCompletedTasks()
        }.subscribeOn(Schedulers.io())
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteAllTasks() {
        Completable.fromAction {
            tasksDao.deleteTasks()
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteTask(taskId: String) {
        Completable.fromAction {
            tasksDao.deleteTaskById(taskId)
        }.subscribeOn(Schedulers.io())
    }

//    companion object {
//        private var INSTANCE: TasksLocalDataSource? = null
//
//        @JvmStatic
//        fun getInstance(context: Context) =
//                INSTANCE ?: TasksLocalDataSource(context).apply { INSTANCE = this }
//    }
}
