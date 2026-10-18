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
import com.raf.culinaryloop.data.repository.StoreRepositoryImpl
import com.raf.culinaryloop.domain.model.StoreId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StoreRepositoryTest {

    private lateinit var storeRepository: StoreRepositoryImpl

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
                    orderId = "ESB-TEST-001",
                    referenceNumber = "REF-998877",
                    branchId = request.branchId,
                    branchName = "Test Branch",
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
                    referenceNumber = "REF-998877",
                    branchId = "esb_branch_001",
                    branchName = "Test Branch",
                    status = "READY",
                    channel = "dine_in",
                    tableNumber = "01",
                    subtotal = 50000L,
                    tax = 5000L,
                    serviceCharge = 2500L,
                    total = 57500L,
                    createdAtEpochMs = 1700000000000L
                )
            )
    }

    @Before
    fun setUp() {
        storeRepository = StoreRepositoryImpl(
            remoteDataSource = fakeRemoteDataSource,
            dispatchers = DefaultDispatcherProvider()
        )
    }

    @Test
    fun testGetStoresReturnsBranches() = runTest {
        val result = storeRepository.getStores()
        assertTrue(result.isSuccess)
        val stores = (result as AppResult.Success).data
        assertEquals(MockEsbData.stores.size, stores.size)
        assertEquals("CulinaryLoop Senopati Flagship", stores.first().name)
    }

    @Test
    fun testSelectStoreUpdatesActiveStore() = runTest {
        val stores = (storeRepository.getStores() as AppResult.Success).data
        val targetStore = stores[2] // Grand Indonesia

        storeRepository.selectStore(targetStore)
        val activeStore = storeRepository.observeSelectedStore().first()
        assertNotNull(activeStore)
        assertEquals(targetStore.id, activeStore?.id)
        assertEquals("CulinaryLoop Grand Indonesia", activeStore?.name)
    }

    @Test
    fun testGetStoreById() = runTest {
        val result = storeRepository.getStoreById(StoreId("esb_branch_002"))
        assertTrue(result.isSuccess)
        val store = (result as AppResult.Success).data
        assertEquals("CulinaryLoop SCBD Gourmet Hub", store.name)
    }
}
