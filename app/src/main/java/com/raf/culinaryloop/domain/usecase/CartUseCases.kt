package com.raf.culinaryloop.domain.usecase

import com.raf.culinaryloop.domain.model.Cart
import com.raf.culinaryloop.domain.model.CartItemId
import com.raf.culinaryloop.domain.model.MenuItem
import com.raf.culinaryloop.domain.model.OrderChannel
import com.raf.culinaryloop.domain.model.SelectedModifier
import com.raf.culinaryloop.domain.model.Store
import com.raf.culinaryloop.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UC-03: Observes reactive cart state.
 */
class ObserveCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    operator fun invoke(): Flow<Cart> = repository.observeCart()
}

/**
 * UC-03: Adds customized item to active cart.
 */
class AddToCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(
        menuItem: MenuItem,
        quantity: Int,
        selectedModifiers: List<SelectedModifier>,
        notes: String
    ) = repository.addItem(menuItem, quantity, selectedModifiers, notes)
}

/**
 * UC-03: Adjusts quantity of existing cart item.
 */
class UpdateCartQuantityUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(cartItemId: CartItemId, quantity: Int) =
        repository.updateQuantity(cartItemId, quantity)
}

/**
 * UC-03: Removes an item from the cart.
 */
class RemoveFromCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(cartItemId: CartItemId) = repository.removeItem(cartItemId)
}

/**
 * UC-03: Switches fulfillment channel.
 */
class SetOrderChannelUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(channel: OrderChannel) = repository.setChannel(channel)
}

/**
 * UC-03: Sets dine-in table number.
 */
class SetTableNumberUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(tableNumber: String) = repository.setTableNumber(tableNumber)
}

/**
 * UC-03: Associates store with cart.
 */
class SetCartStoreUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(store: Store) = repository.setStore(store)
}

/**
 * UC-03: Clears cart.
 */
class ClearCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke() = repository.clearCart()
}
