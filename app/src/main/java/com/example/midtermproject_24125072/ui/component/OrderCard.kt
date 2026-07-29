package com.example.midtermproject_24125072.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.midtermproject_24125072.data.CartItem
import com.example.midtermproject_24125072.data.CoffeeOption
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
  val totalCost = order.orderList.fold(-order.discountDollars) { total, value ->
    total + value.option.cost * value.quantity
  }
  var expanded by rememberSaveable { mutableStateOf(false) }
  val rotationAngle by animateFloatAsState(
    targetValue = if (expanded) 180f else 0f,
    animationSpec = tween(durationMillis = 300),
    label = "arrowRotation"
  )

  var showCompleteDialog by rememberSaveable { mutableStateOf(false) }
  var offsetX by rememberSaveable { mutableStateOf(0f) }
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
      text = { Text("Mark order as completed?") },
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
          Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
              Text("${order.address}", style = MaterialTheme.typography.bodyMedium)
            }
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
          ExpandableCartItemList(order.orderList, order.discountDollars)
      }
    }
  }
}

@Composable
fun ExpandableCartItemList(
  items: List<CartItem>,
  discountDollars: Double = 0.0,
  modifier: Modifier = Modifier,
) {
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
            BasicInfoDisplay(item.option.name, item.option.cost, item.quantity)
          }
          if (index < items.lastIndex) {
            Spacer(modifier = Modifier.height(8.dp))
          }
        }

        val totalCost = items.fold(-discountDollars) { total, value ->
          total + value.option.cost * value.quantity
        }

        if (discountDollars > 0) {
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Discount")
            Text(
              "-$${String.format("%.2f", discountDollars)}",
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF4CAF50)
            )
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Total", fontWeight = FontWeight.Bold)
          Text(
            "$${String.format("%.2f", totalCost)}",
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun BasicInfoDisplay(name: String, unitPrice: Double, quantity: Int) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text("${name} x${quantity}")
    Text(
      "$${String.format("%.2f", unitPrice * quantity)}",
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun OrderCardPreview() {
  val mockCartItems = listOf(
    CartItem(
      inCartId = 1,
      option = CoffeeOption(
        itemId = "americano",
        name = "Americano",
        cost = 3.6,
        shotInfo = "Double",
        temperature = "Cold",
        size = "Large",
        ice = "Normal"
      ),
      quantity = 2
    ),
    CartItem(
      inCartId = 2,
      option = CoffeeOption(
        itemId = "latte",
        name = "Latte",
        cost = 4.2,
        shotInfo = "Single",
        temperature = "Hot",
        size = "Medium",
        ice = "No Ice"
      ),
      quantity = 1
    )
  )
  val mockOrder = OrderItem(
    id = 1,
    address = "123 Main St, City",
    orderList = mockCartItems,
    orderTime = ZonedDateTime.now(java.time.ZoneOffset.ofHours(7)),
    discountDollars = 0.0
  )
  OrderCard(order = mockOrder)
}

@Preview(showBackground = true)
@Composable
private fun ExpandableCartItemListPreview() {
  val mockItems = listOf(
    CartItem(
      inCartId = 1,
      option = CoffeeOption(
        itemId = "americano",
        name = "Americano",
        cost = 3.6,
        shotInfo = "Double",
        temperature = "Cold",
        size = "Large",
        ice = "Normal"
      ),
      quantity = 2
    ),
    CartItem(
      inCartId = 2,
      option = CoffeeOption(
        itemId = "latte",
        name = "Latte",
        cost = 4.2,
        shotInfo = "Single",
        temperature = "Hot",
        size = "Medium",
        ice = "No Ice"
      ),
      quantity = 1
    )
  )
  ExpandableCartItemList(items = mockItems)
}
