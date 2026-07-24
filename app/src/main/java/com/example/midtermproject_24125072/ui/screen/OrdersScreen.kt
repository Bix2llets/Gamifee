package com.example.midtermproject_24125072.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.midtermproject_24125072.data.OrderItem
import com.example.midtermproject_24125072.data.loadOrderItem
import com.example.midtermproject_24125072.data.saveOrderItem
import com.example.midtermproject_24125072.ui.component.OrderCard


@Composable
fun OrdersScreen(navHostController: NavHostController) {
  val context = androidx.compose.ui.platform.LocalContext.current
  val orderFileName = context.filesDir.absolutePath + "/order.json"
  var orderList by remember { mutableStateOf(mutableListOf<OrderItem>()) }
  var selectedTabIndex by remember { mutableIntStateOf(0) }

  LaunchedEffect(Unit) {
    orderList = loadOrderItem(orderFileName).toMutableList()
  }

  val ongoingOrders = orderList.filter { !it.isCompleted }
  val historyOrders = orderList.filter { it.isCompleted }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(32.dp)
  ) {
    Box(
      modifier = Modifier.fillMaxWidth(),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "My Order",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    TabBar(
      selectedIndex = selectedTabIndex,
      onTabSelected = { selectedTabIndex = it }
    )

    Spacer(modifier = Modifier.height(12.dp))

    AnimatedContent(
      targetState = selectedTabIndex,
      transitionSpec = {
        val direction = if (targetState > initialState) 1 else -1
        slideInHorizontally(
          animationSpec = tween(300),
          initialOffsetX = { fullWidth -> direction * fullWidth }
        ) togetherWith slideOutHorizontally(
          animationSpec = tween(300),
          targetOffsetX = { fullWidth -> -direction * fullWidth }
        ) using SizeTransform(clip = false)
      },
      label = "tabContent"
    ) { paneIndex ->
      when (paneIndex) {
        0 -> OrderList(
          orders = ongoingOrders,
          emptyMessage = "No ongoing orders",
          enableSwipe = true,
          onSwipeComplete = { orderId ->
            orderList = orderList.map {
              if (it.id == orderId) it.copy(isCompleted = true) else it
            }.toMutableList()
            saveOrderItem(orderFileName, orderList)
          }
        )
        1 -> OrderList(
          orders = historyOrders,
          emptyMessage = "No order history yet",
          enableSwipe = false,
          onSwipeComplete = {}
        )
      }
    }
  }
}

@Composable
private fun TabBar(
  selectedIndex: Int,
  onTabSelected: (Int) -> Unit,
) {
  val tabLabels = listOf("On going", "History")
  val primaryColor = MaterialTheme.colorScheme.primary
  val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(surfaceVariant),
    horizontalArrangement = Arrangement.SpaceEvenly,
  ) {
    tabLabels.forEachIndexed { index, label ->
      val isActive = index == selectedIndex
      val textColor by animateColorAsState(
        targetValue = if (isActive) primaryColor else primaryColor.copy(alpha = 0.4f),
        animationSpec = tween(300),
        label = "tabColor"
      )

      Box(
        modifier = Modifier
          .weight(1f)
          .clickable { onTabSelected(index) }
          .then(
            if (isActive) {
              Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
            } else {
              Modifier.padding(4.dp)
            }
          )
          .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = label,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
          color = textColor,
        )
      }
    }
  }
}

@Composable
private fun OrderList(
  orders: List<OrderItem>,
  emptyMessage: String,
  enableSwipe: Boolean,
  onSwipeComplete: (Int) -> Unit,
) {
  if (orders.isEmpty()) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = emptyMessage,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )
    }
  } else {
    LazyColumn(
      contentPadding = PaddingValues(bottom = 16.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      items(orders, key = { it.id }) { order ->
        OrderCard(
          order = order,
          enableSwipe = enableSwipe,
          onSwipeComplete = { onSwipeComplete(order.id) }
        )
      }
    }
  }
}
