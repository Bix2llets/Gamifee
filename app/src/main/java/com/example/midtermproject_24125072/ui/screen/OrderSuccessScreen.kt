package com.example.midtermproject_24125072.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.midtermproject_24125072.R

@Composable
fun OrderSuccessScreen(navController: NavController) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Image(painter = painterResource(R.drawable.takeaway),
      contentDescription = "Order success",
      )
    Spacer(modifier = Modifier.height(48.dp))
    Text(
      text = "Order success",
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = "Your order have been ordered successfully. \n For more details, go to \"My order\"",
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.Medium,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)

    )

    Spacer(modifier = Modifier.height(32.dp))

    Button(
      onClick = { navController.navigate("orders") {
                            popUpTo(0) { inclusive = true }
                        } },
      modifier = Modifier.fillMaxWidth(),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
      )
    ) {
      Text("Track my orders")
    }

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedButton(
      onClick = { navController.navigate("cart") {
                            popUpTo(0) { inclusive = true }
                        } },
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Back to shopping cart")
    }

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedButton(
      onClick = { navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        } },
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Back to home")
    }
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OrderSuccessScreenPreview() {
  OrderSuccessScreen(rememberNavController())
}