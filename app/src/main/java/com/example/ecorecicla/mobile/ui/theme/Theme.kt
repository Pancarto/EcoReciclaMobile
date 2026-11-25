
package com.example.ecorecicla.mobile.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// ==========================================
// ECORECICLA MOBILE - MATERIAL DESIGN 3
// ==========================================

// Esquema de colores para el modo claro.
private val LightColorScheme = lightColorScheme(

    primary = EcoGreenPrimary,
    onPrimary = EcoWhite,

    primaryContainer = EcoGreenLight,
    onPrimaryContainer = EcoGreenDark,

    secondary = EcoGreenDark,
    onSecondary = EcoWhite,

    secondaryContainer = EcoGreenLight,
    onSecondaryContainer = EcoGreenDark,

    tertiary = EcoGreenSecondary,
    onTertiary = EcoGreenDark,

    background = EcoBackground,
    onBackground = EcoTextPrimary,

    surface = EcoSurface,
    onSurface = EcoTextPrimary,

    surfaceVariant = EcoSurfaceVariant,
    onSurfaceVariant = EcoTextSecondary,

    outline = EcoOutline,
    outlineVariant = EcoBorder,

    error = EcoError,
    onError = EcoWhite,

    errorContainer = EcoErrorContainer,
    onErrorContainer = Color(0xFF991B1B)
)

// Esquema de colores para el modo oscuro.
private val DarkColorScheme = darkColorScheme(

    primary = EcoGreenAccent,
    onPrimary = EcoGreenDark,

    primaryContainer = EcoGreenDark,
    onPrimaryContainer = EcoGreenLight,

    secondary = EcoGreenSecondary,
    onSecondary = EcoDarkBackground,

    secondaryContainer = EcoDarkSurfaceVariant,
    onSecondaryContainer = EcoDarkText,

    tertiary = EcoGreenAccent,
    onTertiary = EcoGreenDark,

    background = EcoDarkBackground,
    onBackground = EcoDarkText,

    surface = EcoDarkSurface,
    onSurface = EcoDarkText,

    surfaceVariant = EcoDarkSurfaceVariant,
    onSurfaceVariant = EcoDarkTextSecondary,

    outline = EcoDarkOutline,
    outlineVariant = EcoDarkSurfaceVariant,

    error = EcoDarkError,
    onError = Color(0xFF7F1D1D),

    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2)
)

// Formas reutilizables para tarjetas,
// botones, formularios y ventanas.
private val EcoShapes = Shapes(

    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Tema principal de toda la aplicación.
@Composable
fun EcoReciclaMobileTheme(

    darkTheme: Boolean = isSystemInDarkTheme(),

    // Desactivado por defecto para conservar
    // la identidad visual de EcoRecicla.
    dynamicColor: Boolean = false,

    content: @Composable () -> Unit

) {

    // Selección del esquema de colores.
    val colorScheme = when {

        // Colores dinámicos disponibles
        // desde Android 12.
        dynamicColor &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {

            val context = LocalContext.current

            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColorScheme

        else -> LightColorScheme
    }

    // Aplicación global del tema Material 3.
    MaterialTheme(

        colorScheme = colorScheme,

        typography = Typography,

        shapes = EcoShapes,

        content = content
    )
}
