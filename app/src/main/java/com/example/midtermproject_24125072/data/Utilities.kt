package com.example.midtermproject_24125072.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


@Composable
fun getWorkingDir(): String {
  val context = LocalContext.current
  return context.filesDir.absolutePath

}