package com.example.cardify_mobileapplication.ui.viewmodels

import com.example.cardify_mobileapplication.data.network.ApiService
import com.example.cardify_mobileapplication.ui.base.BaseViewModel

// DTO mapped to UI model seamlessly
data class CardInfo(
    val id: String, 
    val bankName: String, 
    val type: String, 
    val owner: String, 
    val rating: Float, 
    val isAvailable: Boolean, 
    val commission: String
)

class HomeViewModel(private val apiService: ApiService) : BaseViewModel<List<CardInfo>>() {

    private suspend fun fetchCardsInternal(query: String? = null, cardType: String? = null): List<CardInfo> {
        val finalType = if (cardType == "ALL") null else cardType
        val pageResponse = apiService.searchCards(
            bankName = if (query.isNullOrBlank()) null else query,
            cardType = finalType
        )
        return pageResponse.content.map { dto ->
            CardInfo(
                id = dto.id ?: "Unknown",
                bankName = dto.bankName,
                type = dto.cardType,
                owner = dto.ownerName ?: "Card Owner",
                rating = dto.rating ?: 0.0f,
                isAvailable = dto.available,
                commission = dto.commission ?: "NEW"
            )
        }
    }

    fun fetchCards(query: String? = null, type: String? = null): kotlinx.coroutines.Job {
        return executeUseCase {
            fetchCardsInternal(query, type)
        }
    }

    fun fetchMyCards(): kotlinx.coroutines.Job {
        return executeUseCase {
            fetchMyCardsInternal()
        }
    }

    fun addCard(bankName: String, cardType: String, available: Boolean): kotlinx.coroutines.Job {
        return executeUseCase {
            if (bankName.isBlank()) throw Exception("Provide Bank Name")
            apiService.addCard(com.example.cardify_mobileapplication.data.network.AddCardRequest(bankName, cardType, available))
            fetchMyCardsInternal()
        }
    }

    fun updateCard(cardId: String, bankName: String, cardType: String, available: Boolean): kotlinx.coroutines.Job {
        return executeUseCase {
            apiService.updateCard(cardId, com.example.cardify_mobileapplication.data.network.AddCardRequest(bankName, cardType, available))
            fetchMyCardsInternal()
        }
    }

    fun deleteCard(cardId: String): kotlinx.coroutines.Job {
        return executeUseCase {
            apiService.deleteCard(cardId)
            fetchMyCardsInternal()
        }
    }

    private suspend fun fetchMyCardsInternal(): List<CardInfo> {
        val response = apiService.getMyCards()
        return response.content.map { dto ->
            CardInfo(
                id = dto.id ?: "Unknown",
                bankName = dto.bankName,
                type = dto.cardType,
                owner = dto.ownerName ?: "Your Card",
                rating = dto.rating ?: 0.0f,
                isAvailable = dto.available,
                commission = dto.commission ?: "NEW"
            )
        }
    }
}
