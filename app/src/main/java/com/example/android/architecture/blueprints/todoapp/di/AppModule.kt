package com.example.android.architecture.blueprints.todoapp.di

import dagger.Module

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a>
 */
@Module(includes = arrayOf(NetworkModule::class))
abstract class AppModule {

//    @Binds
//    abstract fun bindContext(application: Application): Context


}