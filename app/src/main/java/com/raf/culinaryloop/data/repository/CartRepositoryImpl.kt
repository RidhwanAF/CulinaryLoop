package com.raf.culinaryloop.data.repository

import com.raf.culinaryloop.domain.model.Cart
import com.raf.culinaryloop.domain.model.CartItem
import com.raf.culinaryloop.domain.model.CartItemId
import com.raf.culinaryloop.domain.model.MenuItem
import com.raf.culinaryloop.domain.model.OrderChannel
import com.raf.culinaryloop.domain.model.SelectedModifier
import com.raf.culinaryloop.domain.model.Store
import com.raf.culinaryloop.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory reactive implementation of [CartRepository].
 */
@Singleton
class CartRepositoryImpl @Inject constructor() : CartRepository {

    private val cartStateFlow = MutableStateFlow(Cart())

    override fun observeCart(): Flow<Cart> = cartStateFlow.asStateFlow()

    override suspend fun setStore(store: Store) {
        cartStateFlow.update { current ->
            if (current.store?.id != store.id) {
                // If switching stores, retain store and clear items to prevent cross-outlet ordering
                current.copy(store = store, items = emptyList())
            } else {
                current.copy(store = store)
            }
        }
    }

    override suspend fun addItem(
        menuItem: MenuItem,
        quantity: Int,
        selectedModifiers: List<SelectedModifier>,
        notes: String
    ) {
        cartStateFlow.update { current ->
            val newItem = CartItem(
                id = CartItemId(UUID.randomUUID().toString()),
                menuItem = menuItem,
                quantity = quantity,
                selectedModifiers = selectedModifiers,
                notes = notes
            )
            current.copy(items = current.items + newItem)
        }
    }

    override suspend fun updateQuantity(cartItemId: CartItemId, quantity: Int) {
        cartStateFlow.update { current ->
            if (quantity <= 0) {
                current.copy(items = current.items.filterNot { it.id == cartItemId })
            } else {
                current.copy(
                    items = current.items.map { item ->
                        if (item.id == cartItemId) item.copy(quantity = quantity) else item
                    }
                )
            }
        }
    }

    override suspend fun removeItem(cartItemId: CartItemId) {
        cartStateFlow.update { current ->
            current.copy(items = current.items.filterNot { it.id == cartItemId })
        }
    }

    override suspend fun setChannel(channel: OrderChannel) {
        cartStateFlow.update { current ->
            current.copy(channel = channel)
        }
    }

    override suspend fun setTableNumber(tableNumber: String) {
        cartStateFlow.update { current ->
            current.copy(tableNumber = tableNumber)
        }
    }

    override suspend fun clearCart() {
        cartStateFlow.update { current ->
            current.copy(items = emptyList())
        }
    }
}
