package com.example.midtermproject_24125072.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.midtermproject_24125072.data.CartItem
import com.example.midtermproject_24125072.data.loadCartItem
import com.example.midtermproject_24125072.data.saveCartItem
import com.example.midtermproject_24125072.ui.component.CartItemCard

@Composable
fun CartScreen(navController: NavController) {
  val context = LocalContext.current
  val cartFileName = context.filesDir.absolutePath + "/cart.json"
  var cartList by remember { mutableStateOf(mutableListOf<CartItem>()) }

  LaunchedEffect(Unit) {
    cartList = loadCartItem(cartFileName).toMutableList()
  }

  val selectedTotal = cartList
    .filter { it.isChosen }
    .sumOf { it.cost * it.quantity }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(32.dp)
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
        text = "My Cart",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.align(Alignment.Center)
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
    ) {
      cartList.forEach { cartItem ->
        CartItemCard(
          item = cartItem,
          onDelete = {
            cartList = cartList.filter { it.inCartId != cartItem.inCartId }.toMutableList()
            saveCartItem(cartFileName, cartList)
          },
          onToggleChosen = {
            cartList = cartList.map {
              if (it.inCartId == cartItem.inCartId) it.copy(isChosen = !it.isChosen) else it
            }.toMutableList()
          },
          onQuantityChange = { newQty ->
            cartList = cartList.map {
              if (it.inCartId == cartItem.inCartId) it.copy(quantity = newQty) else it
            }.toMutableList()
            saveCartItem(cartFileName, cartList)
          }
        )
        Spacer(modifier = Modifier.height(12.dp))
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "Total: $${String.format("%.2f", selectedTotal)}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )

      Button(
        onClick = {
          val chosenItems = cartList.filter { it.isChosen }
          cartList = cartList.filter { !it.isChosen }.toMutableList()
          saveCartItem(cartFileName, cartList)
          navController.navigate("orderSuccess")
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Icon(
          Icons.Outlined.ShoppingCart,
          contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Checkout")
      }
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
fun CartScreenPreview() {
  var cartList by remember { mutableStateOf(mockCartItems.toMutableList()) }

  val selectedTotal = cartList
    .filter { it.isChosen }
    .sumOf { it.cost * it.quantity }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(32.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
    ) {
      Text(
        text = "My Cart",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.align(Alignment.Center)
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
    ) {
      cartList.forEach { cartItem ->
        CartItemCard(
          item = cartItem,
          onDelete = {
            cartList = cartList.filter { it.inCartId != cartItem.inCartId }.toMutableList()
          },
          onToggleChosen = {
            cartList = cartList.map {
              if (it.inCartId == cartItem.inCartId) it.copy(isChosen = !it.isChosen) else it
            }.toMutableList()
          },
          onQuantityChange = { newQty ->
            cartList = cartList.map {
              if (it.inCartId == cartItem.inCartId) it.copy(quantity = newQty) else it
            }.toMutableList()
          }
        )
        Spacer(modifier = Modifier.height(12.dp))
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {

        Text(
          text = "Total",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = Color.Gray.copy(alpha = 0.5f)

        )
        Text(
          text = "$${String.format("%.2f", selectedTotal)}",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      Button(
        onClick = {
          cartList = cartList.filter { !it.isChosen }.toMutableList()
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Icon(Icons.Outlined.ShoppingCart, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Checkout")
      }
    }
  }
}