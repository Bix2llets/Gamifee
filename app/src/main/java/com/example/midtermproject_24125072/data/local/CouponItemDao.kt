package com.example.midtermproject_24125072.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CouponItemDao {
  @Query("SELECT * FROM coupons")
  fun getAll(): Flow<List<CouponItemEntity>>

  @Query("SELECT COUNT(*) FROM coupons")
  fun getCount(): Flow<Int>

  @Insert
  suspend fun insert(coupon: CouponItemEntity)

  @Query("DELETE FROM coupons WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("DELETE FROM coupons")
  suspend fun deleteAll()
}
