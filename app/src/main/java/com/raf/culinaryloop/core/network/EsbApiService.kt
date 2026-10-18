package com.raf.culinaryloop.core.network

import com.raf.culinaryloop.data.model.CreateOrderRequestDto
import com.raf.culinaryloop.data.model.MenuCategoryDto
import com.raf.culinaryloop.data.model.MenuItemDto
import com.raf.culinaryloop.data.model.OrderResponseDto
import com.raf.culinaryloop.data.model.StoreDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit contract for official ESB REST endpoints (Merchant, Outlets, Catalog, Order).
 */
interface EsbApiService {

    @GET("merchants/branches")
    suspend fun getBranches(
        @Query("city") city: String? = null
    ): Response<List<StoreDto>>

    @GET("merchants/branches/{branchId}")
    suspend fun getBranchById(
        @Path("branchId") branchId: String
    ): Response<StoreDto>

    @GET("merchants/branches/{branchId}/categories")
    suspend fun getCategories(
        @Path("branchId") branchId: String
    ): Response<List<MenuCategoryDto>>

    @GET("merchants/branches/{branchId}/menu")
    suspend fun getMenuItems(
        @Path("branchId") branchId: String,
        @Query("category_id") categoryId: String? = null
    ): Response<List<MenuItemDto>>

    @GET("merchants/menu/{itemId}")
    suspend fun getMenuItemById(
        @Path("itemId") itemId: String
    ): Response<MenuItemDto>

    @POST("orders/simulate")
    suspend fun submitOrder(
        @Body request: CreateOrderRequestDto
    ): Response<OrderResponseDto>

    @GET("orders/{orderId}/status")
    suspend fun getOrderStatus(
        @Path("orderId") orderId: String
    ): Response<OrderResponseDto>
}
