package com.example.play_6sem.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ToastedSugar,
    secondary = Caramel,
    tertiary = BerryJam,
    background = DarkChocolate,
    surface = Cocoa,
    onPrimary = DarkChocolate,
    onSecondary = MilkFoam,
    onTertiary = MilkFoam,
    onBackground = MilkFoam,
    onSurface = MilkFoam
)

private val LightColorScheme = lightColorScheme(
    primary = Cocoa,
    secondary = Caramel,
    tertiary = BerryJam,
    background = CookieCream,
    surface = CookieSurface,
    onPrimary = MilkFoam,
    onSecondary = DarkChocolate,
    onTertiary = MilkFoam,
    onBackground = DarkChocolate,
    onSurface = DarkChocolate
)

@Composable
fun Play_6semTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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
