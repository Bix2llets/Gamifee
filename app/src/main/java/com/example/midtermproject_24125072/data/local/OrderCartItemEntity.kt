package com.example.midtermproject_24125072.data.local

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
  tableName = "order_cart_items",
  primaryKeys = ["orderId", "itemNumber"],
  foreignKeys = [ForeignKey(
    entity = OrderItemEntity::class,
    parentColumns = ["id"],
    childColumns = ["orderId"],
    onDelete = ForeignKey.CASCADE
  )]
)
data class OrderCartItemEntity(
  val orderId: Int,
  val itemNumber: Int,
  val itemId: String,
  val name: String,
  val cost: Double,
  val shotInfo: String,
  val temperature: String,
  val size: String,
  val ice: String,
  val quantity: Int,
  val isChosen: Boolean,
)
