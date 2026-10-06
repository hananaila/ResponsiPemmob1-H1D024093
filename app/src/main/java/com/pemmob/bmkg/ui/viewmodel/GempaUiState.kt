package com.pemmob.bmkg.ui.viewmodel

import com.pemmob.bmkg.data.model.Gempa

sealed interface GempaUiState {
    object Loading : GempaUiState
    data class Success(val data: List<Gempa>) : GempaUiState
    data class Error(val message: String) : GempaUiState
}
