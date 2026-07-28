package com.example.midtermproject_24125072.data

import android.content.Context
import com.example.midtermproject_24125072.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

data class CoffeeItem(
  val name: String,
  val price: Double,
  val description: String,
  val imageResId: Int,
  val id: String
) {
  fun serialize(): JSONObject {
    val obj = JSONObject()
    obj.put("id", id)
    obj.put("name", name)
    obj.put("description", description)
    obj.put("price", price)
    return obj
  }

  companion object {
    fun deserialize(data: JSONObject): CoffeeItem {
      val imageResId = getImageResId(data.getString("id"))
      return CoffeeItem(
        id = data.getString("id"),
        name = data.getString("name"),
        description = data.getString("description"),
        imageResId = imageResId,
        price = data.getDouble("price")
      )
    }
  }
}

fun getImageResId(itemName: String): Int = when (itemName) {
  "americano" -> R.drawable.americano
  "cappuchino" -> R.drawable.cappuccino
  "mocha" -> R.drawable.mocha
  "flatwhite" -> R.drawable.flatwhite
  else -> -1
}

fun CoffeeItem.Companion.loadList(context: Context): List<CoffeeItem> {
  val jsonString = try {
    context.assets.open("coffee_items.json")
      .bufferedReader()
      .use { it.readText() }
  } catch (e: IOException) {
    return emptyList()
  }
  val jsonArray = JSONArray(jsonString)
  return (0 until jsonArray.length()).map { CoffeeItem.deserialize(jsonArray.getJSONObject(it)) }
}