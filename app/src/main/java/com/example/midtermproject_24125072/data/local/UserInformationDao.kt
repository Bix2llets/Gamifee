package com.example.midtermproject_24125072.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInformationDao {
  @Query("SELECT * FROM user_information LIMIT 1")
  fun getUserInfo(): Flow<UserInformationEntity?>

  @Upsert
  suspend fun upsertInfo(info: UserInformationEntity)

  @Query("SELECT COALESCE(MAX(id), 0) + 1 FROM user_information")
  suspend fun nextId(): Long
}
