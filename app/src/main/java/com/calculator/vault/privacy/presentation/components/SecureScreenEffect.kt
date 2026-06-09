package com.calculator.vault.privacy.presentation.components

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun SecureScreenEffect(enabled: Boolean = true) {
    val view = LocalView.current
    DisposableEffect(enabled) {
        val window = (view.context as? android.app.Activity)?.window
        if (enabled && window != null) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE,
            )
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
