package com.raf.culinaryloop.data.repository

import android.content.Context
import com.raf.culinaryloop.core.common.AppLocaleHelper
import com.raf.culinaryloop.core.datastore.UserPreferencesDataSource
import com.raf.culinaryloop.domain.model.AppLocale
import com.raf.culinaryloop.domain.model.ThemeMode
import com.raf.culinaryloop.domain.model.UserSettings
import com.raf.culinaryloop.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [SettingsRepository] backed by DataStore preferences and AppLocaleHelper.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesDataSource: UserPreferencesDataSource
) : SettingsRepository {

    override fun observeSettings(): Flow<UserSettings> = preferencesDataSource.userSettingsFlow.map { settings ->
        settings.copy(appLocale = AppLocaleHelper.getCurrentLocale(context))
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) = preferencesDataSource.setThemeMode(themeMode)

    override suspend fun setAppLocale(locale: AppLocale) {
        withContext(Dispatchers.Main) {
            AppLocaleHelper.setLocale(context, locale)
        }
    }

    override suspend fun setDynamicColor(enabled: Boolean) = preferencesDataSource.setDynamicColor(enabled)

    override suspend fun setHapticFeedback(enabled: Boolean) = preferencesDataSource.setHapticFeedback(enabled)
}
