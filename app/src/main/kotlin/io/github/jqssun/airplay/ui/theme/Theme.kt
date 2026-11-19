package io.github.jqssun.airplay.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Rodemap brand palette: deep navy base (#070B16, the Rodemap icon ground) with the
// sky-blue accent drawn from the Rodemap route mark's gradient; violet and cyan are
// its secondary stops. Rodemap Air is a dark, TV-first product, so it always renders
// in this scheme — no dynamic color, no light variant.
private val RodemapColorScheme = darkColorScheme(
    primary = Color(0xFF54B9F5),
    onPrimary = Color(0xFF04263F),
    primaryContainer = Color(0xFF123650),
    onPrimaryContainer = Color(0xFFCDE9FF),
    secondary = Color(0xFF9D8CFF),
    onSecondary = Color(0xFF1D1440),
    secondaryContainer = Color(0xFF2A2154),
    onSecondaryContainer = Color(0xFFE2DEFF),
    tertiary = Color(0xFF8CE8F5),
    onTertiary = Color(0xFF00363D),
    tertiaryContainer = Color(0xFF0E3A44),
    onTertiaryContainer = Color(0xFFC4F4FD),
    background = Color(0xFF070B16),
    onBackground = Color(0xFFE8EEF7),
    surface = Color(0xFF0B1120),
    onSurface = Color(0xFFE8EEF7),
    surfaceVariant = Color(0xFF141D31),
    onSurfaceVariant = Color(0xFFA7B4C8),
    surfaceContainerLowest = Color(0xFF05080F),
    surfaceContainerLow = Color(0xFF0A0F1D),
    surfaceContainer = Color(0xFF0E1526),
    surfaceContainerHigh = Color(0xFF131C30),
    surfaceContainerHighest = Color(0xFF182338),
    outline = Color(0xFF44506A),
    outlineVariant = Color(0xFF232E47),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    inverseSurface = Color(0xFFE8EEF7),
    inverseOnSurface = Color(0xFF0B1120),
    inversePrimary = Color(0xFF0F6AA8),
    scrim = Color(0xFF000000)
)

@Composable
fun AirPlayTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    // darkTheme is accepted for call-site compatibility; the Rodemap scheme is always dark.
    MaterialTheme(colorScheme = RodemapColorScheme, content = content)
}
