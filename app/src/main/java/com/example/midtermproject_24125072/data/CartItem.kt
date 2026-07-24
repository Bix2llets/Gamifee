package com.example.midtermproject_24125072.data

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class CartItem(
  val inCartId: Int,
  val itemId: String,
  val name: String,
  val cost: Double,
  val shotInfo: String,
  val temperature: String,
  val size: String,
  val ice: String,
  val isChosen: Boolean = false,
  val quantity: Int = 1,
)

fun loadCartItem(fileName: String): List<CartItem> {
  val file = File(fileName)
  if (!file.exists()) return emptyList()
  val content = file.readText().trim()
  if (content.isEmpty()) return emptyList()
  val jsonArray =  JSONArray(content)
  val list = mutableListOf<CartItem>()
  for (i in 0 until jsonArray.length()) {
    val obj = jsonArray.getJSONObject(i)
    list.add(
      deserializeCartItem(obj)
    )
  }
  return list
}

fun deserializeCartItem(obj: JSONObject): CartItem = CartItem(
  inCartId = obj.optInt("inCartId", 0),
  itemId = obj.optString("itemId", ""),
  name = obj.getString("name"),
  cost = obj.getDouble("cost"),
  shotInfo = obj.optString("shotInfo", ""),
  temperature = obj.optString("temperature", ""),
  size = obj.optString("size", ""),
  ice = obj.optString("ice", ""),
  quantity = obj.optInt("quantity", 1)
)

fun saveCartItem(fileName: String, data: List<CartItem>) {
  val jsonArray = JSONArray()
  for (item in data) {
    val obj = serializeCartItem(item)
    jsonArray.put(obj)
  }
  File(fileName).writeText(jsonArray.toString())
}

 fun serializeCartItem(item: CartItem): JSONObject {
  val obj = JSONObject()
  obj.put("inCartId", item.inCartId)
  obj.put("itemId", item.itemId)
  obj.put("name", item.name)
  obj.put("cost", item.cost)
  obj.put("shotInfo", item.shotInfo)
  obj.put("temperature", item.temperature)
  obj.put("size", item.size)
  obj.put("ice", item.ice)
  obj.put("quantity", item.quantity)
  return obj
}
