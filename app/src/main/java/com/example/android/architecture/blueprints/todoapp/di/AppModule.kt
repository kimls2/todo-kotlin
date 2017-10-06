package com.example.android.architecture.blueprints.todoapp.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a>
 */
@Module
abstract class AppModule {

    @Binds
    abstract fun bindContext(application: Application): Context


}