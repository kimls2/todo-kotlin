package com.example.android.architecture.blueprints.todoapp

import com.example.android.architecture.blueprints.todoapp.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a>
 */
class ToDoApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build().also { it.inject(this) }
    }
}