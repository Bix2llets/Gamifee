package com.example.midtermproject_24125072.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderItemEntity(
  @PrimaryKey val id: Int,
  val address: String,
  val orderTime: Long,
  val isCompleted: Boolean,
  val discountDollars: Double,
)
