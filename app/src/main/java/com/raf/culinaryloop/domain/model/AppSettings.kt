package com.raf.culinaryloop.domain.model

/**
 * Supported UI theme modes.
 */
sealed interface ThemeMode {
    val key: String

    data object FollowSystem : ThemeMode {
        override val key: String = "follow_system"
    }

    data object Light : ThemeMode {
        override val key: String = "light"
    }

    data object Dark : ThemeMode {
        override val key: String = "dark"
    }

    companion object {
        fun fromKey(key: String?): ThemeMode = when (key) {
            Light.key -> Light
            Dark.key -> Dark
            else -> FollowSystem
        }
    }
}

/**
 * Supported per-app locales.
 */
sealed interface AppLocale {
    val languageTag: String

    data object SystemDefault : AppLocale {
        override val languageTag: String = ""
    }

    data object English : AppLocale {
        override val languageTag: String = "en"
    }

    data object Indonesian : AppLocale {
        override val languageTag: String = "id"
    }

    companion object {
        fun fromTag(tag: String?): AppLocale = when (tag) {
            English.languageTag -> English
            Indonesian.languageTag -> Indonesian
            else -> SystemDefault
        }
    }
}

/**
 * Aggregated domain state for user application preferences.
 */
data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.FollowSystem,
    val appLocale: AppLocale = AppLocale.SystemDefault,
    val isDynamicColor: Boolean = false,
    val isHapticEnabled: Boolean = true
)
