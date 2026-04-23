package com.example.animationexpo.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TravelOrangeBright,
    secondary = JourneyBlue,
    tertiary = CanvasMist,
    background = Graphite,
    surface = Color(0xFF2C2824),
    onPrimary = PaperWhite,
    onSecondary = PaperWhite,
    onTertiary = Ink,
    onBackground = PaperWhite,
    onSurface = PaperWhite
)

private val LightColorScheme = lightColorScheme(
    primary = TravelOrange,
    secondary = JourneyBlue,
    tertiary = CanvasMist,
    background = CanvasWarm,
    surface = PaperWhite,
    onPrimary = PaperWhite,
    onSecondary = PaperWhite,
    onTertiary = Ink,
    onBackground = Ink,
    onSurface = Ink
)

@Composable
fun AnimationExpoTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
