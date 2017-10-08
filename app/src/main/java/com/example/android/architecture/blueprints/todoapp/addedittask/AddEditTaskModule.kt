package com.example.android.architecture.blueprints.todoapp.addedittask

import android.support.annotation.Nullable
import com.example.android.architecture.blueprints.todoapp.di.ActivityScoped
import com.example.android.architecture.blueprints.todoapp.di.FragmentScoped
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a>
 */
@Module
abstract class AddEditTaskModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        @ActivityScoped
        @Nullable
        fun provideTaskId(activity: AddEditTaskActivity): String? {
            return activity.intent.getStringExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID)
        }

        @JvmStatic
        @Provides
        @ActivityScoped
        fun provideStatusDataMissing(activity: AddEditTaskActivity): Boolean {
            return activity.isDataMissing
        }

    }

    @Suppress("unused")
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun addEditTaskFragment(): AddEditTaskFragment

    @Suppress("unused")
    @ActivityScoped
    @Binds
    abstract fun taskPresenter(presenter: AddEditTaskPresenter): AddEditTaskContract.Presenter
}