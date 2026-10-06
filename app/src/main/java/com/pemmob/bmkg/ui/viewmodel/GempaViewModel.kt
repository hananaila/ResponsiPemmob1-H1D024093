package com.pemmob.bmkg.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.bmkg.data.repository.GempaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GempaViewModel : ViewModel() {
    private val repository = GempaRepository()

    private val _uiState = MutableStateFlow<GempaUiState>(GempaUiState.Loading)
    val uiState: StateFlow<GempaUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        fetchGempa()
    }

    fun fetchGempa() {
        viewModelScope.launch {
            _uiState.value = GempaUiState.Loading
            try {
                val gempaList = repository.getGempaList()
                _uiState.value = GempaUiState.Success(gempaList)
            } catch (e: Exception) {
                _uiState.value = GempaUiState.Error(e.message ?: "Terjadi kesalahan jaringan")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
