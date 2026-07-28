package com.example.midtermproject_24125072.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
  tableName = "user_loyalty",
  foreignKeys = [ForeignKey(
    entity = UserInformationEntity::class,
    parentColumns = ["id"],
    childColumns = ["userId"],
    onDelete = ForeignKey.CASCADE
  )]
)
data class UserLoyaltyEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val userId: Long,
  val cupBought: Int,
  val loyaltyPoint: Int,
)
