package com.example.midtermproject_24125072.data

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime


data class OrderItem(
  val id: Int,
  val address: String,
  val orderList: List<CartItem>,
  val orderTime: ZonedDateTime,
  val isCompleted: Boolean = false,
)

fun loadOrderItem(fileName: String): List<OrderItem> {
  val file = File(fileName)
  if (!file.exists()) return emptyList()
  val content = file.readText().trim()
  if (content.isEmpty()) return emptyList()
  val jsonArray = JSONArray(content)
  val list = mutableListOf<OrderItem>()
  for (i in 0 until jsonArray.length()) {
    val obj = jsonArray.getJSONObject(i)
    val cartArray = obj.getJSONArray("orderList")
    val cartItems = mutableListOf<CartItem>()
    for (j in 0 until cartArray.length()) {
      val c = cartArray.getJSONObject(j)
      cartItems.add(deserializeCartItem(c))
    }
    list.add(
      OrderItem(
        id = obj.getInt("id"),
        address = obj.getString("address"),
        orderList = cartItems,
        orderTime = ZonedDateTime.parse(obj.getString("orderTime")),
        isCompleted = obj.optBoolean("isCompleted", false)
      )
    )
  }
  return list
}

fun saveOrderItem(fileName: String, data: List<OrderItem>) {
  val jsonArray = JSONArray()
  for (item in data) {
    val cartArray = JSONArray()
    for (c in item.orderList) {
      val cartObj = serializeCartItem(c)
      cartArray.put(cartObj)
    }
    val obj = JSONObject()
    obj.put("id", item.id)
    obj.put("address", item.address)
    obj.put("orderList", cartArray)
    obj.put("orderTime", item.orderTime.toString())
    obj.put("isCompleted", item.isCompleted)
    jsonArray.put(obj)
  }
  File(fileName).writeText(jsonArray.toString())
}

fun createOrderItem(orderList: List<CartItem>, id: Int, address: String): OrderItem {
  val currentTime = ZonedDateTime.now()
  val result = OrderItem(
    id = id,
    address = address,
    orderList = orderList,
    orderTime = currentTime
  )
  return result


}