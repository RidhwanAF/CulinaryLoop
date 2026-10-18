package com.raf.culinaryloop.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.culinaryloop.core.common.AppResult
import com.raf.culinaryloop.domain.model.CartItemId
import com.raf.culinaryloop.domain.model.OrderChannel
import com.raf.culinaryloop.domain.usecase.ClearCartUseCase
import com.raf.culinaryloop.domain.usecase.ObserveCartUseCase
import com.raf.culinaryloop.domain.usecase.RemoveFromCartUseCase
import com.raf.culinaryloop.domain.usecase.SetOrderChannelUseCase
import com.raf.culinaryloop.domain.usecase.SetTableNumberUseCase
import com.raf.culinaryloop.domain.usecase.SubmitOrderUseCase
import com.raf.culinaryloop.domain.usecase.UpdateCartQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel handling cart modifications, breakdown calculations, and order dispatch simulation.
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val observeCartUseCase: ObserveCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val setOrderChannelUseCase: SetOrderChannelUseCase,
    private val setTableNumberUseCase: SetTableNumberUseCase,
    private val submitOrderUseCase: SubmitOrderUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            observeCartUseCase().collect { cart ->
                _uiState.update { it.copy(cart = cart) }
            }
        }
    }

    fun onQuantityChange(cartItemId: CartItemId, quantity: Int) {
        viewModelScope.launch {
            updateCartQuantityUseCase(cartItemId, quantity)
        }
    }

    fun onRemoveItem(cartItemId: CartItemId) {
        viewModelScope.launch {
            removeFromCartUseCase(cartItemId)
        }
    }

    fun onChannelSelected(channel: OrderChannel) {
        viewModelScope.launch {
            setOrderChannelUseCase(channel)
        }
    }

    fun onTableNumberChange(tableNumber: String) {
        viewModelScope.launch {
            setTableNumberUseCase(tableNumber)
        }
    }

    fun onSubmitOrder() {
        val currentCart = _uiState.value.cart
        if (currentCart.isEmpty) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            when (val result = submitOrderUseCase(currentCart)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            placedOrder = result.data,
                            errorMessage = null
                        )
                    }
                    clearCartUseCase()
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = "Simulation error: ${result.error}"
                        )
                    }
                }
            }
        }
    }

    fun onDismissOrderSuccess() {
        _uiState.update { it.copy(placedOrder = null) }
    }
}
