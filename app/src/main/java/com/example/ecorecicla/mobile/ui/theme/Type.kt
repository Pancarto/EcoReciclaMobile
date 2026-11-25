
package com.example.ecorecicla.mobile.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ==========================================
// ECORECICLA MOBILE - TIPOGRAFÍA
// ==========================================

// Familia tipográfica principal.
// Utilizamos la fuente nativa de Android.
private val EcoFontFamily = FontFamily.SansSerif

val Typography = Typography(

    // Valores destacados y estadísticas.
    displayLarge = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp
    ),

    displayMedium = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 48.sp
    ),

    displaySmall = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),

    // Encabezados principales.
    headlineLarge = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 38.sp
    ),

    headlineMedium = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 34.sp
    ),

    headlineSmall = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp
    ),

    // Títulos de tarjetas y secciones.
    titleLarge = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),

    titleMedium = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),

    titleSmall = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),

    // Textos informativos.
    bodyLarge = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.3.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),

    bodySmall = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    // Textos de botones y etiquetas.
    labelLarge = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    ),

    labelMedium = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    labelSmall = TextStyle(
        fontFamily = EcoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)
