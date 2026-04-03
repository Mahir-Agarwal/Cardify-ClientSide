package com.example.cardify_mobileapplication.ui.viewmodels

import com.example.cardify_mobileapplication.data.network.ApiService
import com.example.cardify_mobileapplication.data.network.ChatMessageDto
import com.example.cardify_mobileapplication.data.network.OrderRequestDto
import com.example.cardify_mobileapplication.data.network.ReviewDto
import com.example.cardify_mobileapplication.ui.base.BaseViewModel
import com.example.cardify_mobileapplication.ui.screens.order.OrderStatus

data class OrderInfo(
    val id: String,
    val amount: String,
    val commission: String,
    val status: String,
    val role: String // BUYER or OWNER
)

class OrderViewModel(private val apiService: ApiService) : BaseViewModel<List<OrderInfo>>() {
    
    private suspend fun fetchOrdersInternal(): List<OrderInfo> {
        return apiService.getMyOrders().map { dto ->
            OrderInfo(
                id = dto.id.toString(),
                amount = "$${dto.amount}",
                commission = "$${dto.commission}",
                status = dto.status,
                role = "USER"
            )
        }
    }

    fun requestOrder(cardId: Int, amount: Double) {
        executeUseCase {
            if (amount <= 0) throw Exception("Invalid amount")
            if (amount > 50000) throw Exception("ORDER_INVALID_STATE: Escrow Limit exceeded")
            
            val commission = amount * 0.025
            apiService.requestOrder(OrderRequestDto(cardId, amount, commission))
            
            fetchOrdersInternal()
        }
    }

    fun acceptOrder(orderId: String) {
        executeUseCase {
            apiService.acceptOrder(orderId)
            fetchOrdersInternal()
        }
    }

    fun simulatePayment() {
        executeUseCase {
            // Assume 123 for now since it's hardcoded in UI flow
            apiService.payOrder("123")
            fetchOrdersInternal()
        }
    }

    fun placeOrder(orderId: String) {
        executeUseCase {
            apiService.placeExternalOrder(orderId, "AMZN-12345")
            fetchOrdersInternal()
        }
    }

    fun confirmDelivery(orderId: String) {
        executeUseCase {
            apiService.confirmDelivery(orderId)
            fetchOrdersInternal()
        }
    }

    fun submitReview(orderId: String, rating: Int, feedback: String) {
        executeUseCase {
            apiService.submitReview(orderId, ReviewDto(rating, feedback))
            fetchOrdersInternal()
        }
    }

    fun sendChatMessage(orderId: String, text: String, receiverId: Int = 2) {
        executeUseCase {
            if (text.isNotBlank()) {
                apiService.sendChatMessage(orderId, ChatMessageDto(receiverId, text))
            }
            fetchOrdersInternal()
        }
    }

    fun fetchOrders() {
        executeUseCase {
            fetchOrdersInternal()
        }
    }
}
