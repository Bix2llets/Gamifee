package com.example.midtermproject_24125072.ui.screen

import com.example.midtermproject_24125072.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.midtermproject_24125072.data.CartItem
import com.example.midtermproject_24125072.data.CoffeeItem
import com.example.midtermproject_24125072.data.loadCartItem
import com.example.midtermproject_24125072.data.loadCoffeeList
import com.example.midtermproject_24125072.data.saveCartItem
import com.example.midtermproject_24125072.ui.component.ChoiceGroup
import com.example.midtermproject_24125072.ui.component.ChoiceOption
import com.example.midtermproject_24125072.ui.component.Counter

@Composable
fun ProductDetailScreen(navController: NavHostController, coffee: CoffeeItem) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(32.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
      }
      Text(
        text = "Details",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )
      IconButton(onClick = { }) {
        Icon(Icons.Outlined.ShoppingCart, contentDescription = "Cart")
      }
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
    ) {
      if (coffee.imageResId != -1) {
        Image(
          painter = painterResource(coffee.imageResId),
          contentDescription = coffee.name,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.FillHeight
        )
      } else {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.primaryContainer
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = coffee.name.first().toString(),
              style = MaterialTheme.typography.displayLarge,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }
        }
      }
    }

    var countSelectAmount by remember { mutableStateOf(1) }

    val shotOption = listOf(
      ChoiceOption("Single", null), ChoiceOption("Double", null)
    )
    var selectedShot by remember { mutableStateOf(shotOption[0].label) }

    val temperatureOption = listOf(
      ChoiceOption("Hot", R.drawable.cup_hot), ChoiceOption("Cold", R.drawable.cup_iced)
    )
    var selectedTemperature by remember { mutableStateOf(temperatureOption[0].label) }

    val sizeOption = listOf(
      ChoiceOption("Small", R.drawable.cup_small),
      ChoiceOption("Medium", R.drawable.cup_medium),
      ChoiceOption("Large", R.drawable.cup_large)
    )
    var selectedSize by remember { mutableStateOf(sizeOption[0].label) }

    val iceOption = listOf(
      ChoiceOption("Less", R.drawable.ice1),
      ChoiceOption("Normal", R.drawable.ice2),
      ChoiceOption("More", R.drawable.ice3)
    )
    var selectedIce by remember { mutableStateOf(iceOption[0].label) }

    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
    ) {
      CoffeeAmount(
        coffee = coffee,
        countSelectAmount = countSelectAmount,
        onCountChange = { countSelectAmount = it }
      )
      ShotOption(
        shotOption = shotOption,
        selectedShot = selectedShot,
        onShotChanged = { selectedShot = it }
      )
      TemperatureOption(
        temperatureOption = temperatureOption,
        selectedTemperature = selectedTemperature,
        onTemperatureChanged = { selectedTemperature = it }
      )
      SizeOption(
        sizeOption = sizeOption,
        selectedSize = selectedSize,
        onSizeChanged = { selectedSize = it }
      )
      if (selectedTemperature != "Hot") {
        IceOption(
          iceOption = iceOption,
          selectedIce = selectedIce,
          onIceChanged = { selectedIce = it }
        )
      }
    }

    var coffeePrice = calculateCoffeePrice(
      coffee,
      shotOption,
      selectedShot,
      sizeOption,
      selectedSize,
      iceOption,
      selectedIce,
      selectedTemperature,
    )

    DisplayPrice(coffeePrice * countSelectAmount)
    Spacer(modifier = Modifier.height(8.dp))

    val context = LocalContext.current
    val cartFileName = context.filesDir.absolutePath + "/cart.json"
    AddToCartButton(
      onClick = {
        val existingCart = loadCartItem(cartFileName)
        val newId = (existingCart.maxOfOrNull { it.inCartId } ?: 0) + 1
        val newItem = CartItem(
          inCartId = newId,
          itemId = coffee.id,
          name = coffee.name,
          cost = kotlin.math.round(coffeePrice * 100) / 100,
          shotInfo = selectedShot,
          temperature = selectedTemperature,
          size = selectedSize,
          ice = if (selectedTemperature == "Hot") "N/A" else selectedIce,
          quantity = countSelectAmount
        )
        saveCartItem(cartFileName, existingCart + newItem)
      }
    )
  }
}

