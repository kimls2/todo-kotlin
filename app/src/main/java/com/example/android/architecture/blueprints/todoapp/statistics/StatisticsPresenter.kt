/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.example.android.architecture.blueprints.todoapp.statistics


import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.google.common.primitives.Ints
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Listens to user actions from the UI ([StatisticsFragment]), retrieves the data and updates
 * the UI as required.
 */
class StatisticsPresenter(
        private val tasksRepository: TasksRepository,
        private val statisticsView: StatisticsContract.View
) : StatisticsContract.Presenter {
    private val compositeDisposable = CompositeDisposable()

    init {
        statisticsView.presenter = this
    }

    override fun subscribe() {
        loadStatistics()
    }

    override fun unSubscribe() {
        compositeDisposable.clear()
    }

    private fun loadStatistics() {
        statisticsView.setProgressIndicator(true)
        EspressoIdlingResource.increment() // App is busy until further notice
        val tasks = tasksRepository
                .getTasks()
                .flatMap { Flowable.fromIterable(it) }
        val activeTasks = tasks.filter({ it.isActive }).count().toFlowable()
        val completedTasks = tasks.filter({ it.completed }).count().toFlowable()
        val disposable = Flowable
                .zip(activeTasks,
                        completedTasks,
                        BiFunction<Long?, Long?, Pair<Long, Long>>(function = { active, completed -> Pair(active, completed) }))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    if (!EspressoIdlingResource.countingIdlingResource.isIdleNow) {
                        EspressoIdlingResource.decrement() // Set app as idle.
                    }
                }
                .subscribeBy(
                        onNext = {
                            statisticsView.showStatistics(Ints.saturatedCast(it.first), Ints.saturatedCast(it.second))
                        },
                        onError = {
                            statisticsView.showLoadingStatisticsError()
                        },
                        onComplete = {
                            statisticsView.setProgressIndicator(false)
                        }
                )

        compositeDisposable.add(disposable)
    }
}
