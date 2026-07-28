package com.example.midtermproject_24125072.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {
  @Transaction
  @Query("SELECT * FROM orders ORDER BY orderTime DESC")
  fun getAllOrdersWithItems(): Flow<List<OrderWithItems>>

  @Query("SELECT COALESCE(MAX(id), 0) + 1 FROM orders")
  suspend fun nextOrderId(): Int

  @Insert
  suspend fun insertOrder(order: OrderItemEntity)

  @Insert
  suspend fun insertOrderItems(items: List<OrderCartItemEntity>)

  @Transaction
  suspend fun checkout(order: OrderItemEntity, items: List<OrderCartItemEntity>) {
    insertOrder(order)
    insertOrderItems(items)
  }

  @Query("UPDATE orders SET isCompleted = :completed WHERE id = :orderId")
  suspend fun updateIsCompleted(orderId: Int, completed: Boolean)
}
