package com.raf.culinaryloop.presentation.cart

import androidx.compose.runtime.Immutable
import com.raf.culinaryloop.domain.model.Cart
import com.raf.culinaryloop.domain.model.Order

/**
 * Immutable UI State representation for Cart and Checkout simulation.
 */
@Immutable
data class CartUiState(
    val cart: Cart = Cart(),
    val isSubmitting: Boolean = false,
    val placedOrder: Order? = null,
    val errorMessage: String? = null
)
