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
package com.example.android.architecture.blueprints.todoapp.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "tasks")
data class Task constructor(
        @PrimaryKey
        @ColumnInfo(name = "entryid") val id: String = UUID.randomUUID().toString(),
        @ColumnInfo(name = "title") val title: String,
        @ColumnInfo(name = "description") val description: String = "no description",
        @ColumnInfo(name = "completed") var completed: Boolean = false
)

{
    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

    val isActive
        get() = !completed

    val isEmpty
        get() = title.isEmpty() && description.isEmpty()
}