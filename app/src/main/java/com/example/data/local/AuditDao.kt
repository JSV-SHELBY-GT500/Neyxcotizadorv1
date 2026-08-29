package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AuditLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 100): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_logs WHERE categoria = :categoria ORDER BY timestamp DESC")
    fun getLogsByCategory(categoria: String): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_logs WHERE severidad = :severidad ORDER BY timestamp DESC")
    fun getLogsBySeverity(severidad: String): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_logs WHERE accion LIKE '%' || :query || '%' OR entidad LIKE '%' || :query || '%' OR detalles LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchLogs(query: String): Flow<List<AuditLog>>

    @Query("SELECT COUNT(*) FROM audit_logs")
    fun getTotalLogsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM audit_logs WHERE categoria = 'MEMORIA_LOCAL'")
    fun getMemoryAccessCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<AuditLog>)

    @Query("DELETE FROM audit_logs WHERE id NOT IN (SELECT id FROM audit_logs ORDER BY timestamp DESC LIMIT 300)")
    suspend fun trimLogs()

    @Query("DELETE FROM audit_logs")
    suspend fun clearAllLogs()
}
