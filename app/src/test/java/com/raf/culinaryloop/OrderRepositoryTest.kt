package com.raf.culinaryloop

import com.raf.culinaryloop.core.common.AppResult
import com.raf.culinaryloop.core.common.DefaultDispatcherProvider
import com.raf.culinaryloop.data.datasource.EsbRemoteDataSource
import com.raf.culinaryloop.data.datasource.MockEsbData
import com.raf.culinaryloop.data.model.CreateOrderRequestDto
import com.raf.culinaryloop.data.model.MenuCategoryDto
import com.raf.culinaryloop.data.model.MenuItemDto
import com.raf.culinaryloop.data.model.OrderResponseDto
import com.raf.culinaryloop.data.model.StoreDto
import com.raf.culinaryloop.data.model.toDomain
import com.raf.culinaryloop.data.repository.OrderRepositoryImpl
import com.raf.culinaryloop.domain.model.Cart
import com.raf.culinaryloop.domain.model.CartItem
import com.raf.culinaryloop.domain.model.CartItemId
import com.raf.culinaryloop.domain.model.OrderChannel
import com.raf.culinaryloop.domain.model.OrderId
import com.raf.culinaryloop.domain.model.OrderStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OrderRepositoryTest {

    private lateinit var orderRepository: OrderRepositoryImpl

    private val fakeRemoteDataSource = object : EsbRemoteDataSource {
        override suspend fun getBranches(city: String?): AppResult<List<StoreDto>, Nothing> =
            AppResult.Success(MockEsbData.stores)

        override suspend fun getBranchById(branchId: String): AppResult<StoreDto, Nothing> =
            AppResult.Success(MockEsbData.stores.first { it.branchId == branchId })

        override suspend fun getCategories(branchId: String): AppResult<List<MenuCategoryDto>, Nothing> =
            AppResult.Success(MockEsbData.categories)

        override suspend fun getMenuItems(branchId: String, categoryId: String?): AppResult<List<MenuItemDto>, Nothing> =
            AppResult.Success(MockEsbData.menuItems)

        override suspend fun getMenuItemById(itemId: String): AppResult<MenuItemDto, Nothing> =
            AppResult.Success(MockEsbData.menuItems.first { it.itemId == itemId })

        override suspend fun submitOrder(request: CreateOrderRequestDto): AppResult<OrderResponseDto, Nothing> =
            AppResult.Success(
                OrderResponseDto(
                    orderId = "ESB-ORDER-7711",
                    referenceNumber = "REF-202610",
                    branchId = request.branchId,
                    branchName = "CulinaryLoop Senopati Flagship",
                    status = "CONFIRMED",
                    channel = request.orderChannel,
                    tableNumber = request.tableNumber,
                    subtotal = request.subtotal,
                    tax = request.taxAmount,
                    serviceCharge = request.serviceChargeAmount,
                    total = request.grandTotal,
                    createdAtEpochMs = 1700000000000L
                )
            )

        override suspend fun getOrderStatus(orderId: String): AppResult<OrderResponseDto, Nothing> =
            AppResult.Success(
                OrderResponseDto(
                    orderId = orderId,
                    referenceNumber = "REF-202610",
                    branchId = "esb_branch_001",
                    branchName = "CulinaryLoop Senopati Flagship",
                    status = "PREPARING",
                    channel = "dine_in",
                    tableNumber = "Table 05",
                    subtotal = 85000L,
                    tax = 8500L,
                    serviceCharge = 4250L,
                    total = 97750L,
                    createdAtEpochMs = 1700000000000L
                )
            )
    }

    @Before
    fun setUp() {
        orderRepository = OrderRepositoryImpl(
            remoteDataSource = fakeRemoteDataSource,
            dispatchers = DefaultDispatcherProvider()
        )
    }

    @Test
    fun testSubmitOrderCalculatesProperTaxesAndGrandTotal() = runTest {
        val store = MockEsbData.stores.first().toDomain()
        val menuItem = MockEsbData.menuItems.find { it.itemId == "item_m01" }!!.toDomain() // Wagyu Beef Truffle Fried Rice (85,000)

        val cartItem = CartItem(
            id = CartItemId("c_01"),
            menuItem = menuItem,
            quantity = 1,
            selectedModifiers = emptyList(),
            notes = "Spicy level 1"
        )

        val cart = Cart(
            store = store,
            items = listOf(cartItem),
            channel = OrderChannel.DineIn,
            tableNumber = "Table 05"
        )

        val result = orderRepository.submitOrder(cart)
        assertTrue(result.isSuccess)
        val order = (result as AppResult.Success).data

        assertEquals("ESB-ORDER-7711", order.id.value)
        assertEquals("REF-202610", order.referenceNumber)
        assertEquals(85000L, order.subtotal)
        assertEquals(8500L, order.tax)
        assertEquals(4250L, order.serviceCharge)
        assertEquals(97750L, order.total)
        assertEquals(OrderStatus.Confirmed, order.status)
        assertEquals(OrderChannel.DineIn, order.channel)
        assertEquals("Table 05", order.tableNumber)
    }

    @Test
    fun testPollOrderStatusReturnsUpdatedStatus() = runTest {
        val result = orderRepository.pollOrderStatus(OrderId("ESB-ORDER-7711"))
        assertTrue(result.isSuccess)
        val order = (result as AppResult.Success).data
        assertEquals(OrderStatus.Preparing, order.status)
    }
}
