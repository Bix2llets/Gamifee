package com.example.midtermproject_24125072.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.midtermproject_24125072.data.CoffeeItem
import com.example.midtermproject_24125072.data.MAX_CUP_THRESHOLD
import com.example.midtermproject_24125072.data.UserInformation
import com.example.midtermproject_24125072.data.UserLoyalty
import com.example.midtermproject_24125072.data.getWorkingDir
import com.example.midtermproject_24125072.data.load
import com.example.midtermproject_24125072.data.loadList
import com.example.midtermproject_24125072.data.local.AppDatabase
import com.example.midtermproject_24125072.data.toDomain
import com.example.midtermproject_24125072.data.toEntity
import com.example.midtermproject_24125072.ui.component.CartPreviewButton
import com.example.midtermproject_24125072.ui.component.LoyaltyCard
import com.example.midtermproject_24125072.ui.util.LocalIsLandscape
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavHostController) {
  val context = LocalContext.current
  val database = remember { AppDatabase.getInstance(context) }
  val scope = rememberCoroutineScope()
  val workingDir = getWorkingDir()
  var userLoyalty by remember { mutableStateOf(UserLoyalty(0, 0, emptyList())) }
  var userInfo by remember { mutableStateOf(UserInformation("", "", "", "", false)) }
  var redeemed by remember { mutableStateOf(false) }
  var earnedPoints by remember { mutableStateOf(0) }

  val userInfoEntity by database.userInformationDao().getUserInfo().collectAsState(initial = null)

  LaunchedEffect(userInfoEntity) {
    if (userInfoEntity != null) {
      userInfo = userInfoEntity!!.toDomain()
    }
  }

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

  LaunchedEffect(Unit) {
    database.userInformationDao().getUserInfo().first().let { existing ->
      if (existing == null) {
        val jsonInfo = UserInformation.load("$workingDir/user.json")
        val jsonLoyalty = UserLoyalty.load("$workingDir/loyalty.json")
        val newEntity = jsonInfo.toEntity()
        val id = database.userInformationDao().nextId()
        database.userInformationDao().upsertInfo(newEntity.copy(id = id))
        database.userLoyaltyDao().upsertLoyalty(jsonLoyalty.toEntity(id))
      }
    }
  }

  val redeemAction: () -> Unit = {
    val updated = userLoyalty.addRedeemPoint()
    val newReward = updated.rewardHistory.last()
    scope.launch {
      if (userInfoEntity != null) {
        database.userLoyaltyDao().upsertLoyalty(updated.toEntity())
        database.userLoyaltyDao().insertReward(newReward.toEntity(updated.dbId))
      }
    }
    earnedPoints = newReward.amount
    userLoyalty = updated
    redeemed = true
  }

  val isLandscape = LocalIsLandscape.current

  if (isLandscape) {
    Row(
      modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
    ) {

      HomeHeader(navController, vertical = true, userName = userInfo.name)
      Spacer(modifier = Modifier.width(8.dp))

      LoyaltyCard(
        userLoyalty.cupBought, MAX_CUP_THRESHOLD,
        vertical = true,
        onRedeemClick = redeemAction,
      )

      Spacer(modifier = Modifier.width(8.dp))

      CoffeeGrid(
        onCoffeeClick = { coffeeId ->
          navController.navigate("details/$coffeeId")
        },
        modifier = Modifier.weight(1f)
      )
    }
  } else {
    Column(
      modifier = Modifier
          .fillMaxSize()
          .padding(all = 32.dp)
    ) {
      HomeHeader(navController, userName = userInfo.name)

      Spacer(modifier = Modifier.height(8.dp))

      LoyaltyCard(
        userLoyalty.cupBought, MAX_CUP_THRESHOLD,
        modifier = Modifier.padding(8.dp),
        onRedeemClick = redeemAction,
      )

      Spacer(modifier = Modifier.height(16.dp))

      CoffeeGrid(
        onCoffeeClick = { coffeeId ->
          navController.navigate("details/$coffeeId")
        }
      )

    }
  }
}

private fun greeting(): String {
  val hour = java.time.LocalTime.now().hour
  return when {
    hour in 6..11 -> "Good Morning"
    hour in 12..16 -> "Good Afternoon"
    hour in 17..20 -> "Good Evening"
    else -> "Good Night"
  }
}

@Composable
fun HomeHeader(
  navController: NavHostController,
  vertical: Boolean = false,
  userName: String = "Username"
) {
  val greet = greeting()
  val displayName = userName.ifEmpty { "Username" }.substringBefore(" ")
  if (vertical) {
    Column(
      modifier = Modifier
          .fillMaxHeight()
          .padding(vertical = 4.dp)
          .border(
              width = 1.dp,
              color = MaterialTheme.colorScheme.outlineVariant,
              shape = RoundedCornerShape(12.dp)
          )
          .padding(8.dp),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = greet,
          style = MaterialTheme.typography.labelSmall
        )
        Text(
          text = displayName,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      Row {
        CartPreviewButton(navController)
        IconButton(onClick = { navController.navigate("account") }) {
          Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = "Account setting"
          )
        }
      }
    }
  } else {
    Row(
      modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .padding(4.dp)
      ) {
        Text(
          text = greet, style = MaterialTheme.typography.bodySmall
        )

        Text(
          text = displayName, style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
      }

      Row {
        CartPreviewButton(navController)

        IconButton(onClick = { navController.navigate("account") }) {
          Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = "Account setting"
          )
        }
      }
    }
  }
}

@Composable
fun CoffeeGrid(onCoffeeClick: (String) -> Unit, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val coffeeList = remember { CoffeeItem.loadList(context) }
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primary
    )
  ) {

    Column(modifier = Modifier.padding(16.dp)) {
      Text("Choose your coffee", Modifier.padding(bottom = 16.dp))
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        items(coffeeList, key = { it.id }) { coffee ->
          CoffeeCard(
            coffee = coffee,
            onClick = { onCoffeeClick(coffee.id) }
          )
        }
      }
    }
  }
}

@Composable
fun CoffeeCard(coffee: CoffeeItem, onClick: () -> Unit) {
  Card(
    modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick),
    shape = RoundedCornerShape(12.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column {
      // Image — centered at top
      Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
      ) {
        if (coffee.imageResId != -1) {
          Image(
            painter = painterResource(coffee.imageResId),
            contentDescription = coffee.name,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            contentScale = ContentScale.Fit
          )
        } else {
          Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primaryContainer
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = coffee.name.first().toString(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
        }
      }

      // Name — centered below image
      Text(
        text = coffee.name,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

