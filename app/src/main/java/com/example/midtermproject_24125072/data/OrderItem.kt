package com.example.midtermproject_24125072.data


data class OrderItem (
  val id: Int,
  val address: String,
  val orderList: List<CartItem>,
)