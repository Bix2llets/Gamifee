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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.midtermproject_24125072.data.local.AppDatabase
import com.example.midtermproject_24125072.data.toDomain
import com.example.midtermproject_24125072.data.toEntity
import com.example.midtermproject_24125072.ui.component.BasicInfoDisplay
import com.example.midtermproject_24125072.ui.component.CartItemCard
import com.example.midtermproject_24125072.ui.util.LocalIsLandscape
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

@Composable
fun CartScreen(navController: NavController) {
  val context = LocalContext.current
  val database = remember { AppDatabase.getInstance(context) }
  val scope = rememberCoroutineScope()
  val workingDir = getWorkingDir()
  var cartList by remember { mutableStateOf(mutableListOf<CartItem>()) }
  var userLoyalty by remember { mutableStateOf(UserLoyalty(0, 0, emptyList())) }
  var showOrderConfirm by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    cartList = database.cartItemDao().getAll().first().map { it.toDomain() }.toMutableList()
  }

  val userInfoEntity by database.userInformationDao().getUserInfo().collectAsState(initial = null)
  val userId = userInfoEntity?.id
  LaunchedEffect(userId) {
    if (userId != null) {
      database.userLoyaltyDao().getLoyalty(userId).collect { withHistory ->
        if (withHistory != null) {
          userLoyalty = withHistory.loyalty.toDomain(withHistory.history)
        }
      }
    }
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
            scope.launch { database.cartItemDao().delete(cartItem.inCartId) }
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
      CheckoutPanel(
        chosenItems,
        orderTotal = selectedTotal,
        userLoyalty = userLoyalty,
        database = database,
        onDismiss = { showOrderConfirm = false },
        onConfirmation = { address, discount ->
          if (chosenItems.size != 0) {
            scope.launch {
              val infoEntity = database.userInformationDao().getUserInfo().first()
              if (infoEntity == null) return@launch
              val userId = infoEntity.id

              val remaining = cartList.filter { !it.isChosen }
              database.cartItemDao().clearAll()
              remaining.forEach { database.cartItemDao().insert(it.toEntity()) }

              val newId = database.orderItemDao().nextOrderId()
              val discountAmount = discount?.discountDollars ?: 0.0
              val orderEntity = OrderItem(
                id = newId, address = address, orderList = chosenItems,
                orderTime = ZonedDateTime.now(), discountDollars = discountAmount
              )
              val orderEntityDb = orderEntity.toEntity()
              val orderItemEntities = chosenItems.mapIndexed { idx, ci ->
                com.example.midtermproject_24125072.data.local.OrderCartItemEntity(
                  orderId = newId, itemNumber = idx + 1,
                  itemId = ci.option.itemId, name = ci.option.name,
                  cost = ci.option.cost, shotInfo = ci.option.shotInfo,
                  temperature = ci.option.temperature, size = ci.option.size,
                  ice = ci.option.ice, quantity = ci.quantity, isChosen = ci.isChosen
                )
              }
              database.orderItemDao().checkout(orderEntityDb, orderItemEntities)

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
                  chosenItems.fold(0) { r, v -> v.quantity + r },
                  chosenItems.fold(0.0) { r, v -> v.option.cost * v.quantity + r }
                )
              }
              database.userLoyaltyDao().upsertLoyalty(updatedLoyalty.toEntity())
              val newRewards = updatedLoyalty.rewardHistory.drop(userLoyalty.rewardHistory.size)
              newRewards.forEach { r ->
                database.userLoyaltyDao().insertReward(r.toEntity(updatedLoyalty.dbId))
              }
              userLoyalty = updatedLoyalty
              cartList = remaining.toMutableList()
            }
            navController.navigate("orderSuccess")
          }
        }
      )
    }
  }
}

private val mockCartItems = listOf(
  CartItem(
    inCartId = 1, CoffeeOption(
      itemId = "americano", name = "Americano", cost = 3.6,
      shotInfo = "Double", temperature = "Cold", size = "Large", ice = "Normal"
    ), isChosen = true, quantity = 2
  ),
  CartItem(
    inCartId = 2, CoffeeOption(
      itemId = "mocha", name = "Mocha", cost = 4.5,
      shotInfo = "Single", temperature = "Hot", size = "Medium", ice = "N/A"
    ),
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
  database: AppDatabase,
  onDismiss: () -> Unit,
  onConfirmation: (String, DiscountInfo?) -> Unit
) {
  var freshUserInfo by remember { mutableStateOf(UserInformation("", "", "", "", false)) }
  var address by remember { mutableStateOf("") }

  LaunchedEffect(Unit) {
    database.userInformationDao().getUserInfo().first().let { entity ->
      if (entity != null) {
        freshUserInfo = entity.toDomain()
        if (address.isEmpty()) address = freshUserInfo.address
      }
    }
  }
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
            Row(
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {

              Text("Discount")

              Text(
                "-$${String.format("%.2f", discountInfo.discountDollars)}",
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.SemiBold
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
          }
          Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
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