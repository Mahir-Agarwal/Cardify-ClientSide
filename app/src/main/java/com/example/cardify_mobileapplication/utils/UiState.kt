package com.example.cardify_mobileapplication.utils

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String, val errorType: ErrorType = ErrorType.UNKNOWN) : UiState<Nothing>()
}

enum class ErrorType {
    NETWORK,
    UNAUTHORIZED_ACCESS,
    ORDER_INVALID_STATE,
    API_ERROR,
    UNKNOWN
}
