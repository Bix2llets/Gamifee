package com.example.midtermproject_24125072.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.midtermproject_24125072.data.CartItem
import com.example.midtermproject_24125072.data.OrderItem
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


@Composable
fun OrderCard(
  order: OrderItem,
  onSwipeComplete: () -> Unit = {},
  enableSwipe: Boolean = true,
) {
  val totalCost = order.orderList.fold(0.0) { total, value ->
    value.cost + total
  }
  var expanded by remember { mutableStateOf(false) }
  val rotationAngle by animateFloatAsState(
    targetValue = if (expanded) 180f else -90f,
    animationSpec = tween(durationMillis = 300),
    label = "arrowRotation"
  )

  var showCompleteDialog by remember { mutableStateOf(false) }
  var offsetX by remember { mutableStateOf(0f) }
  var cardWidth by remember { mutableStateOf(0) }
  val thresholdFraction = 0.3f
  val completeThreshold = cardWidth * thresholdFraction

  if (showCompleteDialog) {
    AlertDialog(
      onDismissRequest = {
        showCompleteDialog = false
        offsetX = 0f
      },
      title = { Text("Mark as completed") },
      text = { Text("Mark order from ${order.address} as completed?") },
      dismissButton = {
        TextButton(onClick = {
          showCompleteDialog = false
          offsetX = 0f
        }) {
          Text("Cancel")
        }
      },
      confirmButton = {
        TextButton(onClick = {
          showCompleteDialog = false
          offsetX = 0f
          onSwipeComplete()
        }) {
          Text("Complete")
        }
      }
    )
  }

  val animatedOffsetX by animateFloatAsState(
    targetValue = offsetX,
    label = "swipeOffset"
  )

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .onSizeChanged { cardWidth = it.width }
  ) {
    if (animatedOffsetX > 0f) {
      Box(
        modifier = Modifier
          .matchParentSize()
          .background(Color(0xFF4CAF50))
          .padding(start = 16.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        Icon(
          Icons.Default.CheckCircle,
          contentDescription = "Mark as completed",
          tint = Color.White,
          modifier = Modifier.size(28.dp)
        )
      }
    }

    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
        .then(
          if (enableSwipe) {
            Modifier.pointerInput(Unit) {
              detectHorizontalDragGestures(
                onDragEnd = {
                  if (offsetX > completeThreshold) {
                    showCompleteDialog = true
                  } else {
                    offsetX = 0f
                  }
                },
                onDragCancel = { offsetX = 0f }
              ) { _, dragAmount ->
                offsetX = (offsetX + dragAmount).coerceIn(0f, cardWidth.toFloat())
              }
            }
          } else Modifier
        ),
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 2.dp,
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      ) {
        var timeFormatter = DateTimeFormatter.ofPattern("MMMM dd | hh:mm a")
        val formattedTime = timeFormatter.format(order.orderTime)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
        ) {
          Column {
            Text("${order.address}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text("${formattedTime}", style = MaterialTheme.typography.bodySmall)
          }
          Row(verticalAlignment = Alignment.CenterVertically) {

            Text("$%.2f".format(totalCost), style = MaterialTheme.typography.bodyLarge)
            Icon(
              imageVector = Icons.Default.KeyboardArrowDown,
              contentDescription = if (expanded) "Collapse order list" else "Expand order list",
              modifier = Modifier
                .size(24.dp)
                .rotate(rotationAngle),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
        if (expanded)
          ExpandableCartItemList(order.orderList)
      }
    }
  }
}

@Composable
fun ExpandableCartItemList(
  items: List<CartItem>,
  modifier: Modifier = Modifier,
) {

  val content = buildAnnotatedString {
    withBulletList {
      items.map { it -> withBulletListItem { append("${it.name} : $${it.cost}x${it.quantity}") } }
    }
  }
  Card(
    modifier = modifier
      .fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Top,
      modifier = Modifier.fillMaxWidth()
    )
    {
      Column(modifier = Modifier.padding(12.dp)) {

        items.forEachIndexed { index, item ->
          Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = item.name,
              style = MaterialTheme.typography.bodyLarge,
              fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "$${String.format("%.2f", item.cost)}",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "${item.quantity} cups",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          if (index < items.lastIndex) {
            Spacer(modifier = Modifier.height(8.dp))
          }
        }

        Spacer(modifier = Modifier.height(4.dp))
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun OrderCardPreview() {
  val mockCartItems = listOf(
    CartItem(
      inCartId = 1,
      itemId = "americano",
      name = "Americano",
      cost = 3.6,
      shotInfo = "Double",
      temperature = "Cold",
      size = "Large",
      ice = "Normal",
      quantity = 2
    ),
    CartItem(
      inCartId = 2,
      itemId = "latte",
      name = "Latte",
      cost = 4.2,
      shotInfo = "Single",
      temperature = "Hot",
      size = "Medium",
      ice = "No Ice",
      quantity = 1
    )
  )
  val mockOrder = OrderItem(
    id = 1,
    address = "123 Main St, City",
    orderList = mockCartItems,
    orderTime = ZonedDateTime.now()
  )
  OrderCard(order = mockOrder)
}

@Preview(showBackground = true)
@Composable
private fun ExpandableCartItemListPreview() {
  val mockItems = listOf(
    CartItem(
      inCartId = 1,
      itemId = "americano",
      name = "Americano",
      cost = 3.6,
      shotInfo = "Double",
      temperature = "Cold",
      size = "Large",
      ice = "Normal",
      quantity = 2
    ),
    CartItem(
      inCartId = 2,
      itemId = "latte",
      name = "Latte",
      cost = 4.2,
      shotInfo = "Single",
      temperature = "Hot",
      size = "Medium",
      ice = "No Ice",
      quantity = 1
    )
  )
  ExpandableCartItemList(items = mockItems)
}
