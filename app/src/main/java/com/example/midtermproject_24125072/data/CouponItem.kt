package com.example.midtermproject_24125072.data

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class CouponItem(
  val option: CoffeeOption,
  val point: Int,
  val dbId: Long = 0,
) {
  fun serialize(): JSONObject {
    val obj = JSONObject()
    obj.put("option", option.serialize())
    obj.put("point", point)
    return obj
  }

  companion object {
    fun deserialize(data: JSONObject): CouponItem = CouponItem(
      option = CoffeeOption.deserialize(data.getJSONObject("option")),
      point = data.getInt("point")
    )

    fun loadList(fileName: String): List<CouponItem> {
      val file = File(fileName)
      if (!file.exists()) return emptyList()
      val content = file.readText().trim()
      if (content.isEmpty()) return emptyList()
      val jsonArray = JSONArray(content)
      return (0 until jsonArray.length()).map { deserialize(jsonArray.getJSONObject(it)) }
    }
  }
}

fun List<CouponItem>.save(fileName: String) {
  val jsonArray = JSONArray()
  forEach { jsonArray.put(it.serialize()) }
  File(fileName).writeText(jsonArray.toString())
}

fun CouponItem.toEntity(): com.example.midtermproject_24125072.data.local.CouponItemEntity =
  com.example.midtermproject_24125072.data.local.CouponItemEntity(
    id = dbId,
    itemId = option.itemId, name = option.name, cost = option.cost,
    shotInfo = option.shotInfo, temperature = option.temperature,
    size = option.size, ice = option.ice,
    point = point
  )

fun com.example.midtermproject_24125072.data.local.CouponItemEntity.toDomain(): CouponItem =
  CouponItem(
    dbId = id,
    option = CoffeeOption(itemId, name, cost, shotInfo, temperature, size, ice),
    point = point
  )