package com.raf.culinaryloop.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.culinaryloop.core.common.AppResult
import com.raf.culinaryloop.domain.model.CategoryId
import com.raf.culinaryloop.domain.model.StoreId
import com.raf.culinaryloop.domain.usecase.GetCategoriesUseCase
import com.raf.culinaryloop.domain.usecase.GetMenuItemsUseCase
import com.raf.culinaryloop.domain.usecase.ObserveCartUseCase
import com.raf.culinaryloop.domain.usecase.ObserveSelectedStoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing hierarchical food/beverage catalog browsing, category filtering, and cart totals.
 */
@HiltViewModel
class MenuCatalogViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getMenuItemsUseCase: GetMenuItemsUseCase,
    private val observeSelectedStoreUseCase: ObserveSelectedStoreUseCase,
    private val observeCartUseCase: ObserveCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuCatalogUiState(isLoading = true))
    val uiState: StateFlow<MenuCatalogUiState> = _uiState.asStateFlow()

    init {
        observeActiveStore()
        observeCartState()
    }

    private fun observeActiveStore() {
        viewModelScope.launch {
            observeSelectedStoreUseCase().collect { store ->
                if (store != null) {
                    _uiState.update { it.copy(activeStore = store) }
                    loadCatalog(store.id, _uiState.value.selectedCategoryId)
                }
            }
        }
    }

    private fun observeCartState() {
        viewModelScope.launch {
            observeCartUseCase().collect { cart ->
                _uiState.update {
                    it.copy(
                        cartItemCount = cart.totalItemCount,
                        cartGrandTotal = cart.grandTotal
                    )
                }
            }
        }
    }

    fun loadCatalog(storeId: StoreId, categoryId: CategoryId?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedCategoryId = categoryId) }

            // Fetch categories if not yet populated
            if (_uiState.value.categories.isEmpty()) {
                when (val catResult = getCategoriesUseCase(storeId)) {
                    is AppResult.Success -> {
                        _uiState.update { it.copy(categories = catResult.data.toImmutableList()) }
                    }
                    is AppResult.Failure -> {}
                }
            }

            // Fetch menu items
            when (val itemsResult = getMenuItemsUseCase(storeId, categoryId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            menuItems = itemsResult.data.toImmutableList(),
                            isLoading = false
                        )
                    }
                }
                is AppResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onCategorySelected(categoryId: CategoryId?) {
        val storeId = _uiState.value.activeStore?.id ?: StoreId("esb_branch_001")
        loadCatalog(storeId, categoryId)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
