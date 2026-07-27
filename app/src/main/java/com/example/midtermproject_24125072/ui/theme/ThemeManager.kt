package com.example.compose

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object ThemeManager {
    var isDarkTheme by mutableStateOf(false)
        private set

    private var prefs: android.content.SharedPreferences? = null

    fun init(context: Context) {
        if (prefs != null) return
        prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        isDarkTheme = prefs?.getBoolean("is_dark", false) ?: false
    }

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
        prefs?.edit()?.putBoolean("is_dark", isDarkTheme)?.apply()
    }

    fun setThemeEnabled(enabled: Boolean) {
        isDarkTheme = enabled
        prefs?.edit()?.putBoolean("is_dark", enabled)?.apply()
    }
}
