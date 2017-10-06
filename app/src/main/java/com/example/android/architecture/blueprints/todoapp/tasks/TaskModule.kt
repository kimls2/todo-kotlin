package com.example.android.architecture.blueprints.todoapp.tasks

import com.example.android.architecture.blueprints.todoapp.di.ActivityScoped
import com.example.android.architecture.blueprints.todoapp.di.FragmentScoped
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a>
 */
@Module
abstract class TaskModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun tasksFragment(): TasksFragment

    @ActivityScoped
    @Binds abstract fun taskPresenter(presenter: TasksPresenter): TasksContract.Presenter

}