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
package com.example.android.architecture.blueprints.todoapp.data.source.remote

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import io.reactivex.Flowable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the data source that adds a latency simulating network.
 */
@Singleton
class TasksRemoteDataSource @Inject constructor() : TasksDataSource {

    private val SERVICE_LATENCY_IN_MILLIS = 500L

    private var TASKS_SERVICE_DATA = LinkedHashMap<String, Task>(2)

    init {
        addTask(UUID.randomUUID().toString(), "Build tower in Pisa", "Ground looks good, no foundation work required.")
        addTask(UUID.randomUUID().toString(), "Finish bridge in Tacoma", "Found awesome girders at half the cost!")
    }

    private fun addTask(taskId: String, title: String, description: String) {
        val newTask = Task(taskId, title, description, false)
        TASKS_SERVICE_DATA.put(newTask.id, newTask)
    }

    override fun getTasks(): Flowable<List<Task>> {
        return Flowable
                .fromIterable(TASKS_SERVICE_DATA.values)
                .delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS)
                .toList()
                .toFlowable()
    }

    override fun getTask(taskId: String): Flowable<Task> {
        val task = TASKS_SERVICE_DATA[taskId]
        return if (task != null) {
            Flowable.just(task).delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS)
        } else {
            Flowable.empty<Task>()
        }
    }


    override fun saveTask(task: Task) {
        TASKS_SERVICE_DATA.put(task.id, task)
    }

    override fun completeTask(task: Task) {
        val completedTask = Task(task.title, task.description, task.id, true)
        TASKS_SERVICE_DATA.put(task.id, completedTask)
    }

    override fun completeTask(taskId: String) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun activateTask(task: Task) {
        val activeTask = Task(task.title, task.description, task.id, task.completed)
        TASKS_SERVICE_DATA.put(task.id, activeTask)
    }

    override fun activateTask(taskId: String) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun clearCompletedTasks() {
        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues {
            !it.completed
        } as LinkedHashMap<String, Task>
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override fun deleteTask(taskId: String) {
        TASKS_SERVICE_DATA.remove(taskId)
    }
}