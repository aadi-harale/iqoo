package com.iqoo.guardian.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Guardian type scale. Uses the platform sans family so the demo has zero font
 * download cost and renders identically on any device we present from.
 */
private val Sans = FontFamily.SansSerif

val GuardianTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Light,
        fontSize = 64.sp, lineHeight = 66.sp, letterSpacing = (-2).sp
    ),
    displayMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Light,
        fontSize = 48.sp, lineHeight = 52.sp, letterSpacing = (-1.5).sp
    ),
    displaySmall = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
        fontSize = 34.sp, lineHeight = 40.sp, letterSpacing = (-1).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
        fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = (-0.8).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Medium,
        fontSize = 24.sp, lineHeight = 30.sp, letterSpacing = (-0.4).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Medium,
        fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = (-0.2).sp
    ),
    titleLarge = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp, lineHeight = 23.sp, letterSpacing = (-0.1).sp
    ),
    titleMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Medium,
        fontSize = 15.sp, lineHeight = 21.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
        fontSize = 15.sp, lineHeight = 23.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp, lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 17.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp, lineHeight = 17.sp, letterSpacing = 0.2.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 1.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Medium,
        fontSize = 10.sp, lineHeight = 13.sp, letterSpacing = 1.2.sp
    )
)
