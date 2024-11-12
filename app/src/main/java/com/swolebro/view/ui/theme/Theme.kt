package com.swolebro.view.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.swolebro.R

// Dark Colors
val GreyDark = Color(0xFF222222)
val GreyDarker = Color(0xFF2B2B2B)
val GreyLight = Color(0xFFC4C4C4)
val YellowNeon = Color(0xFFEFFF33)
val YellowLight = Color(0xFFFDFFE6)
val RedNeon = Color(0xFFEF0022)
val RedError = Color(0xFFFF5757)
val BlueNeon = Color(0xFF22EEC8)
val GreenNeon = Color(0xFF42da05)

val Neons = arrayOf(BlueNeon, GreenNeon, YellowNeon, RedNeon)

// Light Colors
val White = Color(0xFFF9F9F9)
val GreyLighter = Color(0xFFF1F0F0)
val Green = Color(0xFF45D74B)
val GreenLight = Color(0xFF45D74B)
val BlueLight = Color(0xFF55C2FF)
val BlueDark = Color(0xFF00A4FF)

private val DarkColorScheme = darkColorScheme(
    background = GreyDark,
    onBackground = Color.White,
    primaryContainer = Color.White,
    onPrimaryContainer = Color.Black,
    secondaryContainer = Color.Black,
    onSecondaryContainer = YellowNeon,
    surfaceVariant = GreyDarker,
    onSurfaceVariant = GreyLight,
    primary = YellowNeon,
    secondary = YellowLight,
    tertiary = BlueNeon,
    error = RedError,
)

private val LightColorScheme = lightColorScheme(
    background = White,
    onBackground = Color.Black,
    primaryContainer = GreyLighter,
    onPrimaryContainer = Color.Black,
    secondaryContainer = GreyLighter,
    onSecondaryContainer = BlueDark,
    surfaceVariant = GreyLighter,
    onSurfaceVariant = GreyDark,
    primary = BlueDark,
    secondary = BlueLight,
    tertiary = Green,
    error = RedError,
)

val russoOneFamily = FontFamily(
    Font(R.font.russo_one_regular, FontWeight.Normal),
)

@Composable
fun SwolebroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val swoleBroTypography = Typography(
        titleLarge = TextStyle(
            fontFamily = russoOneFamily,
            fontSize = 48.sp,
            color = colorScheme.primary,
        ),
        labelLarge = TextStyle(
            fontFamily = russoOneFamily,
            fontSize = 24.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = russoOneFamily,
            fontSize = 18.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = russoOneFamily,
            fontSize = 14.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = russoOneFamily,
            fontSize = 18.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = russoOneFamily,
            fontSize = 14.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = russoOneFamily,
            fontSize = 10.sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = russoOneFamily,
            fontSize = 48.sp,
            color = colorScheme.primary,
        ),
        headlineMedium = TextStyle(
            fontFamily = russoOneFamily,
            fontSize = 24.sp,
            color = colorScheme.secondary,
        ),
        headlineSmall = TextStyle(
            fontFamily = russoOneFamily,
            fontSize = 14.sp,
            color = colorScheme.secondary,
        ),
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = swoleBroTypography,
        content = content
    )
}