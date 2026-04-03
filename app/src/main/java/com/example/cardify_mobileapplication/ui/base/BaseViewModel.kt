package com.example.cardify_mobileapplication.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardify_mobileapplication.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<T> : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<T>>(UiState.Idle)
    val uiState: StateFlow<UiState<T>> = _uiState.asStateFlow()

    /**
     * Executes a coroutine task and handles the loading, success, and error states automatically.
     */
    protected fun executeUseCase(
        action: suspend () -> T
    ): kotlinx.coroutines.Job {
        return viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = action()
                _uiState.value = UiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
