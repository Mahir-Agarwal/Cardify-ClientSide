package com.example.cardify_mobileapplication.data.network

// Authentication
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String, val roles: List<String>)
data class AuthResponse(
    val id: Long?, 
    val token: String, 
    val name: String?, 
    val email: String?,
    val rating: Double? = 0.0,
    val totalOrders: Int? = 0,
    val earnedAmount: Double? = 0.0
)

// Cards
data class CardDto(
    val id: String?,
    val bankName: String,
    val cardType: String,
    val available: Boolean,
    // Add default mock fields for UI if backend doesn't supply them yet
    val ownerName: String? = "Card Owner",
    val rating: Float? = null,
    val commission: String? = null
)
data class AddCardRequest(val bankName: String, val cardType: String, val available: Boolean)

// Spring Boot Page structure
data class PageResponse<T>(
    val content: List<T>
)

// Orders
data class OrderDto(
    val id: Long,
    val amount: Double,
    val commission: Double,
    val status: String,
    val cardId: Long,
    val buyerName: String?,
    val ownerName: String?
)
data class OrderRequestDto(val cardId: Int, val amount: Double, val commission: Double)
data class ChatMessageDto(val receiverId: Int, val message: String)
data class ReviewDto(val rating: Int, val feedback: String)
