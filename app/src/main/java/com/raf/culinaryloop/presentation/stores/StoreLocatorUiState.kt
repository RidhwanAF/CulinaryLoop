package com.raf.culinaryloop.presentation.stores

import androidx.compose.runtime.Immutable
import com.raf.culinaryloop.domain.model.Store
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Immutable UI State representation for Store Locator screen.
 */
@Immutable
data class StoreLocatorUiState(
    val stores: ImmutableList<Store> = persistentListOf(),
    val selectedStore: Store? = null,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val hasLocationPermission: Boolean = false,
    val userLatitude: Double = -6.2297, // Jakarta CBD default
    val userLongitude: Double = 106.8166
) {
    val filteredStores: List<Store>
        get() = if (searchQuery.isBlank()) stores
        else stores.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.city.contains(searchQuery, ignoreCase = true) ||
                    it.address.contains(searchQuery, ignoreCase = true)
        }
}