@Composable
private fun CoffeeAmount(
  coffee: CoffeeItem,
  countSelectAmount: Int,
  onCountChange: (Int) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = coffee.name,
      style = MaterialTheme.typography.bodyLarge,
      fontWeight = FontWeight.Bold
    )
    Counter(
      value = countSelectAmount,
      onValueChange = onCountChange
    )
  }
}

@Composable
private fun ShotOption(
  shotOption: List<ChoiceOption>,
  selectedShot: String,
  onShotChanged: (String) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "Shot",
      style = MaterialTheme.typography.bodyLarge,
      fontWeight = FontWeight.Normal
    )
    ChoiceGroup(
      options = shotOption,
      selectedOption = selectedShot,
      onOptionSelected = onShotChanged
    )
  }
}

@Composable
private fun TemperatureOption(
  temperatureOption: List<ChoiceOption>,
  selectedTemperature: String,
  onTemperatureChanged: (String) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "Temperature",
      style = MaterialTheme.typography.bodyLarge,
      fontWeight = FontWeight.Normal
    )
    ChoiceGroup(
      options = temperatureOption,
      selectedOption = selectedTemperature,
      onOptionSelected = onTemperatureChanged
    )
  }
}

@Composable
private fun SizeOption(
  sizeOption: List<ChoiceOption>,
  selectedSize: String,
  onSizeChanged: (String) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "Size",
      style = MaterialTheme.typography.bodyLarge,
      fontWeight = FontWeight.Normal
    )
    ChoiceGroup(
      options = sizeOption,
      selectedOption = selectedSize,
      onOptionSelected = onSizeChanged
    )
  }
}

@Composable
private fun IceOption(
  iceOption: List<ChoiceOption>,
  selectedIce: String,
  onIceChanged: (String) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "Ice",
      style = MaterialTheme.typography.bodyLarge,
      fontWeight = FontWeight.Normal
    )
    ChoiceGroup(
      options = iceOption,
      selectedOption = selectedIce,
      onOptionSelected = onIceChanged
    )
  }
}

@Composable
private fun calculateCoffeePrice(
  coffee: CoffeeItem,
  shotOption: List<ChoiceOption>,
  selectedShot: String,
  sizeOption: List<ChoiceOption>,
  selectedSize: String,
  iceOption: List<ChoiceOption>,
  selectedIce: String,
  selectedTemperature: String,
): Double {
  var coffeePrice = coffee.price
  val SHOT_MODIFIER = 1.5
  val SIZE_MODIFIER = 1.2
  val ICE_ADDITION = 0.2
  val isDouble = shotOption.indexOfFirst { option -> option.label == selectedShot }
  val coffeeSize = sizeOption.indexOfFirst { option -> option.label == selectedSize } - 1
  val iceNumber = iceOption.indexOfFirst { option -> option.label == selectedIce }

  for (i in 1..isDouble) {
    coffeePrice = coffeePrice * SHOT_MODIFIER
  }
  when (coffeeSize) {
    -1 ->
      coffeePrice = coffeePrice / SIZE_MODIFIER

    1 -> coffeePrice = coffeePrice * SIZE_MODIFIER
  }
  if (iceNumber == 0 && selectedTemperature != "Hot") {
    coffeePrice = coffeePrice + ICE_ADDITION
  }
  return coffeePrice
}

@Composable
private fun AddToCartButton(onClick: () -> Unit) {
  Button(
    onClick = onClick,
    colors = ButtonDefaults.buttonColors(),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Text("Add to cart")
    }
  }
}

@Composable
private fun DisplayPrice(coffeePrice: Double) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "Total amount",
      style = MaterialTheme.typography.bodyLarge,
      fontWeight = FontWeight.Normal
    )
    Text(
      text = "$%.2f".format(coffeePrice),
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Composable
fun DetailsScreen(navController: NavHostController, itemId: String?) {
  val context = LocalContext.current
  val coffeeList = remember { loadCoffeeList(context) }
  val coffee: CoffeeItem? = coffeeList.find { it.id == itemId }
  if (coffee == null || itemId == null) {
    Text("Item not available")
    return
  }
  ProductDetailScreen(navController, coffee)
}

@Composable
fun CartPreview() {

}