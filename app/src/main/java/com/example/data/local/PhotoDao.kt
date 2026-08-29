package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PhotoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos ORDER BY timestamp DESC")
    fun getAllPhotos(): Flow<List<PhotoItem>>

    @Query("SELECT * FROM photos WHERE category = :category ORDER BY timestamp DESC")
    fun getPhotosByCategory(category: String): Flow<List<PhotoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(photos: List<PhotoItem>)

    @Update
    suspend fun updatePhoto(photo: PhotoItem)

    @Query("UPDATE photos SET rotationDegrees = :degrees WHERE id = :id")
    suspend fun updateRotation(id: String, degrees: Int)

    @Query("DELETE FROM photos WHERE id = :id")
    suspend fun deletePhoto(id: String)

    @Query("SELECT COUNT(*) FROM photos")
    suspend fun getCount(): Int
}
