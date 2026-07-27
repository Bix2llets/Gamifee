package com.example.midtermproject_24125072.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.midtermproject_24125072.data.CouponItem
import com.example.midtermproject_24125072.data.getImageResId

@Composable
fun CouponCard(
  coupon: CouponItem,
  onRedeem: () -> Unit,
  modifier: Modifier = Modifier

) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
  ) {
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 2.dp,
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        val resId = getImageResId(coupon.option.itemId)
        if (resId != -1) {
          Image(
            painter = painterResource(resId),
            contentDescription = "Image of ${coupon.option.name}",
            modifier = Modifier
              .height(64.dp)
              .width(64.dp),
            contentScale = ContentScale.Fit
          )
          Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = coupon.option.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row {
            Column {
              DetailRow(label = "Shot", value = coupon.option.shotInfo)
              Spacer(modifier = Modifier.height(8.dp))
              DetailRow(label = "Heat", value = coupon.option.temperature)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              DetailRow(label = "Size", value = coupon.option.size)
              if (coupon.option.temperature != "Hot") {
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(label = "Ice", value = coupon.option.ice)
              }
            }
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "${coupon.point} pts",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Button(
            onClick = onRedeem,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.semantics {
              contentDescription = "Redeem ${coupon.option.name} for ${coupon.point} points"
            }
          ) {
            Text("Redeem", style = MaterialTheme.typography.bodySmall)
          }
        }
      }
    }
  }
}

@Composable
private fun DetailRow(label: String, value: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "$label: ",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall,
      fontWeight = FontWeight.Medium
    )
  }
}
