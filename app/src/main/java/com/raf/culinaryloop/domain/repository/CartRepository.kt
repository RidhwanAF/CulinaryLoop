package com.raf.culinaryloop.domain.repository

import com.raf.culinaryloop.domain.model.Cart
import com.raf.culinaryloop.domain.model.CartItemId
import com.raf.culinaryloop.domain.model.MenuItem
import com.raf.culinaryloop.domain.model.OrderChannel
import com.raf.culinaryloop.domain.model.SelectedModifier
import com.raf.culinaryloop.domain.model.Store
import kotlinx.coroutines.flow.Flow

/**
 * Domain boundary managing user shopping cart state and line items.
 */
interface CartRepository {
    /**
     * Observes real-time cart modifications.
     */
    fun observeCart(): Flow<Cart>

    /**
     * Associates or switches the active store for the cart.
     */
    suspend fun setStore(store: Store)

    /**
     * Adds an item with customizations to the cart.
     */
    suspend fun addItem(
        menuItem: MenuItem,
        quantity: Int,
        selectedModifiers: List<SelectedModifier>,
        notes: String
    )

    /**
     * Updates the quantity of an existing cart line item.
     */
    suspend fun updateQuantity(cartItemId: CartItemId, quantity: Int)

    /**
     * Removes an item from the cart.
     */
    suspend fun removeItem(cartItemId: CartItemId)

    /**
     * Sets the fulfillment channel (DineIn, Takeaway, Delivery).
     */
    suspend fun setChannel(channel: OrderChannel)

    /**
     * Sets the dine-in table number.
     */
    suspend fun setTableNumber(tableNumber: String)

    /**
     * Clears all items in the cart.
     */
    suspend fun clearCart()
}
