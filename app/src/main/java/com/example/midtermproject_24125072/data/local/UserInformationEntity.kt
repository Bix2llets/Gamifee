package com.example.midtermproject_24125072.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_information")
data class UserInformationEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val address: String,
  val phoneNumber: String,
  val email: String,
  val haveAvatar: Boolean,
)
