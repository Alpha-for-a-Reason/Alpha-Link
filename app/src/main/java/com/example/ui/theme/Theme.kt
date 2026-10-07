package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

enum class CyberThemeMode(val displayName: String) {
    CYBER_NEON("Cyber Neon (Default)"),
    HOLOGRAM_CYAN("Hologram Cyan"),
    STEALTH_OLED("Stealth OLED Black"),
    MATRIX_EMERALD("Matrix Emerald")
}

data class CyberExtendedColors(
    val cyberBackground: Color,
    val cyberGraphite: Color,
    val cyberSurface: Color,
    val neonAccent: Color,
    val secondaryAccent: Color,
    val glassBorder: Color,
    val alertRed: Color,
    val warningAmber: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val bubbleSent: Color,
    val bubbleReceived: Color
)

val LocalCyberColors = compositionLocalOf {
    CyberExtendedColors(
        cyberBackground = CyberBlack,
        cyberGraphite = CyberGraphite,
        cyberSurface = CyberGlassSurface,
        neonAccent = NeonGreenPrimary,
        secondaryAccent = HologramCyan,
        glassBorder = CyberGlassBorder,
        alertRed = CyberRedAlert,
        warningAmber = CyberAmberWarning,
        textPrimary = TextPrimaryWhite,
        textSecondary = TextSecondaryMuted,
        bubbleSent = Color(0x3300FF66),
        bubbleReceived = Color(0x331A2634)
    )
}

val LocalAnimationIntensity = compositionLocalOf { 1.0f }
val LocalReducedMotion = compositionLocalOf { false }

fun getCyberColorScheme(mode: CyberThemeMode): ColorScheme {
    return when (mode) {
        CyberThemeMode.CYBER_NEON -> darkColorScheme(
            primary = NeonGreenPrimary,
            onPrimary = Color.Black,
            secondary = HologramCyan,
            onSecondary = Color.Black,
            tertiary = NeonGreenBright,
            background = CyberBlack,
            onBackground = TextPrimaryWhite,
            surface = CyberDarkBackground,
            onSurface = TextPrimaryWhite,
            surfaceVariant = CyberGraphite,
            onSurfaceVariant = TextSecondaryMuted,
            outline = CyberGlassBorder,
            error = CyberRedAlert
        )
        CyberThemeMode.HOLOGRAM_CYAN -> darkColorScheme(
            primary = HologramCyan,
            onPrimary = Color.Black,
            secondary = NeonGreenPrimary,
            onSecondary = Color.Black,
            tertiary = Color(0xFF80D8FF),
            background = Color(0xFF040A12),
            onBackground = Color(0xFFE1F5FE),
            surface = Color(0xFF081220),
            onSurface = Color(0xFFE1F5FE),
            surfaceVariant = Color(0xFF101C2E),
            onSurfaceVariant = Color(0xFF81D4FA),
            outline = CyberGlassBorderCyan,
            error = CyberRedAlert
        )
        CyberThemeMode.STEALTH_OLED -> darkColorScheme(
            primary = NeonGreenPrimary,
            onPrimary = Color.Black,
            secondary = Color(0xFFB0BEC5),
            onSecondary = Color.Black,
            tertiary = NeonGreenMuted,
            background = Color.Black,
            onBackground = Color(0xFFEEEEEE),
            surface = Color(0xFF0D0D0D),
            onSurface = Color(0xFFEEEEEE),
            surfaceVariant = Color(0xFF161616),
            onSurfaceVariant = Color(0xFF9E9E9E),
            outline = Color(0x33FFFFFF),
            error = CyberRedAlert
        )
        CyberThemeMode.MATRIX_EMERALD -> darkColorScheme(
            primary = MutedEmerald,
            onPrimary = Color.Black,
            secondary = NeonGreenPrimary,
            onSecondary = Color.Black,
            tertiary = EmeraldDark,
            background = Color(0xFF030A05),
            onBackground = Color(0xFFE8F5E9),
            surface = Color(0xFF07140B),
            onSurface = Color(0xFFE8F5E9),
            surfaceVariant = Color(0xFF0F2415),
            onSurfaceVariant = Color(0xFFA5D6A7),
            outline = Color(0x3350B984),
            error = CyberRedAlert
        )
    }
}

fun getCyberExtendedColors(mode: CyberThemeMode): CyberExtendedColors {
    return when (mode) {
        CyberThemeMode.CYBER_NEON -> CyberExtendedColors(
            cyberBackground = CyberBlack,
            cyberGraphite = CyberGraphite,
            cyberSurface = CyberGlassSurface,
            neonAccent = NeonGreenPrimary,
            secondaryAccent = HologramCyan,
            glassBorder = CyberGlassBorder,
            alertRed = CyberRedAlert,
            warningAmber = CyberAmberWarning,
            textPrimary = TextPrimaryWhite,
            textSecondary = TextSecondaryMuted,
            bubbleSent = Color(0x2E00FF66),
            bubbleReceived = Color(0x52141C28)
        )
        CyberThemeMode.HOLOGRAM_CYAN -> CyberExtendedColors(
            cyberBackground = Color(0xFF040A12),
            cyberGraphite = Color(0xFF101C2E),
            cyberSurface = Color(0xD8081220),
            neonAccent = HologramCyan,
            secondaryAccent = NeonGreenPrimary,
            glassBorder = CyberGlassBorderCyan,
            alertRed = CyberRedAlert,
            warningAmber = CyberAmberWarning,
            textPrimary = Color(0xFFE1F5FE),
            textSecondary = Color(0xFF90CAF9),
            bubbleSent = Color(0x2E00E5FF),
            bubbleReceived = Color(0x52101C2E)
        )
        CyberThemeMode.STEALTH_OLED -> CyberExtendedColors(
            cyberBackground = Color.Black,
            cyberGraphite = Color(0xFF141414),
            cyberSurface = Color(0xDD0D0D0D),
            neonAccent = NeonGreenPrimary,
            secondaryAccent = Color(0xFFB0BEC5),
            glassBorder = Color(0x33333333),
            alertRed = CyberRedAlert,
            warningAmber = CyberAmberWarning,
            textPrimary = Color(0xFFF5F5F5),
            textSecondary = Color(0xFF9E9E9E),
            bubbleSent = Color(0x2B00FF66),
            bubbleReceived = Color(0x551A1A1A)
        )
        CyberThemeMode.MATRIX_EMERALD -> CyberExtendedColors(
            cyberBackground = Color(0xFF030A05),
            cyberGraphite = Color(0xFF0E1F13),
            cyberSurface = Color(0xD607140B),
            neonAccent = MutedEmerald,
            secondaryAccent = NeonGreenPrimary,
            glassBorder = Color(0x3350B984),
            alertRed = CyberRedAlert,
            warningAmber = CyberAmberWarning,
            textPrimary = Color(0xFFE8F5E9),
            textSecondary = Color(0xFFA5D6A7),
            bubbleSent = Color(0x2B50B984),
            bubbleReceived = Color(0x520F2415)
        )
    }
}

@Composable
fun MyApplicationTheme(
    themeMode: CyberThemeMode = CyberThemeMode.CYBER_NEON,
    animationIntensity: Float = 1.0f,
    reducedMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = getCyberColorScheme(themeMode)
    val extendedColors = getCyberExtendedColors(themeMode)

    CompositionLocalProvider(
        LocalCyberColors provides extendedColors,
        LocalAnimationIntensity provides animationIntensity,
        LocalReducedMotion provides reducedMotion
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CyberTypography,
            content = content
        )
    }
}
