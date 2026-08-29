package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Vehicle
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY marca ASC, modelo ASC")
    fun getAllVehicles(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE marca LIKE '%' || :query || '%' OR modelo LIKE '%' || :query || '%' ORDER BY marca ASC")
    fun searchVehicles(query: String): Flow<List<Vehicle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vehicles: List<Vehicle>)

    @Update
    suspend fun updateVehicle(vehicle: Vehicle)

    @Query("DELETE FROM vehicles WHERE id = :id")
    suspend fun deleteVehicle(id: Long)

    @Query("DELETE FROM vehicles WHERE TRIM(marca || ' ' || modelo || ' ' || anios) = :name")
    suspend fun deleteVehicleByName(name: String)

    @Query("SELECT COUNT(*) FROM vehicles")
    suspend fun getCount(): Int
}
