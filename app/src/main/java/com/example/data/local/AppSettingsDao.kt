package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: AppSettingsEntity)

    @Query("UPDATE app_settings SET activePresetId = :presetId WHERE id = 1")
    suspend fun updateActivePreset(presetId: String)

    @Query("UPDATE app_settings SET panelsJson = :panelsJson WHERE id = 1")
    suspend fun updatePanelsJson(panelsJson: String)
}
