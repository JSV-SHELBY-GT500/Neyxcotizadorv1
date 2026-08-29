package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.OcrPreset
import kotlinx.coroutines.flow.Flow

@Dao
interface OcrPresetDao {
    @Query("SELECT * FROM ocr_presets ORDER BY isDefault DESC, name ASC")
    fun getAllPresets(): Flow<List<OcrPreset>>

    @Query("SELECT * FROM ocr_presets WHERE id = :id LIMIT 1")
    suspend fun getPresetById(id: String): OcrPreset?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: OcrPreset)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(presets: List<OcrPreset>)

    @Query("DELETE FROM ocr_presets WHERE id = :id")
    suspend fun deletePreset(id: String)
}
