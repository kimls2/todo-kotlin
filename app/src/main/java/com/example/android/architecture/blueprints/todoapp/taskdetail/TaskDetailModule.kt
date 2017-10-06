package com.example.android.architecture.blueprints.todoapp.taskdetail

import com.example.android.architecture.blueprints.todoapp.di.ActivityScoped
import com.example.android.architecture.blueprints.todoapp.di.FragmentScoped
import com.example.android.architecture.blueprints.todoapp.taskdetail.TaskDetailActivity.Companion.EXTRA_TASK_ID
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

/**
 * @author yisuk
 */
@Module
abstract class TaskDetailModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        @ActivityScoped
        fun provideTaskId(activity: TaskDetailActivity): String {
            return activity.intent.getStringExtra(EXTRA_TASK_ID)
        }
    }

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun taskDetailFragment(): TaskDetailFragment

    @ActivityScoped
    @Binds
    abstract fun taskDetailPresenter(presenter: TaskDetailPresenter): TaskDetailContract.Presenter
}