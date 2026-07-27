package com.example.midtermproject_24125072.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.midtermproject_24125072.data.CartItem
import com.example.midtermproject_24125072.data.CoffeeOption
import com.example.midtermproject_24125072.data.DiscountInfo
import com.example.midtermproject_24125072.data.OrderItem
import com.example.midtermproject_24125072.data.RewardEntry
import com.example.midtermproject_24125072.data.UserInformation
import com.example.midtermproject_24125072.data.UserLoyalty
import com.example.midtermproject_24125072.data.calculateDiscount
import com.example.midtermproject_24125072.data.getWorkingDir
import com.example.midtermproject_24125072.data.load
import com.example.midtermproject_24125072.data.loadList
import com.example.midtermproject_24125072.data.save
import com.example.midtermproject_24125072.ui.component.BasicInfoDisplay
import com.example.midtermproject_24125072.ui.component.CartItemCard
import com.example.midtermproject_24125072.ui.util.LocalIsLandscape
import java.time.ZonedDateTime
import kotlin.math.max

@Composable
fun CartScreen(navController: NavController) {
  val workingDir = getWorkingDir()
  val cartFileName = "$workingDir/cart.json"
  var cartList by remember { mutableStateOf(mutableListOf<CartItem>()) }
  val orderFileName = "$workingDir/order.json"
  val loyaltyFileName = "$workingDir/loyalty.json"
  var showOrderConfirm by rememberSaveable { mutableStateOf(false) }
  val userLoyalty = remember { UserLoyalty.load(loyaltyFileName) }

  LaunchedEffect(Unit) {
    cartList = CartItem.loadList(cartFileName).toMutableList()
  }

  val isLandscape = LocalIsLandscape.current

  val selectedTotal = remember(cartList) {
    cartList
      .filter { it.isChosen }
      .sumOf { it.option.cost * it.quantity }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(all = if (isLandscape) 16.dp else 32.dp)
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
            cartList.save(cartFileName)
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
          if (cartList.filter { it.isChosen }.size != 0)
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
        orderTotal = selectedTotal,
        userLoyalty = userLoyalty,
        onDismiss = { showOrderConfirm = false },
        onConfirmation = { address, discount ->
          if (chosenItems.size != 0) {
            cartList.save(cartFileName)
            val orderList: List<OrderItem> = OrderItem.loadList(orderFileName)
            val maxId = orderList.fold(0) { result, value -> max(result, value.id) }
            val discountAmount = discount?.discountDollars ?: 0.0
            val newOrder: OrderItem = OrderItem.create(chosenItems, maxId + 1, address, discountAmount)
            (orderList + newOrder).save(orderFileName)

            val updatedLoyalty = if (discount != null) {
              userLoyalty.addRewardHistory(
                RewardEntry(
                  amount = -discount.pointsToDeduct,
                  date = ZonedDateTime.now(),
                  reason = "Apply discount of $${discount.discountDollars}"
                )
              )
            } else {
              userLoyalty.addCupBought(
                newOrder.orderList.fold(0) { result, value -> value.quantity + result },
                newOrder.orderList.fold(0.0) { result, value -> value.option.cost * value.quantity + result }
              )
            }
            updatedLoyalty.save(loyaltyFileName)
            navController.navigate("orderSuccess")
          }
        }
      )
    }
  }
}

private val mockCartItems = listOf(
  CartItem(
    inCartId = 1, CoffeeOption(itemId = "americano", name = "Americano", cost = 3.6,
    shotInfo = "Double", temperature = "Cold", size = "Large", ice = "Normal"
    ), isChosen = true, quantity = 2
  ),
  CartItem(
    inCartId = 2, CoffeeOption(itemId = "mocha", name = "Mocha", cost = 4.5,
    shotInfo = "Single", temperature = "Hot", size = "Medium", ice = "N/A"),
    isChosen = false, quantity = 1
  ),
  CartItem(
    inCartId = 3, CoffeeOption(
      itemId = "flatwhite", name = "White coffee", cost = 5.0,
      shotInfo = "Double", temperature = "Cold", size = "Small", ice = "Less"
    ),
    isChosen = true, quantity = 1
  )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckoutPanel(
  chosenItems: List<CartItem>,
  orderTotal: Double,
  userLoyalty: UserLoyalty,
  onDismiss: () -> Unit,
  onConfirmation: (String, DiscountInfo?) -> Unit
) {
  val workingDir = getWorkingDir()
  val freshUserInfo = remember { UserInformation.load("$workingDir/user.json") }
  var address by remember { mutableStateOf(freshUserInfo.address) }
  var isAddressModified by remember { mutableStateOf(false) }
  var useDiscount by remember { mutableStateOf(false) }
  var showDiscountAlert by remember { mutableStateOf(false) }

  val discountInfo = remember(orderTotal, userLoyalty.loyaltyPoint) {
    calculateDiscount(orderTotal, userLoyalty.loyaltyPoint)
  }

  if (showDiscountAlert) {
    AlertDialog(
      onDismissRequest = { showDiscountAlert = false },
      title = { Text("Use Loyalty Discount") },
      text = { Text("Discounted orders won't earn loyalty cups or points. Do you want to proceed?") },
      dismissButton = {
        TextButton(onClick = { showDiscountAlert = false; useDiscount = false }) {
          Text("Cancel")
        }
      },
      confirmButton = {
        TextButton(onClick = { showDiscountAlert = false; useDiscount = true }) {
          Text("Proceed")
        }
      }
    )
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .verticalScroll(rememberScrollState())
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

        Column(
          modifier = Modifier
            .fillMaxWidth(),
          horizontalAlignment = Alignment.End
        ) {

          var discounted = orderTotal

          if (useDiscount && discountInfo != null) {
            discounted = orderTotal - discountInfo.discountDollars
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {

              Text("Discount")

              Text(
                "-$${String.format("%.2f", discountInfo.discountDollars)}",
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.SemiBold
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
          }
          Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
              "Sum"
            )
            Text(
              "$%.2f".format(discounted), fontWeight = FontWeight.SemiBold
            )
          }
        }
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Checkbox(
            checked = useDiscount,
            onCheckedChange = { checked ->
              if (checked && discountInfo != null) {
                showDiscountAlert = true
              } else {
                useDiscount = false
              }
            },
            enabled = discountInfo != null
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            if (discountInfo != null) "Use ${discountInfo.pointsToDeduct} pts for $${
              String.format(
                "%.2f",
                discountInfo.discountDollars
              )
            } discount"
            else "Loyalty discount unavailable"
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = address,
          onValueChange = {
            address = it
            if (!isAddressModified) isAddressModified = true
          },
          label = { Text("Delivery Address") },
          supportingText = {
            if (!isAddressModified) Text("From your profile")
          },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
          .navigationBarsPadding()
      ) {
        CheckoutButton(onConfirm = {
          val deliveryAddress = if (isAddressModified) address else freshUserInfo.address
          onConfirmation(
            deliveryAddress,
            if (useDiscount) discountInfo else null
          )
        })
      }
    }
  }
}

@Composable
private fun CheckoutItemDisplay(chosenItems: List<CartItem>) {
  Column(
    modifier = Modifier.fillMaxWidth()
  ) {
    chosenItems.forEach { item ->
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        BasicInfoDisplay(item.option.name, item.option.cost, item.quantity)
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