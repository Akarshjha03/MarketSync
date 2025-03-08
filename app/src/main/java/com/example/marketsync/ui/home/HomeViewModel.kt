package com.example.marketsync.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketsync.data.model.Stock
import com.example.marketsync.data.model.StockList
import com.example.marketsync.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val stockRepository: StockRepository
) : ViewModel() {

    private val _marketOverview = MutableLiveData<MarketOverviewState>()
    val marketOverview: LiveData<MarketOverviewState> = _marketOverview

    private val _searchResults = MutableLiveData<SearchState>()
    val searchResults: LiveData<SearchState> = _searchResults

    private var searchJob: Job? = null

    init {
        loadMarketOverview()
    }

    fun loadMarketOverview() {
        viewModelScope.launch {
            try {
                _marketOverview.value = MarketOverviewState.Loading
                val stockList = stockRepository.getMarketOverview()
                _marketOverview.value = MarketOverviewState.Success(stockList)
            } catch (e: Exception) {
                _marketOverview.value = MarketOverviewState.Error(e.message ?: "Failed to load market overview")
            }
        }
    }

    fun searchStocks(query: String) {
        searchJob?.cancel()
        if (query.isEmpty()) {
            _searchResults.value = SearchState.Empty
            return
        }

        searchJob = viewModelScope.launch {
            try {
                delay(300) // Debounce search
                _searchResults.value = SearchState.Loading
                val stocks = stockRepository.searchStocks(query)
                _searchResults.value = if (stocks.isEmpty()) {
                    SearchState.Empty
                } else {
                    SearchState.Success(stocks)
                }
            } catch (e: Exception) {
                _searchResults.value = SearchState.Error(e.message ?: "Search failed")
            }
        }
    }

    fun addToWatchlist(userId: String, stock: Stock) {
        viewModelScope.launch {
            try {
                stockRepository.addToWatchlist(userId, stock)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

sealed class MarketOverviewState {
    object Loading : MarketOverviewState()
    data class Success(val data: StockList) : MarketOverviewState()
    data class Error(val message: String) : MarketOverviewState()
}

sealed class SearchState {
    object Loading : SearchState()
    object Empty : SearchState()
    data class Success(val stocks: List<Stock>) : SearchState()
    data class Error(val message: String) : SearchState()
} 