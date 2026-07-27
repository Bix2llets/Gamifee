package com.example.midtermproject_24125072.data

import org.json.JSONObject
import java.io.File

data class UserInformation(
  val name: String,
  val address: String,
  val phoneNumber: String,
  val email: String,
  val haveAvatar: Boolean
) {
  fun serialize(): JSONObject {
    val obj = JSONObject()
    obj.put("name", name)
    obj.put("address", address)
    obj.put("phoneNumber", phoneNumber)
    obj.put("email", email)
    obj.put("haveAvatar", haveAvatar)
    return obj
  }

  fun save(location: String) {
    File(location).writeText(serialize().toString())
  }

  companion object {
    fun deserialize(data: JSONObject): UserInformation = UserInformation(
      name = data.optString("name", ""),
      address = data.optString("address", ""),
      phoneNumber = data.optString("phoneNumber", ""),
      email = data.optString("email", ""),
      haveAvatar = data.optBoolean("haveAvatar", false)
    )

    fun load(location: String): UserInformation {
      val file = File(location)
      if (!file.exists()) return UserInformation("", "", "", "", false)
      val content = file.readText().trim()
      if (content.isEmpty()) return UserInformation("", "", "", "", false)
      return deserialize(JSONObject(content))
    }
  }
}
