package com.example.midtermproject_24125072.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithItems(
  @Embedded val order: OrderItemEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "orderId"
  )
  val items: List<OrderCartItemEntity>,
)
