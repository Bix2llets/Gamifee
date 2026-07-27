package com.example.midtermproject_24125072.data

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.ZonedDateTime
import kotlin.math.min
import kotlin.math.roundToInt
import java.util.Random


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
    val OFFSET = 2
    val INTERVAL = 0.5
    var newCups = numberOfCups + cupBought
    Log.d("AddCupBought", "$newCups = $numberOfCups + $cupBought")

    var ptsGainFromPayment = 0
    val validPortion = totalCost - OFFSET;
    if (validPortion <= 0) ptsGainFromPayment = 0;
    else ptsGainFromPayment = (validPortion / INTERVAL).toInt() + 1
    var result = UserLoyalty(
      cupBought = newCups,
      loyaltyPoint = loyaltyPoint,
      rewardHistory = rewardHistory
    )
    if (ptsGainFromPayment > 0) {
      val purchaseCompletionEntry = RewardEntry(
        reason = "Purchase completion",
        date = ZonedDateTime.now(),
        amount = ptsGainFromPayment * 10
      )
      result = result.addRewardHistory(purchaseCompletionEntry)
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

  fun isEnoughForRedeemPoint() : Boolean {
    return cupBought >= MAX_CUP_THRESHOLD
  }

  fun addRedeemPoint() : UserLoyalty {
    if (!isEnoughForRedeemPoint()) return this
    val points = generateRedeemPoints()
    return copy(cupBought = cupBought - MAX_CUP_THRESHOLD).addRewardHistory(RewardEntry(
      date = ZonedDateTime.now(),
      reason = "Redeemed $MAX_CUP_THRESHOLD cups",
      amount = points
    ))
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

data class DiscountInfo(
  val discountDollars: Double,
  val pointsToDeduct: Int,
)

fun calculateDiscount(orderTotal: Double, availablePoints: Int): DiscountInfo? {
  val maxDiscount = orderTotal * DISCOUNT_PERCENT_CAP
  if (maxDiscount < MIN_DISCOUNT_DOLLARS) return null

  val maxPoints = (maxDiscount * POINT_TO_DOLLAR_RATIO).toInt()
  val pointsToUse = min(maxPoints, availablePoints)
  val discountRounded = (pointsToUse.toDouble() / POINT_TO_DOLLAR_RATIO * 100).toLong() / 100.0

  if (discountRounded < MIN_DISCOUNT_DOLLARS) return null
  return DiscountInfo(discountRounded, pointsToUse)
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

fun generateRedeemPoints(): Int {
    val gaussian = Random().nextGaussian()
    return (gaussian * 50.0 + 400.0).roundToInt().coerceIn(200, 600)
}

const val MAX_CUP_THRESHOLD = 8;
const val POINT_TO_DOLLAR_RATIO = 100
const val DISCOUNT_PERCENT_CAP = 0.20
const val MIN_DISCOUNT_DOLLARS = 0.50

