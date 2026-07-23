package com.example.midtermproject_24125072.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.unit.dp

@Composable
fun Counter(
  value: Int,
  onValueChange: (Int) -> Unit,
  modifier: Modifier = Modifier,
  min: Int = 1,
  increment: Int = 1
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(24.dp),
    color = MaterialTheme.colorScheme.surfaceVariant
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(
        onClick = { onValueChange(value - increment) },
        enabled = value != min,
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          Icons.Outlined.Remove,
          contentDescription = if (value - increment >= min) {
            ("Decrease amount by $increment")
          } else {
            "Cannot decrease any more"
          }
        )
      }
      Text(
        text = "$value",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(horizontal = 8.dp)
      )
      IconButton(
        onClick = { onValueChange(value + increment) },
        modifier = Modifier.size(36.dp)
      ) {
        Icon(Icons.Outlined.Add, contentDescription = "Increase amount by $increment")
      }
    }
  }
}
