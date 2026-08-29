package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: String): Order?

    @Query("SELECT * FROM orders WHERE estado = :estado ORDER BY timestamp DESC")
    fun getOrdersByStatus(estado: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id LIKE '%' || :query || '%' OR clienteNombre LIKE '%' || :query || '%' OR vehiculo LIKE '%' || :query || '%' OR pieza LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchOrders(query: String): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Query("UPDATE orders SET estado = :status, fechaActualizacion = :updatedDate, guiaRastreo = :tracking, observaciones = :notes WHERE id = :id")
    suspend fun updateOrderStatus(id: String, status: String, updatedDate: String, tracking: String, notes: String)

    @Query("DELETE FROM orders WHERE id = :id")
    suspend fun deleteOrder(id: String)

    @Query("SELECT COUNT(*) FROM orders")
    fun getTotalOrdersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM orders WHERE estado = 'entregado'")
    fun getDeliveredCount(): Flow<Int>
}
