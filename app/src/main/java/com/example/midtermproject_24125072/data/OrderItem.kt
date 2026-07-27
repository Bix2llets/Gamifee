package com.example.midtermproject_24125072.data

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.ZonedDateTime


data class OrderItem(
  val id: Int,
  val address: String,
  val orderList: List<CartItem>,
  val orderTime: ZonedDateTime,
  val isCompleted: Boolean = false,
  val discountDollars: Double = 0.0,
) {
  fun serialize(): JSONObject {
    val cartArray = JSONArray()
    for (c in orderList) {
      val cartObj = c.serialize()
      cartArray.put(cartObj)
    }
    val obj = JSONObject()
    obj.put("id", id)
    obj.put("address", address)
    obj.put("orderList", cartArray)
    obj.put("orderTime", orderTime.toString())
    obj.put("isCompleted", isCompleted)
    obj.put("discountDollars", discountDollars)
    return obj
  }

  companion object {
    fun create(orderList: List<CartItem>, id: Int, address: String, discountDollars: Double = 0.0): OrderItem = OrderItem(
      id = id,
      address = address,
      orderList = orderList,
      orderTime = ZonedDateTime.now(),
      discountDollars = discountDollars
    )

    fun deserialize(data: JSONObject): OrderItem {
      val cartArray = data.getJSONArray("orderList")
      val cartItems = mutableListOf<CartItem>()
      for (j in 0 until cartArray.length()) {
        val c = cartArray.getJSONObject(j)
        cartItems.add(CartItem.deserialize(c))
      }
      return OrderItem(
        id = data.getInt("id"),
        address = data.getString("address"),
        orderList = cartItems,
        orderTime = ZonedDateTime.parse(data.getString("orderTime")),
        isCompleted = data.optBoolean("isCompleted", false),
        discountDollars = data.optDouble("discountDollars", 0.0)
      )
    }
  }
}

fun List<OrderItem>.save(fileName: String) {
  val jsonArray = JSONArray()
  forEach { jsonArray.put(it.serialize()) }
  File(fileName).writeText(jsonArray.toString())
}

fun OrderItem.Companion.loadList(fileName: String): List<OrderItem> {
  val file = File(fileName)
  if (!file.exists()) return emptyList()
  val content = file.readText().trim()
  if (content.isEmpty()) return emptyList()
  val jsonArray = JSONArray(content)
  return (0 until jsonArray.length()).map { OrderItem.deserialize(jsonArray.getJSONObject(it)) }
}