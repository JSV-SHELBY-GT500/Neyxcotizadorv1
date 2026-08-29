package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Warranty
import kotlinx.coroutines.flow.Flow

@Dao
interface WarrantyDao {
    @Query("SELECT * FROM warranties ORDER BY timestamp DESC")
    fun getAllWarranties(): Flow<List<Warranty>>

    @Query("SELECT * FROM warranties WHERE id = :id LIMIT 1")
    suspend fun getWarrantyById(id: String): Warranty?

    @Query("SELECT * FROM warranties WHERE orderId = :orderId LIMIT 1")
    suspend fun getWarrantyByOrderId(orderId: String): Warranty?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarranty(warranty: Warranty)

    @Query("DELETE FROM warranties WHERE id = :id")
    suspend fun deleteWarranty(id: String)
}
