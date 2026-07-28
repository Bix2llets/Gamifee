package com.example.midtermproject_24125072.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.midtermproject_24125072.data.CartItem
import com.example.midtermproject_24125072.data.CoffeeItem
import com.example.midtermproject_24125072.data.CoffeeOption
import com.example.midtermproject_24125072.data.CouponItem
import com.example.midtermproject_24125072.data.POINT_TO_DOLLAR_RATIO
import com.example.midtermproject_24125072.data.RewardEntry
import com.example.midtermproject_24125072.data.UserLoyalty
import com.example.midtermproject_24125072.data.loadList
import com.example.midtermproject_24125072.data.local.AppDatabase
import com.example.midtermproject_24125072.data.toDomain
import com.example.midtermproject_24125072.data.toEntity
import com.example.midtermproject_24125072.ui.component.CouponCard
import com.example.midtermproject_24125072.ui.util.LocalIsLandscape
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.Random
import kotlin.math.roundToInt

private const val CHECK_INTERVAL_MS = 1000L
private const val GENERATION_COUNT = 3
private const val FLAT_ADD_ON = 2.0
private const val SIGMA =
  0.5           // Standard deviation for price adjustment (N(0, sigma^2)); adjustable, non-negative
private const val TRIGGER_SECOND =
  0    // Second value (0-59) for coupon generation trigger; adjustable for demo
private const val MAX_COUPONS =
  12      // Maximum number of coupons displayed; oldest are purged when new ones arrive

