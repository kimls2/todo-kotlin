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
package com.example.android.architecture.blueprints.todoapp.tasks

import android.app.Activity
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskActivity
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.di.ActivityScoped
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

/**
 * Listens to user actions from the UI ([TasksFragment]), retrieves the data and updates the
 * UI as required.
 */

@ActivityScoped
class TasksPresenter @Inject constructor(private val tasksRepository: TasksRepository)
    : TasksContract.Presenter {

    private lateinit var tasksView: TasksContract.View

    override fun subscribe(view: TasksContract.View) {
        tasksView = view
        tasksView.presenter = this
        loadTasks(false)
    }

    override var currentFiltering = TasksFilterType.ALL_TASKS
    private var firstLoad = true
    private val compositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        compositeDisposable.clear()
    }

    override fun result(requestCode: Int, resultCode: Int) {
        // If a task was successfully added, show snackbar
        if (AddEditTaskActivity.REQUEST_ADD_TASK ==
                requestCode && Activity.RESULT_OK == resultCode) {
            tasksView.showSuccessfullySavedMessage()
        }
    }

    override fun loadTasks(forceUpdate: Boolean) {
        // Simplification for sample: a network reload will be forced on first load.
        loadTasks(forceUpdate || firstLoad, true)
        firstLoad = false
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the [TasksDataSource]
     * *
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private fun loadTasks(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            tasksView.setLoadingIndicator(true)
        }
        if (forceUpdate) {
            tasksRepository.refreshTasks()
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment() // App is busy until further notice


        compositeDisposable.add(tasksRepository.getTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext = {
                    val tasksToShow = ArrayList<Task>()
                    for (task in it) {
                        when (currentFiltering) {
                            TasksFilterType.ALL_TASKS -> tasksToShow.add(task)
                            TasksFilterType.ACTIVE_TASKS -> if (task.isActive) {
                                tasksToShow.add(task)
                            }
                            TasksFilterType.COMPLETED_TASKS -> if (task.completed) {
                                tasksToShow.add(task)
                            }
                        }
                    }
                    // The view may not be able to handle UI updates anymore
                    if (!tasksView.isActive) {
                        return@subscribeBy
                    }
                    if (showLoadingUI) {
                        tasksView.setLoadingIndicator(false)
                    }

                    processTasks(tasksToShow)


                }, onComplete = {}, onError = {
                    tasksView.showLoadingTasksError()

                })
        )
    }

    private fun processTasks(tasks: List<Task>) {
        if (tasks.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            processEmptyTasks()
        } else {
            // Show the list of tasks
            tasksView.showTasks(tasks)
            // Set the filter label's text.
            showFilterLabel()
        }
    }

    private fun showFilterLabel() {
        when (currentFiltering) {
            TasksFilterType.ACTIVE_TASKS -> tasksView.showActiveFilterLabel()
            TasksFilterType.COMPLETED_TASKS -> tasksView.showCompletedFilterLabel()
            else -> tasksView.showAllFilterLabel()
        }
    }

    private fun processEmptyTasks() {
        when (currentFiltering) {
            TasksFilterType.ACTIVE_TASKS -> tasksView.showNoActiveTasks()
            TasksFilterType.COMPLETED_TASKS -> tasksView.showNoCompletedTasks()
            else -> tasksView.showNoTasks()
        }
    }

    override fun addNewTask() {
        tasksView.showAddTask()
    }

    override fun openTaskDetails(requestedTask: Task) {
        tasksView.showTaskDetailsUi(requestedTask.id)
    }

    override fun completeTask(completedTask: Task) {
        tasksRepository.completeTask(completedTask)
        tasksView.showTaskMarkedComplete()
        loadTasks(false, false)
    }

    override fun activateTask(activeTask: Task) {
        tasksRepository.activateTask(activeTask)
        tasksView.showTaskMarkedActive()
        loadTasks(false, false)
    }

    override fun clearCompletedTasks() {
        tasksRepository.clearCompletedTasks()
        tasksView.showCompletedTasksCleared()
        loadTasks(false, false)
    }

}
