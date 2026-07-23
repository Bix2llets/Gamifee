package com.example.midtermproject_24125072.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.midtermproject_24125072.R
import com.example.midtermproject_24125072.data.CartItem
import com.example.midtermproject_24125072.data.getImageResId
import kotlin.math.roundToInt

@Composable
fun CartItemCard(
  item: CartItem,
  onDelete: () -> Unit,
  onToggleChosen: () -> Unit,
  onQuantityChange: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var showDeleteDialog by remember { mutableStateOf(false) }
  var offsetX by remember { mutableStateOf(0f) }
  var cardWidth by remember { mutableStateOf(0) }
  val thresholdFraction = 0.25f
  val deleteThreshold = cardWidth * thresholdFraction

  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = {
        showDeleteDialog = false
        offsetX = 0f
      },
      title = { Text("Remove item") },
      text = { Text("Remove ${item.name} from your cart?") },
      confirmButton = {
        TextButton(onClick = {
          showDeleteDialog = false
          offsetX = 0f
          onDelete()
        }) {
          Text("Remove")
        }
      },
      dismissButton = {
        TextButton(onClick = {
          showDeleteDialog = false
          offsetX = 0f
        }) {
          Text("Cancel")
        }
      }
    )
  }

  val animatedOffsetX by animateFloatAsState(
    targetValue = offsetX,
    label = "swipeOffset"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .onSizeChanged { cardWidth = it.width }
  ) {
    if (animatedOffsetX < 0f) {
      Box(
        modifier = Modifier
          .matchParentSize()
          .background(Color(0xFFE53935))
          .padding(end = 16.dp),
        contentAlignment = Alignment.CenterEnd
      ) {
        Icon(
          Icons.Default.Delete,
          contentDescription = "Delete",
          tint = Color.White,
          modifier = Modifier.size(28.dp)
        )
      }
    }

    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
        .pointerInput(Unit) {
          detectHorizontalDragGestures(
            onDragEnd = {
              if (offsetX < -deleteThreshold) {
                showDeleteDialog = true
              } else {
                offsetX = 0f
              }
            },
            onDragCancel = { offsetX = 0f }
          ) { _, dragAmount ->
            offsetX = (offsetX + dragAmount).coerceIn(-cardWidth.toFloat(), 0f)
          }
        },
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 2.dp,
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onToggleChosen() }
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          val resId = getImageResId(item.itemId)
          if (resId != -1) {
            Image(
              painter = painterResource(resId),
              contentDescription = item.name,
              modifier = Modifier
                .height(64.dp)
                .width(64.dp)
                .fillMaxHeight(),
              contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
          }
          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {

              Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )

              Spacer(modifier = Modifier.width(8.dp))
              Box(
                modifier = Modifier
                  .size(20.dp)
                  .clip(RoundedCornerShape(4.dp))
                  .background(
                    if (item.isChosen) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                  ),
                contentAlignment = Alignment.Center
              ) {
                if (item.isChosen) {
                  Text(
                    text = "\u2713",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row (
              verticalAlignment = Alignment.Bottom
            ) {
              Column {
                DetailRow(label = "Shot", value = item.shotInfo)
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(label = "Heat", value = item.temperature)
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                DetailRow(label = "Size", value = item.size)
                if (item.temperature != "Hot") {
                  Spacer(modifier = Modifier.height(8.dp))
                  DetailRow(label = "Ice", value = item.ice)
                }
              }

              Spacer(modifier = Modifier.height(8.dp))
              Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "$${String.format("%.2f", item.cost * item.quantity)}",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))

                Text(text = "x${item.quantity}")
              }
            }


          }

        }
      }
    }
  }
}

@Composable
private fun DetailRow(label: String, value: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "$label: ",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall,
      fontWeight = FontWeight.Medium
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun CartItemCardPreview() {
  val mockItem = CartItem(
    inCartId = 1,
    itemId = "americano",
    name = "Americano",
    cost = 3.6,
    shotInfo = "Double",
    temperature = "Cold",
    size = "Large",
    ice = "Normal",
    isChosen = true,
    quantity = 2
  )
  CartItemCard(
    item = mockItem,
    onDelete = {},
    onToggleChosen = {},
    onQuantityChange = {}
  )
}