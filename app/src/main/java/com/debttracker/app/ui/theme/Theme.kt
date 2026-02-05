package com.debttracker.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Matrix theme is always dark
private val MatrixColorScheme = darkColorScheme(
    primary = MatrixGreen,
    onPrimary = MatrixBlack,
    primaryContainer = MatrixGreenBorder,
    onPrimaryContainer = MatrixGreen,
    secondary = MatrixGreenDark,
    onSecondary = MatrixBlack,
    secondaryContainer = MatrixGreenBorderDark,
    onSecondaryContainer = MatrixGreenDark,
    tertiary = MatrixGreenDim,
    onTertiary = MatrixBlack,
    background = MatrixBlack,
    onBackground = MatrixGreen,
    surface = MatrixDark,
    onSurface = MatrixGreen,
    surfaceVariant = MatrixDark,
    onSurfaceVariant = MatrixGreenDim,
    error = MatrixRed,
    onError = MatrixBlack,
    errorContainer = MatrixRed.copy(alpha = 0.2f),
    onErrorContainer = MatrixRed,
    outline = MatrixGreenBorder,
    outlineVariant = MatrixGreenBorderDark,
    inverseSurface = MatrixGreen,
    inverseOnSurface = MatrixBlack,
    inversePrimary = MatrixGreenDark
)

@Composable
fun DebtTrackerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = MatrixColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MatrixBlack.toArgb()
            window.navigationBarColor = MatrixBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MatrixTypography,
        content = content
    )
}
