package com.example.android.architecture.blueprints.todoapp.data.source

import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a>
 */
@Module
abstract class TasksRepositoryModule {

    @Suppress("unused")
    @Singleton
    @Binds
    @Remote
    abstract fun provideTasksRemoteDataSource(tasksRemoteDataSource: TasksRemoteDataSource): TasksDataSource

    @Suppress("unused")
    @Singleton
    @Binds
    @Local
    abstract fun provideTasksLocalDataSource(tasksLocalDataSource: TasksLocalDataSource): TasksDataSource

}