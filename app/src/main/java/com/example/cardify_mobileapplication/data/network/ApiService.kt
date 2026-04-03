package com.example.cardify_mobileapplication.data.network

import retrofit2.http.*

interface ApiService {

    // 1. Authentication Module
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    // 2. Cards Module
    @GET("cards/search")
    suspend fun searchCards(
        @Query("bankName") bankName: String? = null,
        @Query("cardType") cardType: String? = null,
        @Query("isAvailable") isAvailable: Boolean? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): PageResponse<CardDto>

    @GET("cards/my-cards")
    suspend fun getMyCards(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): PageResponse<CardDto>

    @POST("cards")
    suspend fun addCard(@Body request: AddCardRequest)

    @PUT("cards/{cardId}")
    suspend fun updateCard(@Path("cardId") cardId: String, @Body request: AddCardRequest)

    @DELETE("cards/{cardId}")
    suspend fun deleteCard(@Path("cardId") cardId: String)

    // 3. Order Lifecycle
    @GET("orders/my-orders")
    suspend fun getMyOrders(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): List<OrderDto>

    @POST("orders/request")
    suspend fun requestOrder(@Body request: OrderRequestDto)

    @PUT("orders/{orderId}/accept")
    suspend fun acceptOrder(@Path("orderId") orderId: String)

    @POST("orders/{orderId}/pay")
    suspend fun payOrder(@Path("orderId") orderId: String)

    @PUT("orders/{orderId}/place")
    suspend fun placeExternalOrder(@Path("orderId") orderId: String, @Query("externalOrderId") externalOrderId: String)

    @PUT("orders/{orderId}/confirm")
    suspend fun confirmDelivery(@Path("orderId") orderId: String)

    // 4. Chat Module
    @POST("chat/{orderId}")
    suspend fun sendChatMessage(@Path("orderId") orderId: String, @Body request: ChatMessageDto)

    @GET("chat/{orderId}")
    suspend fun getChatHistory(
        @Path("orderId") orderId: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): PageResponse<Any> // Adjust as needed if chat has a DTO

    // 5. Reviews Module
    @POST("reviews/{orderId}")
    suspend fun submitReview(@Path("orderId") orderId: String, @Body request: ReviewDto)
}
