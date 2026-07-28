package com.example.midtermproject_24125072.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserLoyaltyDao {
  @Transaction
  @Query("SELECT * FROM user_loyalty WHERE userId = :userId LIMIT 1")
  fun getLoyalty(userId: Long): Flow<UserLoyaltyWithHistory?>

  @Upsert
  suspend fun upsertLoyalty(loyalty: UserLoyaltyEntity)

  @Insert
  suspend fun insertReward(entry: RewardEntryEntity)

  @Query("SELECT COUNT(*) FROM user_loyalty WHERE userId = :userId")
  suspend fun countForUser(userId: Long): Int
}
