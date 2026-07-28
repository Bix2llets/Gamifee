@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.midtermproject_24125072.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.midtermproject_24125072.data.CartItem
import com.example.midtermproject_24125072.data.CoffeeOption
import com.example.midtermproject_24125072.data.local.AppDatabase
import com.example.midtermproject_24125072.data.toDomain
import kotlinx.coroutines.flow.first


@Composable
fun CartPreviewButton(navController: NavController) {
  val context = LocalContext.current
  val database = remember { AppDatabase.getInstance(context) }
  var showSheet: Boolean by rememberSaveable { mutableStateOf(false) }
  var cartItems by remember { mutableStateOf(emptyList<CartItem>()) }

  LaunchedEffect(Unit) {
    cartItems = database.cartItemDao().getAll().first().map { it.toDomain() }
  }
  IconButton(onClick = { showSheet = !showSheet }) {
    Icon(
      imageVector = Icons.Outlined.ShoppingCart,
      contentDescription = "Your Cart"
    )
  }
  if (showSheet) {
    ItemPreview(
      cartItems,
      onDismissRequest = { showSheet = false },
      onGoToCart = { showSheet = !showSheet; navController.navigate("cart") })
  }
}

@Composable
private fun ItemPreview(
  cartItem: List<CartItem>,
  onDismissRequest: () -> Unit,
  onGoToCart: () -> Unit
) {

  val sheetState = rememberModalBottomSheetState()
  val scrollState = rememberScrollState()
  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    dragHandle = { SwipeHintDragHandle(sheetState, scrollState) },
  ) {
    Column(
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = "Cart Preview",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 24.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      Column(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .verticalScroll(scrollState)
      ) {
        cartItem.forEach { item ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("${item.option.name} x${item.quantity}")
            Text(
              "$${String.format("%.2f", item.option.cost * item.quantity)}",
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      cartItem.sumOf { it.option.cost * it.quantity }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .padding(bottom = 16.dp)
          .navigationBarsPadding()
      ) {
        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onGoToCart,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("View Full Cart")
        }
      }
    }
  }
}

private val mockCartItems = listOf(
  CartItem(
    inCartId = 1,
    option = CoffeeOption(
      itemId = "americano", name = "Americano", cost = 3.6,
      shotInfo = "Double", temperature = "Cold", size = "Large", ice = "Normal"
    ),
    isChosen = true, quantity = 2
  ),
  CartItem(
    inCartId = 2,
    option = CoffeeOption(
      itemId = "mocha", name = "Mocha", cost = 4.5,
      shotInfo = "Single", temperature = "Hot", size = "Medium", ice = "N/A"
    ),
    isChosen = false, quantity = 1
  ),
  CartItem(
    inCartId = 3,
    option = CoffeeOption(
      itemId = "flatwhite", name = "White coffee", cost = 5.0,
      shotInfo = "Double", temperature = "Cold", size = "Small", ice = "Less"
    ),
    isChosen = true, quantity = 1
  )
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewItemPreview() {
  ItemPreview(
    cartItem = mockCartItems,
    onDismissRequest = {},
    onGoToCart = {}
  )
}
