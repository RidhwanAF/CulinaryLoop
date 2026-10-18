package com.raf.culinaryloop.core.common

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.raf.culinaryloop.domain.model.AppLocale

/**
 * Authoritative single source of truth for per-app language localization.
 * Directly interrogates platform LocaleManager (Android 13+ / API 33+) and AppCompatDelegate.
 * Never manually stored in DataStore.
 */
object AppLocaleHelper {

    /**
     * Resolves the current active per-app locale directly from system and AppCompat.
     */
    fun getCurrentLocale(context: Context): AppLocale {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(LocaleManager::class.java)
            val appLocales = localeManager?.applicationLocales
            if (appLocales != null && !appLocales.isEmpty) {
                val tag = appLocales.toLanguageTags()
                return parseTag(tag)
            }
        }

        val appCompatLocales = AppCompatDelegate.getApplicationLocales()
        if (!appCompatLocales.isEmpty) {
            val tag = appCompatLocales.toLanguageTags()
            return parseTag(tag)
        }

        return AppLocale.SystemDefault
    }

    /**
     * Updates per-app language directly in AppCompatDelegate and LocaleManager.
     */
    fun setLocale(context: Context, locale: AppLocale) {
        val tag = locale.languageTag
        val localeListCompat = if (tag.isEmpty()) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(tag)
        }
        AppCompatDelegate.setApplicationLocales(localeListCompat)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(LocaleManager::class.java)
            val platformLocales = if (tag.isEmpty()) {
                LocaleList.getEmptyLocaleList()
            } else {
                LocaleList.forLanguageTags(tag)
            }
            localeManager?.applicationLocales = platformLocales
        }
    }

    private fun parseTag(tag: String): AppLocale = when {
        tag.startsWith("id", ignoreCase = true) || tag.startsWith(
            "in",
            ignoreCase = true
        ) -> AppLocale.Indonesian

        tag.startsWith("en", ignoreCase = true) -> AppLocale.English
        else -> AppLocale.SystemDefault
    }
}
