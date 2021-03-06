/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.example.unsplash.dataBase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.unsplash.dataBase.contracts.RemoteKeyContract
import com.example.unsplash.dataBase.dataBaseEssences.remoteKeys.RemoteKey

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: RemoteKey)

    @Query("SELECT * FROM ${RemoteKeyContract.TABLE_NAME} WHERE ${RemoteKeyContract.Columns.MARKER} = :marker")
    suspend fun remoteKeyByMarker(marker: String): RemoteKey?

    @Query("DELETE FROM ${RemoteKeyContract.TABLE_NAME} WHERE ${RemoteKeyContract.Columns.MARKER} = :marker")
    suspend fun deleteByMarker(marker: String)

    @Query("DELETE FROM ${RemoteKeyContract.TABLE_NAME}")
    fun clearAll()
}