package com.example.android.architecture.blueprints.todoapp.di

import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskActivity
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskModule
import com.example.android.architecture.blueprints.todoapp.statistics.StatisticsActivity
import com.example.android.architecture.blueprints.todoapp.statistics.StatisticsModule
import com.example.android.architecture.blueprints.todoapp.taskdetail.TaskDetailActivity
import com.example.android.architecture.blueprints.todoapp.taskdetail.TaskDetailModule
import com.example.android.architecture.blueprints.todoapp.tasks.TaskModule
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a>
 */
@Module
abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = arrayOf(TaskModule::class))
    abstract fun taskActivity(): TasksActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = arrayOf(StatisticsModule::class))
    abstract fun statisticsActivity(): StatisticsActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = arrayOf(AddEditTaskModule::class))
    abstract fun addEditTaskActivity(): AddEditTaskActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = arrayOf(TaskDetailModule::class))
    abstract fun taskDetailActivity(): TaskDetailActivity

}