package com.example.midtermproject_24125072.ui.screen

import android.graphics.BitmapFactory
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.midtermproject_24125072.data.UserInformation
import com.example.midtermproject_24125072.data.getWorkingDir
import com.example.compose.ThemeManager

@Composable
fun AccountScreen(navController: NavController) {
  val context = LocalContext.current
  val workingDir = getWorkingDir()
  val userFileName = "$workingDir/user.json"
  val avatarFileName = "$workingDir/avatar.png"
  var userInfo by remember { mutableStateOf(UserInformation.load(userFileName)) }
  var avatarRefreshKey by remember { mutableStateOf(0) }

  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri ->
    uri?.let {
      try {
        context.contentResolver.openInputStream(it)?.use { input ->
          java.io.File(avatarFileName).outputStream().use { output ->
            input.copyTo(output)
          }
        }
        userInfo = userInfo.copy(haveAvatar = true)
        userInfo.save(userFileName)
        avatarRefreshKey++
      } catch (_: Exception) { }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(32.dp)
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
        text = "Account",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.align(Alignment.Center)
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
    ) {
      Box(
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .size(100.dp)
          .clip(CircleShape)
          .clickable { galleryLauncher.launch("image/*") }
          .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        if (userInfo.haveAvatar) {
          val bitmap = remember(avatarRefreshKey) {
            BitmapFactory.decodeFile(avatarFileName)
          }
          if (bitmap != null) {
            Image(
              bitmap = bitmap.asImageBitmap(),
              contentDescription = "Avatar",
              modifier = Modifier.fillMaxSize()
            )
          } else {
            Icon(
              Icons.Outlined.Person,
              contentDescription = "Avatar",
              modifier = Modifier.size(56.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          Icon(
            Icons.Outlined.Person,
            contentDescription = "Avatar",
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      EditableField(
        label = "Name",
        initialValue = userInfo.name,
        validate = { true },
        onValidValue = {
          userInfo = userInfo.copy(name = it)
          userInfo.save(userFileName)
        }
      )
      Spacer(modifier = Modifier.height(16.dp))
      EditableField(
        label = "Address",
        initialValue = userInfo.address,
        validate = { true },
        onValidValue = {
          userInfo = userInfo.copy(address = it)
          userInfo.save(userFileName)
        }
      )
      Spacer(modifier = Modifier.height(16.dp))
      EditableField(
        label = "Phone number",
        initialValue = userInfo.phoneNumber,
        validate = { it.isBlank() || Patterns.PHONE.matcher(it).matches() },
        onValidValue = {
          userInfo = userInfo.copy(phoneNumber = it)
          userInfo.save(userFileName)
        }
      )
      Spacer(modifier = Modifier.height(16.dp))
      EditableField(
        label = "Email",
        initialValue = userInfo.email,
        validate = { it.isBlank() || Patterns.EMAIL_ADDRESS.matcher(it).matches() },
        onValidValue = {
          userInfo = userInfo.copy(email = it)
          userInfo.save(userFileName)
        }
      )

      Spacer(modifier = Modifier.height(24.dp))
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Dark Theme",
          style = MaterialTheme.typography.bodyLarge
        )
        Switch(
          checked = ThemeManager.isDarkTheme,
          onCheckedChange = { ThemeManager.setThemeEnabled(it) }
        )
      }
    }
  }
}

@Composable
private fun EditableField(
  label: String,
  initialValue: String,
  validate: (String) -> Boolean,
  onValidValue: (String) -> Unit,
) {
  var text by remember { mutableStateOf(initialValue) }
  var isError by remember { mutableStateOf(false) }
  var isFocused by remember { mutableStateOf(false) }

  Column {
    OutlinedTextField(
      value = text,
      onValueChange = { newText ->
        text = newText
        isError = !validate(newText)
      },
      isError = isError,
      singleLine = true,
      trailingIcon = {
        IconButton(onClick = { if (!isError) onValidValue(text) }) {
          Icon(Icons.Outlined.Edit, contentDescription = "Save $label")
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .onFocusChanged { focusState ->
          val wasFocused = isFocused
          isFocused = focusState.isFocused
          if (wasFocused && !focusState.isFocused && !isError) {
            onValidValue(text)
          }
        }
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 4.dp)
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.weight(1f)
      )
      if (isError) {
        Text(
          text = "Invalid ${label.lowercase()}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.error
        )
      }
    }
  }
}
