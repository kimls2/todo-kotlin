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
package com.example.android.architecture.blueprints.todoapp.data.source

import com.example.android.architecture.blueprints.todoapp.data.Task
import io.reactivex.Flowable
import java.util.*

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 *
 *
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
class TasksRepository(
        val tasksRemoteDataSource: TasksDataSource,
        val tasksLocalDataSource: TasksDataSource
) : TasksDataSource {

    var cachedTasks: LinkedHashMap<String, Task> = LinkedHashMap()
    var cacheIsDirty = false

    override fun getTasks(): Flowable<List<Task>> {
        // Respond immediately with cache if available and not dirty
        if (cachedTasks.isNotEmpty() && !cacheIsDirty) {
            return Flowable.fromIterable<Task>(cachedTasks.values).toList().toFlowable()
        }

        val remoteTasks = getAndSaveRemoteTasks()

        return if (cacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            remoteTasks
        } else {
            // Query the local storage if available. If not, query the network.
            val localTasks = getAndCacheLocalTasks()
            Flowable.concat(localTasks, remoteTasks)
                    .filter { tasks -> !tasks.isEmpty() }
                    .firstOrError()
                    .toFlowable()
        }
    }

    private fun getAndSaveRemoteTasks(): Flowable<List<Task>> {
        return tasksRemoteDataSource
                .getTasks()
                .flatMap({ tasks ->
                    Flowable.fromIterable<Task>(tasks).doOnNext { task ->
                        tasksLocalDataSource.saveTask(task)
                        cachedTasks.put(task.id, task)
                    }.toList().toFlowable()
                })
                .doOnComplete({ cacheIsDirty = false })
    }

    private fun getAndCacheLocalTasks(): Flowable<List<Task>> {
        return tasksLocalDataSource.getTasks()
                .flatMap({ tasks ->
                    Flowable.fromIterable<Task>(tasks)
                            .doOnNext { task -> cachedTasks.put(task.id, task) }
                            .toList()
                            .toFlowable()
                })
    }

    override fun getTask(taskId: String): Flowable<Task> {
        val taskInCache = getTaskWithId(taskId)

        if (taskInCache != null) {
            return Flowable.just(taskInCache)
        }

        val localTask = getTaskWithIdFromLocalRepository(taskId)
        val remoteTask = tasksRemoteDataSource
                .getTask(taskId)
                .doOnNext({ task ->
                    tasksLocalDataSource.saveTask(task)
                    cachedTasks.put(task.id, task)
                })

        return Flowable.concat<Task>(localTask, remoteTask)
                .firstElement()
                .toFlowable()
    }

    private fun getTaskWithIdFromLocalRepository(taskId: String): Flowable<Task> {
        return tasksLocalDataSource
                .getTask(taskId)
                .doOnNext({ task ->
                    cachedTasks.put(taskId, task)
                })
                .firstElement().toFlowable()
    }

    override fun saveTask(task: Task) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(task) {
            tasksRemoteDataSource.saveTask(it)
            tasksLocalDataSource.saveTask(it)
        }
    }

    override fun completeTask(task: Task) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(task) {
            it.completed = true
            tasksRemoteDataSource.completeTask(it)
            tasksLocalDataSource.completeTask(it)
        }
    }

    override fun completeTask(taskId: String) {
        getTaskWithId(taskId)?.let {
            completeTask(it)
        }
    }

    override fun activateTask(task: Task) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(task) {
            it.completed = false
            tasksRemoteDataSource.activateTask(it)
            tasksLocalDataSource.activateTask(it)
        }
    }

    override fun activateTask(taskId: String) {
        getTaskWithId(taskId)?.let {
            activateTask(it)
        }
    }

    override fun clearCompletedTasks() {
        tasksRemoteDataSource.clearCompletedTasks()
        tasksLocalDataSource.clearCompletedTasks()

        cachedTasks = cachedTasks.filterValues {
            !it.completed
        } as LinkedHashMap<String, Task>
    }

    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     *
     *
     * Note: [TasksDataSource.GetTaskCallback.onDataNotAvailable] is fired if both data sources fail to
     * get the data.
     */

    override fun refreshTasks() {
        cacheIsDirty = true
    }

    override fun deleteAllTasks() {
        tasksRemoteDataSource.deleteAllTasks()
        tasksLocalDataSource.deleteAllTasks()
        cachedTasks.clear()
    }

    override fun deleteTask(taskId: String) {
        tasksRemoteDataSource.deleteTask(taskId)
        tasksLocalDataSource.deleteTask(taskId)
        cachedTasks.remove(taskId)
    }

    private fun refreshCache(tasks: List<Task>) {
        cachedTasks.clear()
        tasks.forEach {
            cacheAndPerform(it) {}
        }
        cacheIsDirty = false
    }

    private fun refreshLocalDataSource(tasks: List<Task>) {
        tasksLocalDataSource.deleteAllTasks()
        for (task in tasks) {
            tasksLocalDataSource.saveTask(task)
        }
    }

    private fun getTaskWithId(id: String) = cachedTasks[id]

    private inline fun cacheAndPerform(task: Task, perform: (Task) -> Unit) {
        val cachedTask = Task(task.title, task.description, task.id, task.completed).apply {
            task.completed
        }
        cachedTasks.put(cachedTask.id, cachedTask)
        perform(cachedTask)
    }


    companion object {

        private var INSTANCE: TasksRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.

         * @param tasksRemoteDataSource the backend data source
         * *
         * @param tasksLocalDataSource  the device storage data source
         * *
         * @return the [TasksRepository] instance
         */
        @JvmStatic
        fun getInstance(tasksRemoteDataSource: TasksDataSource,
                        tasksLocalDataSource: TasksDataSource): TasksRepository {
            return INSTANCE ?: TasksRepository(tasksRemoteDataSource, tasksLocalDataSource)
                    .apply { INSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}