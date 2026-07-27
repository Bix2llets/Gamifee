package com.example.midtermproject_24125072.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import com.example.midtermproject_24125072.data.MAX_CUP_THRESHOLD

@Composable
fun LoyaltyCard(
  bought: Int,
  total: Int,
  modifier: Modifier = Modifier,
  vertical: Boolean = false,
  onRedeemClick: (() -> Unit)? = null,
) {
  val isRedeemable = bought >= total && onRedeemClick != null

  val infiniteTransition = rememberInfiniteTransition()
  val scale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.03f,
    animationSpec = infiniteRepeatable(
      animation = tween(600),
      repeatMode = RepeatMode.Reverse
    )
  )

  Column(
    modifier = if (vertical) modifier.fillMaxHeight() else modifier.fillMaxWidth()
  ) {
    val cardModifier = if (isRedeemable) {
      (if (vertical) Modifier else Modifier.fillMaxWidth())
        .graphicsLayer(scaleX = scale, scaleY = scale)
        .clickable { onRedeemClick?.invoke() }
    } else {
      if (vertical) Modifier else Modifier.fillMaxWidth()
    }

    if (vertical) {
      Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Column(
          modifier = Modifier
            .padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Loyalty",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "$bought / $total",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary
          )
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            modifier = Modifier
              .width(36.dp)
              .fillMaxHeight(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
          ) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
              verticalArrangement = Arrangement.SpaceEvenly,
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              repeat(total) { index ->
                Box(modifier = Modifier.size(20.dp)) {
                  Icon(
                    imageVector = Icons.Outlined.Coffee,
                    contentDescription = "$bought / $total",
                    tint = if (index < bought)
                      MaterialTheme.colorScheme.onPrimary
                    else
                      MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                  )
                }
              }
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
        }
      }
    } else {
      Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Loyalty",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
              text = "$bought / $total",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onPrimary
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .height(36.dp),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
          ) {
            Row(
              modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.CenterVertically
            ) {
              repeat(total) { index ->
                Box(
                  modifier = Modifier
                    .size(20.dp)
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Coffee,
                    contentDescription = "$bought / $total",
                    tint = if (index < bought)
                      MaterialTheme.colorScheme.onPrimary
                    else
                      MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun HorizontalLoyalty() {
  LoyaltyCard(bought = 8, total = MAX_CUP_THRESHOLD, Modifier, false)
}

@Preview(showBackground = true)
@Composable
fun VerticalLoyalty() {
  LoyaltyCard(bought = 8, total = MAX_CUP_THRESHOLD, Modifier, true)
}
