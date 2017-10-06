/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.data.source.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.android.architecture.blueprints.todoapp.data.Task

/**
 * The Room Database that contains the Task table.
 */
@Database(entities = arrayOf(Task::class), version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun taskDao(): TasksDao

    companion object {

        private var INSTANCE: ToDoDatabase? = null

        private val sLock = Any()

        @JvmStatic
        fun getInstance(context: Context): ToDoDatabase {
            synchronized(sLock) {
                return INSTANCE ?: Room.databaseBuilder(context.applicationContext,
                        ToDoDatabase::class.java, "Tasks.db")
                        .build().apply {
                    INSTANCE = this
                }

            }
        }
    }
}