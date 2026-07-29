package com.example.midtermproject_24125072.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.midtermproject_24125072.R
import com.example.midtermproject_24125072.data.CartItem
import com.example.midtermproject_24125072.data.CoffeeItem
import com.example.midtermproject_24125072.data.CoffeeOption
import com.example.midtermproject_24125072.data.loadList
import com.example.midtermproject_24125072.data.local.AppDatabase
import com.example.midtermproject_24125072.data.toEntity
import com.example.midtermproject_24125072.ui.component.CartPreviewButton
import com.example.midtermproject_24125072.ui.component.ChoiceGroup
import com.example.midtermproject_24125072.ui.component.ChoiceOption
import com.example.midtermproject_24125072.ui.component.Counter
import com.example.midtermproject_24125072.ui.util.LocalIsLandscape
import kotlinx.coroutines.launch

@Composable
fun ProductDetailScreen(navController: NavHostController, coffee: CoffeeItem) {
  val context = LocalContext.current
  val database = remember { AppDatabase.getInstance(context) }
  val scope = rememberCoroutineScope()
  val isLandscape = LocalIsLandscape.current

  var countSelectAmount by rememberSaveable { mutableStateOf(1) }

  val shotOption = listOf(
    ChoiceOption("Single", null), ChoiceOption("Double", null)
  )
  var selectedShot by rememberSaveable { mutableStateOf(shotOption[0].label) }

  val temperatureOption = listOf(
    ChoiceOption("Hot", R.drawable.cup_hot), ChoiceOption("Cold", R.drawable.cup_iced)
  )
  var selectedTemperature by rememberSaveable { mutableStateOf(temperatureOption[0].label) }

  val sizeOption = listOf(
    ChoiceOption("Small", R.drawable.cup_small),
    ChoiceOption("Medium", R.drawable.cup_medium),
    ChoiceOption("Large", R.drawable.cup_large)
  )
  var selectedSize by rememberSaveable { mutableStateOf(sizeOption[0].label) }

  val iceOption = listOf(
    ChoiceOption("Less", R.drawable.ice1),
    ChoiceOption("Normal", R.drawable.ice2),
    ChoiceOption("More", R.drawable.ice3)
  )
  var selectedIce by rememberSaveable { mutableStateOf(iceOption[0].label) }

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

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(all = if (isLandscape) 16.dp else 32.dp)
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
      CartPreviewButton(navController)
    }

    if (isLandscape) {
      Row(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
      ) {
        ProductImageDisplay(
          coffee = coffee,
          modifier = Modifier
            .weight(0.35f)
            .fillMaxHeight()
        )
        ProductOptionSection(
          coffee = coffee,
          countSelectAmount = countSelectAmount,
          onCountChange = { countSelectAmount = it },
          shotOption = shotOption,
          selectedShot = selectedShot,
          onShotChanged = { selectedShot = it },
          temperatureOption = temperatureOption,
          selectedTemperature = selectedTemperature,
          onTemperatureChanged = { selectedTemperature = it },
          sizeOption = sizeOption,
          selectedSize = selectedSize,
          onSizeChanged = { selectedSize = it },
          iceOption = iceOption,
          selectedIce = selectedIce,
          onIceChanged = { selectedIce = it },
          modifier = Modifier.weight(0.65f)
        )
      }
    } else {
      ProductImageDisplay(
        coffee = coffee,
        modifier = Modifier
          .fillMaxWidth()
          .height(160.dp)
      )
      ProductOptionSection(
        coffee = coffee,
        countSelectAmount = countSelectAmount,
        onCountChange = { countSelectAmount = it },
        shotOption = shotOption,
        selectedShot = selectedShot,
        onShotChanged = { selectedShot = it },
        temperatureOption = temperatureOption,
        selectedTemperature = selectedTemperature,
        onTemperatureChanged = { selectedTemperature = it },
        sizeOption = sizeOption,
        selectedSize = selectedSize,
        onSizeChanged = { selectedSize = it },
        iceOption = iceOption,
        selectedIce = selectedIce,
        onIceChanged = { selectedIce = it },
        modifier = Modifier.weight(1f)
      )
    }

    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

    ProductPriceAndCart(
      totalPrice = coffeePrice * countSelectAmount,
      onAddToCart = {
        scope.launch {
          val newId = database.cartItemDao().nextInCartId()
          val entity = CartItem(
            inCartId = newId,
            option = CoffeeOption(
              itemId = coffee.id,
              name = coffee.name,
              cost = kotlin.math.round(coffeePrice * 100) / 100,
              shotInfo = selectedShot,
              temperature = selectedTemperature,
              size = selectedSize,
              ice = if (selectedTemperature == "Hot") "N/A" else selectedIce
            ),
            quantity = countSelectAmount
          ).toEntity()
          database.cartItemDao().insert(entity)
        }
        navController.navigate("home") {
          popUpTo(0) { inclusive = true }
        }
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
private fun ProductImageDisplay(
  coffee: CoffeeItem,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier) {
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
}

@Composable
private fun ProductOptionSection(
  coffee: CoffeeItem,
  countSelectAmount: Int,
  onCountChange: (Int) -> Unit,
  shotOption: List<ChoiceOption>,
  selectedShot: String,
  onShotChanged: (String) -> Unit,
  temperatureOption: List<ChoiceOption>,
  selectedTemperature: String,
  onTemperatureChanged: (String) -> Unit,
  sizeOption: List<ChoiceOption>,
  selectedSize: String,
  onSizeChanged: (String) -> Unit,
  iceOption: List<ChoiceOption>,
  selectedIce: String,
  onIceChanged: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.verticalScroll(rememberScrollState())
  ) {
    CoffeeAmount(
      coffee = coffee,
      countSelectAmount = countSelectAmount,
      onCountChange = onCountChange
    )
    ShotOption(
      shotOption = shotOption,
      selectedShot = selectedShot,
      onShotChanged = onShotChanged
    )
    TemperatureOption(
      temperatureOption = temperatureOption,
      selectedTemperature = selectedTemperature,
      onTemperatureChanged = onTemperatureChanged
    )
    SizeOption(
      sizeOption = sizeOption,
      selectedSize = selectedSize,
      onSizeChanged = onSizeChanged
    )
    if (selectedTemperature != "Hot") {
      IceOption(
        iceOption = iceOption,
        selectedIce = selectedIce,
        onIceChanged = onIceChanged
      )
    }
  }
}

@Composable
private fun ProductPriceAndCart(
  totalPrice: Double,
  onAddToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Total amount",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Normal
      )
      Text(
        text = "$%.2f".format(totalPrice),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold
      )
    }

    Button(
      onClick = onAddToCart,
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
fun DetailsScreen(navController: NavHostController, itemId: String?) {
  val context = LocalContext.current
  val coffeeList = remember { CoffeeItem.loadList(context) }
  val coffee: CoffeeItem? = coffeeList.find { it.id == itemId }
  if (coffee == null || itemId == null) {
    Text("Item not available")
    return
  }
  ProductDetailScreen(navController, coffee)
}
