package com.example.android.architecture.blueprints.todoapp.di

import android.app.Application
import com.example.android.architecture.blueprints.todoapp.ToDoApp
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepositoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a>
 */

@Singleton
@Component(modules = arrayOf(AppModule::class, AndroidSupportInjectionModule::class, TasksRepositoryModule::class, ActivityBindingModule::class))
interface AppComponent : AndroidInjector<DaggerApplication> {

    fun inject(toDoApp: ToDoApp)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

}