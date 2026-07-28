package com.example.midtermproject_24125072.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
  tableName = "reward_entries",
  foreignKeys = [ForeignKey(
    entity = UserLoyaltyEntity::class,
    parentColumns = ["id"],
    childColumns = ["loyaltyId"],
    onDelete = ForeignKey.CASCADE
  )]
)
data class RewardEntryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val loyaltyId: Long,
  val date: Long,
  val reason: String,
  val amount: Int,
)
