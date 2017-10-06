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

//    @Singleton
//    @Binds
//    fun provideTaskDao(context: Context): TasksDao {
//        return ToDoDatabase.getInstance(context.applicationContext).taskDao()
//    }

    @Singleton
    @Binds
    @Remote
    abstract fun provideTasksRemoteDataSource(tasksRemoteDataSource: TasksRemoteDataSource): TasksDataSource

    @Singleton
    @Binds
    @Local
    abstract fun provideTasksLocalDataSource(tasksLocalDataSource: TasksLocalDataSource): TasksDataSource

}