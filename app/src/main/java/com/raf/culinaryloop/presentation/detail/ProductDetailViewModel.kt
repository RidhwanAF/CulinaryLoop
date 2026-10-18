package com.raf.culinaryloop.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.culinaryloop.core.common.AppResult
import com.raf.culinaryloop.domain.model.MenuItemId
import com.raf.culinaryloop.domain.model.ModifierGroup
import com.raf.culinaryloop.domain.model.ModifierOption
import com.raf.culinaryloop.domain.model.SelectedModifier
import com.raf.culinaryloop.domain.usecase.AddToCartUseCase
import com.raf.culinaryloop.domain.usecase.GetMenuItemDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel orchestrating dynamic modifier selections, live price computation, and cart commitment.
 */
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getMenuItemDetailUseCase: GetMenuItemDetailUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState(isLoading = true))
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadItem(itemId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getMenuItemDetailUseCase(MenuItemId(itemId))) {
                is AppResult.Success -> {
                    val item = result.data
                    // Select default options for required groups
                    val defaultModifiers = mutableListOf<SelectedModifier>()
                    item.modifierGroups.forEach { group ->
                        if (group.isRequired && group.options.isNotEmpty()) {
                            val defaultOpt = group.options.first()
                            defaultModifiers.add(
                                SelectedModifier(
                                    groupId = group.id,
                                    groupTitle = group.title,
                                    optionId = defaultOpt.id,
                                    optionName = defaultOpt.name,
                                    priceDelta = defaultOpt.priceDelta
                                )
                            )
                        }
                    }

                    val unitPrice = item.basePrice + defaultModifiers.sumOf { it.priceDelta }
                    _uiState.update {
                        it.copy(
                            item = item,
                            quantity = 1,
                            selectedModifiers = defaultModifiers.toImmutableList(),
                            unitPrice = unitPrice,
                            totalPrice = unitPrice,
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

    fun onToggleModifier(group: ModifierGroup, option: ModifierOption) {
        val currentModifiers = _uiState.value.selectedModifiers.toMutableList()
        val existingInGroup = currentModifiers.filter { it.groupId == group.id }

        if (group.maxSelections == 1) {
            // Radio button replacement
            currentModifiers.removeAll { it.groupId == group.id }
            currentModifiers.add(
                SelectedModifier(
                    groupId = group.id,
                    groupTitle = group.title,
                    optionId = option.id,
                    optionName = option.name,
                    priceDelta = option.priceDelta
                )
            )
        } else {
            // Multi-selection toggle
            val alreadySelected = currentModifiers.find { it.groupId == group.id && it.optionId == option.id }
            if (alreadySelected != null) {
                if (!group.isRequired || existingInGroup.size > group.minSelections) {
                    currentModifiers.remove(alreadySelected)
                }
            } else {
                if (existingInGroup.size < group.maxSelections) {
                    currentModifiers.add(
                        SelectedModifier(
                            groupId = group.id,
                            groupTitle = group.title,
                            optionId = option.id,
                            optionName = option.name,
                            priceDelta = option.priceDelta
                        )
                    )
                }
            }
        }

        val basePrice = _uiState.value.item?.basePrice ?: 0L
        val unitPrice = basePrice + currentModifiers.sumOf { it.priceDelta }
        val qty = _uiState.value.quantity

        _uiState.update {
            it.copy(
                selectedModifiers = currentModifiers.toImmutableList(),
                unitPrice = unitPrice,
                totalPrice = unitPrice * qty
            )
        }
    }

    fun onQuantityChange(newQuantity: Int) {
        if (newQuantity < 1) return
        val unitPrice = _uiState.value.unitPrice
        _uiState.update {
            it.copy(
                quantity = newQuantity,
                totalPrice = unitPrice * newQuantity
            )
        }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun onAddToCart(onSuccess: () -> Unit) {
        val item = _uiState.value.item ?: return
        viewModelScope.launch {
            addToCartUseCase(
                menuItem = item,
                quantity = _uiState.value.quantity,
                selectedModifiers = _uiState.value.selectedModifiers,
                notes = _uiState.value.notes
            )
            _uiState.update { it.copy(isAddedToCart = true) }
            onSuccess()
        }
    }
}
