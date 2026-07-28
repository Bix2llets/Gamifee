package com.example.midtermproject_24125072.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {
  @Query("SELECT * FROM cart_items ORDER BY inCartId")
  fun getAll(): Flow<List<CartItemEntity>>

  @Query("SELECT COALESCE(MAX(inCartId), 0) + 1 FROM cart_items")
  suspend fun nextInCartId(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(item: CartItemEntity)

  @Query("DELETE FROM cart_items WHERE inCartId = :id")
  suspend fun delete(id: Int)

  @Query("DELETE FROM cart_items")
  suspend fun clearAll()
}
