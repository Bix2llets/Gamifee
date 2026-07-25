package com.example.midtermproject_24125072.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.time.format.DateTimeFormatter
import com.example.midtermproject_24125072.data.MAX_CUP_THRESHOLD
import com.example.midtermproject_24125072.data.RewardEntry
import com.example.midtermproject_24125072.data.UserLoyalty
import com.example.midtermproject_24125072.data.getWorkingDir
import com.example.midtermproject_24125072.data.load
import com.example.midtermproject_24125072.ui.component.LoyaltyCard
import java.time.ZonedDateTime


@Composable
fun RewardsScreen(navHostController: NavHostController) {
  var loyalty = UserLoyalty.load(getWorkingDir() + "/loyalty.json")
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(all = 32.dp)
  ) {

    LoyaltyCard(loyalty.cupBought, MAX_CUP_THRESHOLD)

    Spacer(modifier = Modifier.height(20.dp))

    PointCard(loyalty.loyaltyPoint)

    Spacer(modifier = Modifier.height(20.dp))

    Text("History Reward")

    Spacer(modifier = Modifier.height(20.dp))

    loyalty.rewardHistory.forEach { it -> RewardHistoryCard(it) }


  }
}

@Composable
fun PointCard(point: Int) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(4.dp),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primary
    )
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(end = 12.dp)
    ) {

      Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)

      ) {
        Text("My point", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Text("${point}", style = MaterialTheme.typography.headlineSmall)
      }

      Button(
        onClick = {},
        modifier = Modifier
          .padding(0.dp),

        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
          contentColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Text("Redeem your points", style = MaterialTheme.typography.bodyMedium)
      }
    }
  }
}

@Composable
fun RewardHistoryCard(rewardEntry: RewardEntry) {
  Box {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier
          .weight(1f)
          .horizontalScroll(rememberScrollState())
      ) {
        Column {
          Text(rewardEntry.reason, style = MaterialTheme.typography.bodyLarge)
          Spacer(Modifier.height(4.dp))
          Text(
            rewardEntry.date.format(DateTimeFormatter.ofPattern("MMMM dd yyyy | hh:mm a")),
            style = MaterialTheme.typography.bodySmall
          )
        }
      }

      Text(text = "${rewardEntry.amount} pts", style = MaterialTheme.typography.headlineSmall)
    }

    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .height(2.dp)
        .background(MaterialTheme.colorScheme.outlineVariant)
    )
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewPointCard() {
  PointCard(6767)
}

@Preview(showBackground = true)
@Composable
fun PreviewRewardHistoryCard() {

  val rewardEntries = listOf<RewardEntry>(
    RewardEntry(
      amount = 69,
      date = ZonedDateTime.now(),
      reason = "Testing"
    ), RewardEntry(
      amount = 69,
      date = ZonedDateTime.now(),
      reason = "Testing"
    )
  )
  Column {
    rewardEntries.forEach { it -> RewardHistoryCard(it) }
  }
}
