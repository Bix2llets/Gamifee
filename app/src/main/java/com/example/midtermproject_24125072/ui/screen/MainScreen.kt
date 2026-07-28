package com.example.midtermproject_24125072.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.midtermproject_24125072.ui.component.FloatNavigationBox
import com.example.midtermproject_24125072.ui.util.LocalIsLandscape

class BottomNavItem(
  val route: String,
  val label: String,
  val icon: @Composable () -> Unit
)

@Composable
fun AppMainScreen() {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  val configuration = LocalConfiguration.current
  val isLandscape =
    configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

  val routesWithNavBar = listOf("home", "orders", "rewards")

  CompositionLocalProvider(LocalIsLandscape provides isLandscape) {
    Scaffold(
      bottomBar = {
        if (currentRoute in routesWithNavBar) {
          FloatNavigationBox(navController)
        }
      }
    ) { innerPadding ->
      Column {
        NavHost(
          navController = navController,
          startDestination = "home",
          modifier = Modifier.padding(innerPadding)
        ) {
          composable("home") {
            HomeScreen(navController)
          }
          composable("cart") { CartScreen(navController) }
          composable("orders") { OrdersScreen(navController) }
          composable("rewards") { RewardsScreen(navController) }
          composable("details/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            DetailsScreen(navController, itemId)
          }
          composable("orderSuccess") { OrderSuccessScreen(navController) }
          composable("redeem") { RedeemScreen(navController) }
          composable("account") { AccountScreen(navController) }

        }
      }
    }
  }
}

// ─── 5. Bottom nav items list ───