@Composable
fun RedeemScreen(navController: NavController) {
  val context = LocalContext.current
  val database = remember { AppDatabase.getInstance(context) }
  val scope = rememberCoroutineScope()
  val coffeeList = remember { CoffeeItem.loadList(context) }
  val isLandscape = LocalIsLandscape.current

  var userLoyalty by remember { mutableStateOf(UserLoyalty(0, 0, emptyList())) }
  var redeemingCoupon by remember { mutableStateOf<CouponItem?>(null) }
  var remainingSeconds by remember { mutableStateOf(0) }

  val couponEntities by database.couponItemDao().getAll().collectAsState(initial = emptyList())
  var coupons by remember(couponEntities) {
    mutableStateOf(couponEntities.map { it.toDomain() }.toMutableList())
  }

  val userInfoEntity by database.userInformationDao().getUserInfo().collectAsState(initial = null)
  val userId = userInfoEntity?.id
  LaunchedEffect(userId) {
    if (userId != null) {
      database.userLoyaltyDao().getLoyalty(userId).collect { withHistory ->
        if (withHistory != null) {
          userLoyalty = withHistory.loyalty.toDomain(withHistory.history)
        }
      }
    }
  }

  val rand = remember { Random() }

  LaunchedEffect(Unit) {
    if (database.couponItemDao().getAll().first().isEmpty()) {
      val newCoupons = (1..GENERATION_COUNT).map { generateRandomCoupon(coffeeList, rand) }
      newCoupons.forEach { database.couponItemDao().insert(it.toEntity()) }
    }
  }

  LaunchedEffect(Unit) {
    var lastGenerationTick = (ZonedDateTime.now().hour * 60 + ZonedDateTime.now().minute) / 5
    var lastGenerationDay = ZonedDateTime.now().dayOfYear

    while (true) {
      kotlinx.coroutines.delay(CHECK_INTERVAL_MS)
      val current = ZonedDateTime.now()
      val currentDay = current.dayOfYear
      val minuteOfDay = current.hour * 60 + current.minute
      val tick = minuteOfDay / 5

      val currentSecOfDay = current.hour * 3600 + current.minute * 60 + current.second
      val bucketSize = 5 * 60
      val bucketStart = (currentSecOfDay / bucketSize) * bucketSize
      val nextTrigger = bucketStart + TRIGGER_SECOND
      remainingSeconds = if (currentSecOfDay < nextTrigger)
        nextTrigger - currentSecOfDay
      else
        bucketStart + bucketSize + TRIGGER_SECOND - currentSecOfDay

      if (currentDay != lastGenerationDay) {
        database.couponItemDao().deleteAll()
        lastGenerationDay = currentDay
        lastGenerationTick = -1
      }

      if (tick != lastGenerationTick && current.second == TRIGGER_SECOND && minuteOfDay % 5 == 0) {
        val newCoupons = (1..GENERATION_COUNT).map { generateRandomCoupon(coffeeList, rand) }
        newCoupons.forEach { database.couponItemDao().insert(it.toEntity()) }
        lastGenerationTick = tick
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(all = if (isLandscape) 16.dp else 32.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
    ) {
      IconButton(
        onClick = { navController.popBackStack() },
        modifier = Modifier.align(Alignment.CenterStart)
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
      }
      Text(
        text = "Redeem reward",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.align(Alignment.Center)
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.Start
    ) {
      Text(
        text = "Your points: ${userLoyalty.loyaltyPoint}",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )

      Text(
        text = "Next refresh in ${remainingSeconds / 60}:${
          String.format(
            "%02d",
            remainingSeconds % 60
          )
        }",
        style = MaterialTheme.typography.bodySmall,
      )
    }


    Spacer(modifier = Modifier.height(16.dp))

    if (coupons.isEmpty()) {
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "No coupons available. Wait for the next generation cycle.",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    } else {
      Column(
        modifier = Modifier
          .weight(1f)
          .verticalScroll(rememberScrollState())
      ) {
        coupons.forEach { coupon ->
          CouponCard(
            coupon = coupon,
            onRedeem = {
              if (userLoyalty.loyaltyPoint >= coupon.point) {
                redeemingCoupon = coupon
              }
            }
          )
          Spacer(modifier = Modifier.height(12.dp))
        }
      }
    }


    redeemingCoupon?.let { coupon ->
      AlertDialog(
        onDismissRequest = { redeemingCoupon = null },
        title = { Text("Redeem Coupon") },
        text = {
          Text("After conversion, the point cannot be refunded at any case. Do you want to proceed?")
        },
        dismissButton = {
          TextButton(onClick = {
            val entry = RewardEntry(
              amount = -coupon.point,
              reason = "Coupon conversion",
              date = ZonedDateTime.now()
            )
            val updatedLoyalty = userLoyalty.addRewardHistory(entry)
            scope.launch {
              if (userInfoEntity != null) {
                database.userLoyaltyDao()
                  .upsertLoyalty(updatedLoyalty.toEntity())
                database.userLoyaltyDao().insertReward(entry.toEntity(updatedLoyalty.dbId))
                database.couponItemDao().deleteById(coupon.dbId)
                val newId = database.cartItemDao().nextInCartId()
                val cartItem = CartItem(
                  inCartId = newId,
                  option = coupon.option.copy(cost = 0.0),
                  isChosen = false, quantity = 1
                )
                database.cartItemDao().insert(cartItem.toEntity())
              }
            }
            coupons = coupons.filter { it.dbId != coupon.dbId }.toMutableList()
            userLoyalty = updatedLoyalty
            redeemingCoupon = null
          }) {
            Text("Proceed")
          }
        },
        confirmButton = {
          TextButton(onClick = { redeemingCoupon = null }) {
            Text("Cancel")
          }
        }
      )
    }
  }
}

private fun generateRandomCoupon(
  coffeeList: List<CoffeeItem>,
  rand: Random
): CouponItem {
  val coffee = coffeeList[rand.nextInt(coffeeList.size)]
  val shot = if (rand.nextBoolean()) "Single" else "Double"

  val tempIceRoll = rand.nextInt(4)
  val temperature: String
  val ice: String
  when (tempIceRoll) {
    0 -> {
      temperature = "Hot"; ice = "N/A"
    }

    1 -> {
      temperature = "Cold"; ice = "Less"
    }

    2 -> {
      temperature = "Cold"; ice = "Normal"
    }

    else -> {
      temperature = "Cold"; ice = "More"
    }
  }

  val sizeRoll = rand.nextInt(3)
  val size = when (sizeRoll) {
    0 -> "Small"
    1 -> "Medium"
    else -> "Large"
  }

  val adjustment = rand.nextGaussian() * SIGMA
  val price = coffee.price + FLAT_ADD_ON + adjustment
  val points = (price * POINT_TO_DOLLAR_RATIO).roundToInt().coerceAtLeast(1)

  val option = CoffeeOption(
    itemId = coffee.id,
    name = coffee.name,
    cost = 0.0,
    shotInfo = shot,
    temperature = temperature,
    size = size,
    ice = ice
  )

  return CouponItem(option = option, point = points)
}
