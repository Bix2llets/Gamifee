package com.example.midtermproject_24125072.data

import org.json.JSONArray
import org.json.JSONObject
import java.time.ZonedDateTime


data class UserLoyalty(
  val cupBought: Int,
  val loyaltyPoint: Int,
  val rewardHistory: List<RewardEntry>,
) {
  fun serialize(): JSONObject {
    var result: JSONObject = JSONObject();
    result.put("cupBought", cupBought)
    result.put("loyaltyPoint", loyaltyPoint)
    var rewardSerialized = JSONArray();
    rewardHistory.forEach { it -> rewardSerialized.put(it.serialize()) }
    result.put("rewardHistory", rewardSerialized)
    return result
  }

  companion object {
    fun deserialize(data: JSONObject): UserLoyalty {
      val rewardArray = data.getJSONArray("rewardHistory")
      val rewards = mutableListOf<RewardEntry>()
      for (i in 0 until rewardArray.length()) {
        rewards.add(RewardEntry.deserialize(rewardArray.getJSONObject(i)))
      }
      return UserLoyalty(
        cupBought = data.getInt("cupBought"),
        loyaltyPoint = data.getInt("loyaltyPoint"),
        rewardHistory = rewards
      )
    }
  }
}

data class RewardEntry(
  val date: ZonedDateTime,
  val reason: String,
  val amount: Int,
) {

  fun serialize(): JSONObject {
    var result = JSONObject();
    result.put("date", date.toString())
    result.put("reason", reason)
    result.put("amount", amount)

    return result;
  }

  companion object {
    fun deserialize(data: JSONObject): RewardEntry = RewardEntry(
      date = ZonedDateTime.parse(data.getString("date")),
      reason = data.getString("reason"),
      amount = data.getInt("amount")
    )
  }
}

const val MAX_CUP_THRESHOLD = 8;

