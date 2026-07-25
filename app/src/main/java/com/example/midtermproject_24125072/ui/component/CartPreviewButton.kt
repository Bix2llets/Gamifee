@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.midtermproject_24125072.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.midtermproject_24125072.data.CartItem
import com.example.midtermproject_24125072.data.getWorkingDir
import com.example.midtermproject_24125072.data.loadList


@Composable
fun CartPreviewButton(navController: NavController){
  var showSheet: Boolean by remember{mutableStateOf(false)}
  var cartFileName = getWorkingDir() + "/cart.json"
  var cartItems   by remember {mutableStateOf(emptyList<CartItem>())}

  LaunchedEffect(Unit) {
    cartItems = CartItem.loadList(cartFileName)
  }
  IconButton(onClick = {showSheet = !showSheet}) {
    Icon(
      imageVector = Icons.Outlined.ShoppingCart,
      contentDescription = "Your Cart"
    )
  }
  if (showSheet) {
    ItemPreview(cartItems, onDismissRequest = {showSheet = false}, onGoToCart = {navController.navigate("cart")})
  }
}

@Composable
private fun ItemPreview(cartItem: List<CartItem>, onDismissRequest: () -> Unit, onGoToCart: () -> Unit) {

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = rememberModalBottomSheetState()
  ) {
    Column(
      modifier = Modifier
        .padding(horizontal = 24.dp, vertical = 16.dp)
        .fillMaxWidth()
    ) {
      Text(
        text = "Cart Preview",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(12.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 300.dp)
          .verticalScroll(rememberScrollState())
      ) {
        cartItem.forEach { item ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("${item.name} x${item.quantity}")
            Text(
              "$${String.format("%.2f", item.cost * item.quantity)}",
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      val total = cartItem.sumOf { it.cost * it.quantity }
      Text(
        text = "Total: $${String.format("%.2f", total)}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = onGoToCart,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("View Full Cart")
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

private val mockCartItems = listOf(
  CartItem(
    inCartId = 1, itemId = "americano", name = "Americano", cost = 3.6,
    shotInfo = "Double", temperature = "Cold", size = "Large", ice = "Normal",
    isChosen = true, quantity = 2
  ),
  CartItem(
    inCartId = 2, itemId = "mocha", name = "Mocha", cost = 4.5,
    shotInfo = "Single", temperature = "Hot", size = "Medium", ice = "N/A",
    isChosen = false, quantity = 1
  ),
  CartItem(
    inCartId = 3, itemId = "flatwhite", name = "White coffee", cost = 5.0,
    shotInfo = "Double", temperature = "Cold", size = "Small", ice = "Less",
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
