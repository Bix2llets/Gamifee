package com.example.midtermproject_24125072.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
  @PrimaryKey val inCartId: Int,
  val itemId: String,
  val name: String,
  val cost: Double,
  val shotInfo: String,
  val temperature: String,
  val size: String,
  val ice: String,
  val isChosen: Boolean,
  val quantity: Int,
)
