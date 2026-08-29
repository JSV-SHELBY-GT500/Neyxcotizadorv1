package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AutoPart
import kotlinx.coroutines.flow.Flow

@Dao
interface AutoPartDao {
    @Query("SELECT * FROM autoparts ORDER BY nombre ASC")
    fun getAllAutoParts(): Flow<List<AutoPart>>

    @Query("SELECT * FROM autoparts WHERE categoria = :categoria ORDER BY nombre ASC")
    fun getAutoPartsByCategory(categoria: String): Flow<List<AutoPart>>

    @Query("SELECT * FROM autoparts WHERE nombre LIKE '%' || :query || '%' OR numeroParte LIKE '%' || :query || '%' OR categoria LIKE '%' || :query || '%' OR modeloVehiculo LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun searchAutoParts(query: String): Flow<List<AutoPart>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutoPart(autoPart: AutoPart): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(autoParts: List<AutoPart>)

    @Update
    suspend fun updateAutoPart(autoPart: AutoPart)

    @Query("DELETE FROM autoparts WHERE id = :id")
    suspend fun deleteAutoPart(id: Long)

    @Query("SELECT COUNT(*) FROM autoparts")
    suspend fun getCount(): Int
}
