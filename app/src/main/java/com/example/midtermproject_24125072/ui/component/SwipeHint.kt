@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.midtermproject_24125072.ui.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.drop

@Composable
fun SwipeHintDragHandle(
  sheetState: SheetState,
  scrollState: androidx.compose.foundation.ScrollState? = null,
  modifier: Modifier = Modifier
) {
  var hasInteracted by remember { mutableStateOf(false) }

  if (scrollState != null) {
    LaunchedEffect(scrollState) {
      snapshotFlow { scrollState.value }
        .drop(1)
        .collect { hasInteracted = true }
    }
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .fillMaxWidth()
      .then(
        if (!hasInteracted) Modifier.pointerInput(Unit) {
          awaitPointerEventScope { awaitFirstDown() }
          hasInteracted = true
        } else Modifier
      )
  ) {
    if (!hasInteracted) {
      val infiniteTransition = rememberInfiniteTransition()
      val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
          animation = tween(800),
          repeatMode = RepeatMode.Reverse
        ),
        label = "swipeHintScale"
      )
      val arrowY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
          animation = tween(800),
          repeatMode = RepeatMode.Reverse
        ),
        label = "swipeHintArrow"
      )
      val textAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
          animation = tween(1200),
          repeatMode = RepeatMode.Reverse
        ),
        label = "swipeHintText"
      )

      Box(
        modifier = Modifier
          .width(32.dp)
          .height(4.dp)
          .graphicsLayer(scaleX = scale, scaleY = scale)
          .clip(RoundedCornerShape(2.dp))
          .background(
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
          )
      )
      Spacer(modifier = Modifier.height(4.dp))
      Icon(
        imageVector = Icons.Filled.KeyboardArrowUp,
        contentDescription = "Swipe up",
        modifier = Modifier
          .size(16.dp)
          .offset(y = arrowY.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = textAlpha)
      )
      Text(
        "Swipe for details",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = textAlpha)
      )
    } else {
      Spacer(modifier = Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .width(32.dp)
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
          )
      )
      Spacer(modifier = Modifier.height(4.dp))
    }
  }
}
