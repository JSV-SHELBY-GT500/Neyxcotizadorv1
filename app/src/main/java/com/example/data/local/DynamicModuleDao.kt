package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DynamicModule
import kotlinx.coroutines.flow.Flow

@Dao
interface DynamicModuleDao {
    @Query("SELECT * FROM dynamic_modules ORDER BY category ASC, name ASC")
    fun getAllModules(): Flow<List<DynamicModule>>

    @Query("SELECT * FROM dynamic_modules WHERE isEnabled = 1 AND isInstalled = 1")
    fun getEnabledModules(): Flow<List<DynamicModule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: DynamicModule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(modules: List<DynamicModule>)

    @Query("UPDATE dynamic_modules SET isEnabled = :enabled WHERE id = :id")
    suspend fun setModuleEnabled(id: String, enabled: Boolean)

    @Query("UPDATE dynamic_modules SET isInstalled = :installed, isEnabled = :installed WHERE id = :id")
    suspend fun setModuleInstalled(id: String, installed: Boolean)

    @Query("UPDATE dynamic_modules SET lastSync = :lastSync, version = :version WHERE id = :id")
    suspend fun updateModuleMetadata(id: String, lastSync: String, version: String)

    @Query("DELETE FROM dynamic_modules WHERE id = :id")
    suspend fun deleteModule(id: String)
}
