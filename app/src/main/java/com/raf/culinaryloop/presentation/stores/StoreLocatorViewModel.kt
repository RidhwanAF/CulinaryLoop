package com.raf.culinaryloop.presentation.stores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.culinaryloop.core.common.AppResult
import com.raf.culinaryloop.domain.model.Store
import com.raf.culinaryloop.domain.usecase.GetStoresUseCase
import com.raf.culinaryloop.domain.usecase.ObserveSelectedStoreUseCase
import com.raf.culinaryloop.domain.usecase.ObserveStoresUseCase
import com.raf.culinaryloop.domain.usecase.SelectStoreUseCase
import com.raf.culinaryloop.domain.usecase.SetCartStoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * ViewModel orchestrating store outlet branch discovery, interactive map selection, and geolocation.
 */
@HiltViewModel
class StoreLocatorViewModel @Inject constructor(
    private val observeStoresUseCase: ObserveStoresUseCase,
    private val getStoresUseCase: GetStoresUseCase,
    private val selectStoreUseCase: SelectStoreUseCase,
    private val observeSelectedStoreUseCase: ObserveSelectedStoreUseCase,
    private val setCartStoreUseCase: SetCartStoreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreLocatorUiState(isLoading = true))
    val uiState: StateFlow<StoreLocatorUiState> = _uiState.asStateFlow()

    init {
        loadStores()
        observeSelectedStore()
    }

    fun loadStores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getStoresUseCase()) {
                is AppResult.Success -> {
                    val storeList = result.data.toImmutableList()
                    val defaultSelected = _uiState.value.selectedStore ?: storeList.firstOrNull()
                    _uiState.update {
                        it.copy(
                            stores = storeList,
                            selectedStore = defaultSelected,
                            isLoading = false
                        )
                    }
                    if (defaultSelected != null) {
                        setCartStoreUseCase(defaultSelected)
                    }
                }
                is AppResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun observeSelectedStore() {
        viewModelScope.launch {
            observeSelectedStoreUseCase().collect { store ->
                if (store != null) {
                    _uiState.update { it.copy(selectedStore = store) }
                }
            }
        }
    }

    fun onSelectStore(store: Store) {
        viewModelScope.launch {
            selectStoreUseCase(store)
            setCartStoreUseCase(store)
            _uiState.update { it.copy(selectedStore = store) }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onLocationPermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(hasLocationPermission = granted) }
    }

    fun onUserLocationUpdated(latitude: Double, longitude: Double) {
        _uiState.update {
            it.copy(
                userLatitude = latitude,
                userLongitude = longitude,
                hasLocationPermission = true
            )
        }
    }

    fun calculateDistanceKm(store: Store): Double {
        val userLat = _uiState.value.userLatitude
        val userLng = _uiState.value.userLongitude
        val r = 6371.0 // Radius of earth in km
        val dLat = Math.toRadians(store.coordinates.latitude - userLat)
        val dLng = Math.toRadians(store.coordinates.longitude - userLng)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(userLat)) * cos(Math.toRadians(store.coordinates.latitude)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
