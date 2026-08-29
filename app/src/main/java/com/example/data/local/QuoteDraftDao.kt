package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ActiveQuoteDraft
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDraftDao {
    @Query("SELECT * FROM active_quote_draft WHERE id = 1 LIMIT 1")
    fun getDraft(): Flow<ActiveQuoteDraft?>

    @Query("SELECT * FROM active_quote_draft WHERE id = 1 LIMIT 1")
    suspend fun getDraftDirect(): ActiveQuoteDraft?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: ActiveQuoteDraft)

    @Query("DELETE FROM active_quote_draft WHERE id = 1")
    suspend fun deleteDraft()
}
