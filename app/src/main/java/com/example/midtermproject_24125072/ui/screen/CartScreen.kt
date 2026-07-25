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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.midtermproject_24125072.data.CartItem
import com.example.midtermproject_24125072.data.OrderItem
import com.example.midtermproject_24125072.data.loadList
import com.example.midtermproject_24125072.data.save
import com.example.midtermproject_24125072.ui.component.BasicInfoDisplay
import com.example.midtermproject_24125072.ui.component.CartItemCard
import kotlin.math.max

@Composable
fun CartScreen(navController: NavController) {
  val context = LocalContext.current
  val cartFileName = context.filesDir.absolutePath + "/cart.json"
  var cartList by remember { mutableStateOf(mutableListOf<CartItem>()) }
  val orderFileName = context.filesDir.absolutePath + "/order.json"
  var showOrderConfirm by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    cartList = CartItem.loadList(cartFileName).toMutableList()
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
            cartList.save(cartFileName)
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
            cartList.save(cartFileName)
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
          if (cartList.filter{it.isChosen}.size != 0)
            showOrderConfirm = true
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
    if (showOrderConfirm) {
      val chosenItems = cartList.filter { it.isChosen }
      val cartList = cartList.filter { !it.isChosen }.toMutableList()
      CheckoutPanel(
        chosenItems,
        onDismiss = { showOrderConfirm = false },
        onConfirmation = { address ->
          if (chosenItems.size != 0) {
            cartList.save(cartFileName)
            val orderList: List<OrderItem> = OrderItem.loadList(orderFileName)
            val maxId = orderList.fold(0) { result, value -> max(result, value.id) }
            val newOrder: OrderItem = OrderItem.create(chosenItems, maxId + 1, address)
            (orderList + newOrder).save(orderFileName)

            navController.navigate("orderSuccess")
          }
        }
      )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckoutPanel(
  chosenItems: List<CartItem>,
  onDismiss: () -> Unit,
  onConfirmation: (String) -> Unit
) {
  var address by remember { mutableStateOf("") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      )
      {
        Text(
          "Your planned order",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
      CheckoutItemDisplay(chosenItems)

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = address,
        onValueChange = { address = it },
        label = { Text("Delivery Address") },
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(16.dp))

      CheckoutButton(onConfirm = { onConfirmation(address) })
    }
  }
}

@Composable
private fun CheckoutItemDisplay(chosenItems: List<CartItem>) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState())
  ) {
    chosenItems.forEach { item ->
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        BasicInfoDisplay(item.name, item.cost, item.quantity)
      }
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}


@Composable
private fun CheckoutButton(onConfirm: () -> Unit) {
  var showDialog by remember { mutableStateOf(false) }

  if (showDialog) {
    AlertDialog(
      onDismissRequest = { showDialog = false },
      title = { Text("Confirm Order") },
      text = { Text("Are you sure you want to place this order?") },
      dismissButton = {
        TextButton(onClick = { showDialog = false }) {
          Text("Cancel")
        }
      },
      confirmButton = {
        TextButton(onClick = {
          showDialog = false
          onConfirm()
        }) {
          Text("Confirm")
        }
      }
    )
  }

  Button(
    onClick = { showDialog = true },
    modifier = Modifier.fillMaxWidth(),
    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
  ) {
    Icon(Icons.Outlined.ShoppingCart, contentDescription = null)
    Spacer(modifier = Modifier.width(8.dp))
    Text("Place Order")
  }
}