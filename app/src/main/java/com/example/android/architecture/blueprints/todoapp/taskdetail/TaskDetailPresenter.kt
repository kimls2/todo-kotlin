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
package com.example.android.architecture.blueprints.todoapp.taskdetail

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Listens to user actions from the UI ([TaskDetailFragment]), retrieves the data and updates
 * the UI as required.
 */
class TaskDetailPresenter @Inject constructor(
        var taskId: String,
        private val tasksRepository: TasksRepository
) : TaskDetailContract.Presenter {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var taskDetailView: TaskDetailContract.View

    override fun subscribe(view: TaskDetailContract.View) {
        taskDetailView = view
        taskDetailView.presenter = this
        openTask()
    }

    override fun unSubscribe() {
        compositeDisposable.clear()
    }

    private fun openTask() {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
            return
        }

        taskDetailView.setLoadingIndicator(true)
        tasksRepository.getTask(taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext = {
                    showTask(it)
                }, onError = {
                    taskDetailView.showMissingTask()
                }, onComplete = {
                    taskDetailView.setLoadingIndicator(false)
                })
    }

    override fun editTask() {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
            return
        }
        taskDetailView.showEditTask(taskId)
    }

    override fun deleteTask() {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
            return
        }
        tasksRepository.deleteTask(taskId)
        taskDetailView.showTaskDeleted()
    }

    override fun completeTask() {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
            return
        }
        tasksRepository.completeTask(taskId)
        taskDetailView.showTaskMarkedComplete()
    }

    override fun activateTask() {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
            return
        }
        tasksRepository.activateTask(taskId)
        taskDetailView.showTaskMarkedActive()
    }

    private fun showTask(task: Task) {
        with(taskDetailView) {
            if (taskId.isEmpty()) {
                hideTitle()
                hideDescription()
            } else {
                showTitle(task.title)
                showDescription(task.description)
            }
            showCompletionStatus(task.completed)
        }
    }
}
