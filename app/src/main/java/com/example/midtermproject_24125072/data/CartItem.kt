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
) {
  fun serialize(): JSONObject {
    val obj = JSONObject()
    obj.put("inCartId", inCartId)
    obj.put("itemId", itemId)
    obj.put("name", name)
    obj.put("cost", cost)
    obj.put("shotInfo", shotInfo)
    obj.put("temperature", temperature)
    obj.put("size", size)
    obj.put("ice", ice)
    obj.put("quantity", quantity)
    return obj
  }

  companion object {
    fun deserialize(data: JSONObject): CartItem {
      return CartItem(
        inCartId = data.optInt("inCartId", 0),
        itemId = data.optString("itemId", ""),
        name = data.getString("name"),
        cost = data.getDouble("cost"),
        shotInfo = data.optString("shotInfo", ""),
        temperature = data.optString("temperature", ""),
        size = data.optString("size", ""),
        ice = data.optString("ice", ""),
        quantity = data.optInt("quantity", 1)
      )
    }
  }
}

fun List<CartItem>.save(fileName: String) {
  val jsonArray = JSONArray()
  forEach { jsonArray.put(it.serialize()) }
  File(fileName).writeText(jsonArray.toString())
}

fun CartItem.Companion.loadList(fileName: String): List<CartItem> {
  val file = File(fileName)
  if (!file.exists()) return emptyList()

  val content = file.readText().trim()
  if (content.isEmpty()) return emptyList()

  val jsonArray = JSONArray(content)
  return (0 until jsonArray.length()).map { CartItem.deserialize(jsonArray.getJSONObject(it)) }
}

