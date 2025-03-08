package com.example.marketsync.ui.watchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketsync.data.model.Stock
import com.example.marketsync.data.repository.StockRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val stockRepository: StockRepository
) : ViewModel() {

    private val _watchlistState = MutableLiveData<WatchlistState>()
    val watchlistState: LiveData<WatchlistState> = _watchlistState

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private var searchJob: Job? = null

    init {
        loadWatchlist()
    }

    fun loadWatchlist() {
        viewModelScope.launch {
            try {
                _watchlistState.value = WatchlistState.Loading
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection("watchlist")
                    .get()
                    .await()

                val watchlist = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Stock::class.java)
                }

                _watchlistState.value = if (watchlist.isEmpty()) {
                    WatchlistState.Empty
                } else {
                    WatchlistState.Success(watchlist)
                }
            } catch (e: Exception) {
                _watchlistState.value = WatchlistState.Error(e.message ?: "Failed to load watchlist")
            }
        }
    }

    fun searchStocks(query: String) {
        searchJob?.cancel()
        if (query.isEmpty()) {
            _searchState.value = SearchState.Empty
            return
        }

        searchJob = viewModelScope.launch {
            try {
                delay(300) // Debounce search
                _searchState.value = SearchState.Loading
                val stocks = stockRepository.searchStocks(query)
                _searchState.value = if (stocks.isEmpty()) {
                    SearchState.Empty
                } else {
                    SearchState.Success(stocks)
                }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("401") == true -> "Invalid API key. Please check your configuration."
                    e.message?.contains("429") == true -> "API rate limit exceeded. Please try again later."
                    e.message?.contains("timeout") == true -> "Connection timeout. Please check your internet connection."
                    else -> "Search failed: ${e.message}"
                }
                android.util.Log.e("WatchlistViewModel", "Search error: ${e.message}", e)
                _searchState.value = SearchState.Error(errorMessage)
            }
        }
    }

    fun addToWatchlist(stock: Stock) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                
                firestore.collection("users")
                    .document(userId)
                    .collection("watchlist")
                    .document(stock.symbol)
                    .set(stock)
                    .await()

                loadWatchlist() // Reload the watchlist
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun removeFromWatchlist(stock: Stock) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                
                firestore.collection("users")
                    .document(userId)
                    .collection("watchlist")
                    .document(stock.symbol)
                    .delete()
                    .await()

                loadWatchlist() // Reload the watchlist
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

sealed class WatchlistState {
    object Loading : WatchlistState()
    object Empty : WatchlistState()
    data class Success(val stocks: List<Stock>) : WatchlistState()
    data class Error(val message: String) : WatchlistState()
}

sealed class SearchState {
    object Loading : SearchState()
    object Empty : SearchState()
    data class Success(val stocks: List<Stock>) : SearchState()
    data class Error(val message: String) : SearchState()
} 