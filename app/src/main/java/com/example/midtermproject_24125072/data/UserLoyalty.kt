package com.example.midtermproject_24125072.data

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
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

  fun addCupBought(numberOfCups: Int, totalCost: Double): UserLoyalty {
    val OFFSET = 5
    val INTERVAL = 1
    var newCups = numberOfCups + cupBought
    Log.d("AddCupBought", "$newCups = $numberOfCups + $cupBought")
    val overflowTimes = (newCups / MAX_CUP_THRESHOLD).toInt()
    newCups = newCups % MAX_CUP_THRESHOLD
    Log.d("AddCupBought", "$newCups $overflowTimes")

    var ptsGainFromPayment = 0
    val validPortion = totalCost - OFFSET;
    if (validPortion <= 0) ptsGainFromPayment = 0;
    else ptsGainFromPayment = (validPortion / INTERVAL).toInt()
    var result = UserLoyalty(
      cupBought = newCups,
      loyaltyPoint = loyaltyPoint,
      rewardHistory = rewardHistory
    )
    if (ptsGainFromPayment > 0) {
      val purchaseCompletionEntry = RewardEntry(
        reason = "Purchase completion",
        date = ZonedDateTime.now(),
        amount = ptsGainFromPayment
      )
      result = result.addRewardHistory(purchaseCompletionEntry)
    }
    if (overflowTimes != 0) {
      val overflowCompletionEntry = RewardEntry(
        reason = "${MAX_CUP_THRESHOLD} cups reward",
        amount = (overflowTimes * 0.1 * POINT_TO_DOLLAR_RATIO).toInt(),
        date = ZonedDateTime.now()
      )
      result = result.addRewardHistory(overflowCompletionEntry)
    }
    return result
  }

  fun addRewardHistory(entry: RewardEntry): UserLoyalty {
    return UserLoyalty(
      cupBought = cupBought,
      loyaltyPoint = loyaltyPoint + entry.amount,
      rewardHistory = rewardHistory + entry
    )
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

fun UserLoyalty.save(fileName: String) {
  val jsonArray = serialize()
  File(fileName).writeText(jsonArray.toString())
}

fun UserLoyalty.Companion.load(fileName: String): UserLoyalty {

  val file = File(fileName)
  if (!file.exists()) return UserLoyalty(0, 0, emptyList<RewardEntry>())
  val content = file.readText().trim()
  if (content.isEmpty()) return UserLoyalty(0, 0, emptyList<RewardEntry>())
  val data = JSONObject(content)
  return UserLoyalty.deserialize(data)
}

const val MAX_CUP_THRESHOLD = 8;
const val POINT_TO_DOLLAR_RATIO = 100

