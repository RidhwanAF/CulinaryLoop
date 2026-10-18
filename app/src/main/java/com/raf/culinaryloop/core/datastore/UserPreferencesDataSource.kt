package com.raf.culinaryloop.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.raf.culinaryloop.core.common.AppLocaleHelper
import com.raf.culinaryloop.domain.model.ThemeMode
import com.raf.culinaryloop.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * DataStore layer persisting user appearance and tactile haptic preferences.
 * Localization (per-app language) is strictly delegated to AppCompatDelegate as single source of truth.
 */
@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val context: Context,
) {
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val IS_DYNAMIC_COLOR = booleanPreferencesKey("is_dynamic_color")
        val IS_HAPTIC_ENABLED = booleanPreferencesKey("is_haptic_enabled")
    }

    val userSettingsFlow: Flow<UserSettings> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeKey = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.FollowSystem.key
            val dynamicColor = preferences[PreferencesKeys.IS_DYNAMIC_COLOR] ?: false
            val haptics = preferences[PreferencesKeys.IS_HAPTIC_ENABLED] ?: true

            UserSettings(
                themeMode = ThemeMode.fromKey(themeKey),
                appLocale = AppLocaleHelper.getCurrentLocale(context),
                isDynamicColor = dynamicColor,
                isHapticEnabled = haptics
            )
        }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.key
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_HAPTIC_ENABLED] = enabled
        }
    }
}
