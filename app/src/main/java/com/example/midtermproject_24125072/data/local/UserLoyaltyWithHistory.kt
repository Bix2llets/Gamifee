package com.example.midtermproject_24125072.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class UserLoyaltyWithHistory(
  @Embedded val loyalty: UserLoyaltyEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "loyaltyId"
  )
  val history: List<RewardEntryEntity>,
)
