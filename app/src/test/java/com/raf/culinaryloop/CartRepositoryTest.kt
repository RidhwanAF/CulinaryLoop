package com.raf.culinaryloop

import com.raf.culinaryloop.data.datasource.MockEsbData
import com.raf.culinaryloop.data.model.toDomain
import com.raf.culinaryloop.data.repository.CartRepositoryImpl
import com.raf.culinaryloop.domain.model.OrderChannel
import com.raf.culinaryloop.domain.model.SelectedModifier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CartRepositoryTest {

    private lateinit var cartRepository: CartRepositoryImpl

    @Before
    fun setUp() {
        cartRepository = CartRepositoryImpl()
    }

    @Test
    fun testInitialCartIsEmpty() = runTest {
        val cart = cartRepository.observeCart().first()
        assertTrue(cart.isEmpty)
        assertEquals(0, cart.totalItemCount)
        assertEquals(0L, cart.grandTotal)
    }

    @Test
    fun testAddItemCalculatesModifiersAndTotal() = runTest {
        val store = MockEsbData.stores.first().toDomain()
        cartRepository.setStore(store)

        val menuItem = MockEsbData.menuItems.first().toDomain() // Gula Aren Espresso Macchiato (Rp 38,000)
        val oatMilkModifier = SelectedModifier(
            groupId = "mod_milk",
            groupTitle = "Milk Choice",
            optionId = "opt_milk_oat",
            optionName = "Oatside Barista Oat Milk",
            priceDelta = 8000L
        )

        cartRepository.addItem(
            menuItem = menuItem,
            quantity = 2,
            selectedModifiers = listOf(oatMilkModifier),
            notes = "Less ice"
        )

        val cart = cartRepository.observeCart().first()
        assertFalse(cart.isEmpty)
        assertEquals(2, cart.totalItemCount)
        assertEquals(1, cart.items.size)

        val cartItem = cart.items.first()
        // Unit price = 38,000 + 8,000 = 46,000
        assertEquals(46000L, cartItem.unitPrice)
        // Total line price = 46,000 * 2 = 92,000
        assertEquals(92000L, cartItem.totalPrice)

        // Subtotal = 92,000
        assertEquals(92000L, cart.subtotal)
        // PB1 Tax 10% = 9,200
        assertEquals(9200L, cart.tax)
        // Service Charge 5% = 4,600
        assertEquals(4600L, cart.serviceCharge)
        // Grand Total = 92,000 + 9,200 + 4,600 = 105,800
        assertEquals(105800L, cart.grandTotal)
    }

    @Test
    fun testUpdateQuantityAndRemove() = runTest {
        val menuItem = MockEsbData.menuItems[1].toDomain()
        cartRepository.addItem(
            menuItem = menuItem,
            quantity = 1,
            selectedModifiers = emptyList(),
            notes = ""
        )

        val cart = cartRepository.observeCart().first()
        val itemId = cart.items.first().id

        // Increase quantity
        cartRepository.updateQuantity(itemId, 3)
        var updatedCart = cartRepository.observeCart().first()
        assertEquals(3, updatedCart.totalItemCount)

        // Reduce to 0 removes item
        cartRepository.updateQuantity(itemId, 0)
        updatedCart = cartRepository.observeCart().first()
        assertTrue(updatedCart.isEmpty)
    }

    @Test
    fun testChannelAndTableAssignment() = runTest {
        cartRepository.setChannel(OrderChannel.Takeaway)
        cartRepository.setTableNumber("Table 12")

        val cart = cartRepository.observeCart().first()
        assertEquals(OrderChannel.Takeaway, cart.channel)
        assertEquals("Table 12", cart.tableNumber)
    }
}
