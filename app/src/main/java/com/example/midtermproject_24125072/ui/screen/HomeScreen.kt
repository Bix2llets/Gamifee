package com.example.midtermproject_24125072.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Portrait
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.midtermproject_24125072.data.CoffeeItem
import com.example.midtermproject_24125072.data.coffeeList
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import com.example.midtermproject_24125072.ui.component.FloatNavigationBox
import com.example.midtermproject_24125072.ui.component.LoyaltyCard

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 32.dp)
    ) {
        HomeHeader(
            onCartClick = { navController.navigate("cart")},
            onAccountClick = { /* navigate to profile later */ }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LoyaltyCard(4, 8, modifier = Modifier.padding(8.dp))

        Spacer(modifier = Modifier.height(16.dp))

        CoffeeGrid(
            onCoffeeClick = { coffeeId ->
                navController.navigate("details/$coffeeId")
            }
        )

    }
}

@Composable
fun HomeHeader(onCartClick: () -> Unit, onAccountClick: () -> Unit) {
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
                text = "Good Morning", style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Username", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Row() {
            IconButton(onClick = onCartClick) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Your Cart"

                )
            }

            IconButton(onClick = onAccountClick) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Account setting"
                )
            }
        }
    }
}

@Composable
fun CoffeeGrid(onCoffeeClick: (String) -> Unit) {
    Card(
        modifier = Modifier,
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
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
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

@Preview(showBackground = true)
@Composable
fun PreviewHomeHeader() {
    Row() {
        HomeHeader({}, {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        HomeHeader(
            onCartClick = { },
            onAccountClick = { /* navigate to profile later */ }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LoyaltyCard(4, 8, modifier = Modifier.padding(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        CoffeeGrid(
            onCoffeeClick = {
//                coffeeId ->
//                navController.navigate("details/$coffeeId")
            }
        )

    }
}
