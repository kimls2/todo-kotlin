package com.example.android.architecture.blueprints.todoapp.di

import android.app.Application
import android.arch.persistence.room.Room
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksDao
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * @author yisuk
 */
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkhttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("URL")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    fun provideDb(application: Application): ToDoDatabase {
        return Room.databaseBuilder(application,
                ToDoDatabase::class.java, "Tasks.db")
                .build()
    }

    @Singleton
    @Provides
    fun provideTaskDao(toDoDatabase: ToDoDatabase): TasksDao {
        return toDoDatabase.taskDao()
    }
}