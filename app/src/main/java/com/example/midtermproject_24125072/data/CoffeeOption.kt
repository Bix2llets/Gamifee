package com.example.midtermproject_24125072.data

import org.json.JSONObject

data class CoffeeOption (
  val itemId: String,
  val name: String,
  val cost: Double,
  val shotInfo: String,
  val temperature: String,
  val size: String,
  val ice: String,
) {
  fun serialize(): JSONObject {
    val obj = JSONObject()
    obj.put("itemId", itemId)
    obj.put("name", name)
    obj.put("cost", cost)
    obj.put("shotInfo", shotInfo)
    obj.put("temperature", temperature)
    obj.put("size", size)
    obj.put("ice", ice)
    return obj
  }

  companion object {
    fun deserialize(data: JSONObject): CoffeeOption = CoffeeOption(
      itemId = data.getString("itemId"),
      name = data.getString("name"),
      cost = data.getDouble("cost"),
      shotInfo = data.getString("shotInfo"),
      temperature = data.getString("temperature"),
      size = data.getString("size"),
      ice = data.getString("ice"),
    )
  }
}