package com.example.midtermproject_24125072

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.compose.AppTheme
import com.example.midtermproject_24125072.ui.screen.AppMainScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var showSplash by rememberSaveable { mutableStateOf(true) }
            if (showSplash) {
                SplashScreen(onSplashComplete = { showSplash = false })
            } else {
                AppTheme {
                    AppMainScreen()
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        delay(2000)
        onSplashComplete()
    }
    Box(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.loadingscreen),
            contentDescription = "Loading",
            modifier = Modifier.fillMaxHeight(),
            contentScale = ContentScale.Crop
        )
    }
}
