package com.yargisoft.pomodoroapp.ui.theme
// app/src/main/java/com/seninuygulamaninadi/ui/theme/Color.kt

import androidx.compose.ui.graphics.Color

// Çalışma modu için odaklanmayı destekleyen bir renk
val WorkRed = Color(0xFFD32F2F) // Koyu Kırmızı
val WorkRedDark = Color(0xFF9A0007) // Daha Koyu Kırmızı

// Mola modu için dinlendirici bir renk
val BreakGreen = Color(0xFF689F38) // Koyu Yeşil
val BreakGreenDark = Color(0xFF387002) // Daha Koyu Yeşil

// Genel UI öğeleri ve arka plan için
val Grey80 = Color(0xFF424242)
val Grey20 = Color(0xFFEEEEEE)
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

// Malzeme Teması için renk şeması (Light ve Dark Mode uyumlu)
val md_theme_light_primary = WorkRed
val md_theme_light_onPrimary = White
val md_theme_light_primaryContainer = Color(0xFFFFDAD4)
val md_theme_light_onPrimaryContainer = Color(0xFF410002)
val md_theme_light_secondary = BreakGreen
val md_theme_light_onSecondary = White
val md_theme_light_secondaryContainer = Color(0xFFDAE6CC)
val md_theme_light_onSecondaryContainer = Color(0xFF1B3600)
val md_theme_light_tertiary = Color(0xFF006C4F)
val md_theme_light_onTertiary = White
val md_theme_light_tertiaryContainer = Color(0xFF8CF8CB)
val md_theme_light_onTertiaryContainer = Color(0xFF002116)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_onError = White
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Grey20 // Açık modda açık gri arka plan
val md_theme_light_onBackground = Black
val md_theme_light_surface = White // Yüzeyler için beyaz
val md_theme_light_onSurface = Black
val md_theme_light_surfaceVariant = Color(0xFFF5DDDB)
val md_theme_light_onSurfaceVariant = Color(0xFF534341)
val md_theme_light_outline = Color(0xFF857371)
val md_theme_light_inverseOnSurface = Color(0xFFFBEEEC)
val md_theme_light_inverseSurface = Color(0xFF362F2E)
val md_theme_light_inversePrimary = Color(0xFFFFB4AA)
val md_theme_light_surfaceTint = WorkRed
val md_theme_light_outlineVariant = Color(0xFFD8C2BF)
val md_theme_light_scrim = Black

val md_theme_dark_primary = Color(0xFFFFB4AA) // Karanlık modda açık kırmızı
val md_theme_dark_onPrimary = Color(0xFF690005)
val md_theme_dark_primaryContainer = Color(0xFF93000C)
val md_theme_dark_onPrimaryContainer = Color(0xFFFFDAD4)
val md_theme_dark_secondary = Color(0xFFBEDA97) // Karanlık modda açık yeşil
val md_theme_dark_onSecondary = Color(0xFF315300)
val md_theme_dark_secondaryContainer = Color(0xFF496C00)
val md_theme_dark_onSecondaryContainer = Color(0xFFDAE6CC)
val md_theme_dark_tertiary = Color(0xFF70DBAF)
val md_theme_dark_onTertiary = Color(0xFF003827)
val md_theme_dark_tertiaryContainer = Color(0xFF00513B)
val md_theme_dark_onTertiaryContainer = Color(0xFF8CF8CB)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Grey80 // Karanlık modda koyu gri arka plan
val md_theme_dark_onBackground = White
val md_theme_dark_surface = Color(0xFF201A19)
val md_theme_dark_onSurface = Color(0xFFEDE0DE)
val md_theme_dark_surfaceVariant = Color(0xFF534341)
val md_theme_dark_onSurfaceVariant = Color(0xFFD8C2BF)
val md_theme_dark_outline = Color(0xFF9F8C8A)
val md_theme_dark_inverseOnSurface = Color(0xFF201A19)
val md_theme_dark_inverseSurface = Color(0xFFEDE0DE)
val md_theme_dark_inversePrimary = Color(0xFFB3261E)
val md_theme_dark_surfaceTint = Color(0xFFFFB4AA)
val md_theme_dark_outlineVariant = Color(0xFF534341)
val md_theme_dark_scrim = Black