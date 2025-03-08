package com.example.marketsync.ui.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketsync.data.model.Stock
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class PortfolioStock(
    val stock: Stock,
    val quantity: Int,
    val averagePrice: Double,
    val totalValue: Double = stock.currentPrice * quantity,
    val profitLoss: Double = (stock.currentPrice - averagePrice) * quantity
)

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _portfolioState = MutableLiveData<PortfolioState>()
    val portfolioState: LiveData<PortfolioState> = _portfolioState

    private val _portfolioStats = MutableLiveData<PortfolioStats>()
    val portfolioStats: LiveData<PortfolioStats> = _portfolioStats

    init {
        loadPortfolio()
    }

    fun loadPortfolio() {
        viewModelScope.launch {
            try {
                _portfolioState.value = PortfolioState.Loading
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection("portfolio")
                    .get()
                    .await()

                val portfolio = snapshot.documents.mapNotNull { doc ->
                    val stock = doc.toObject(Stock::class.java) ?: return@mapNotNull null
                    val quantity = doc.getLong("quantity")?.toInt() ?: 0
                    val averagePrice = doc.getDouble("averagePrice") ?: 0.0
                    
                    PortfolioStock(stock, quantity, averagePrice)
                }

                if (portfolio.isNotEmpty()) {
                    val totalValue = portfolio.sumOf { it.totalValue }
                    val totalProfitLoss = portfolio.sumOf { it.profitLoss }
                    
                    _portfolioStats.value = PortfolioStats(
                        totalValue = totalValue,
                        totalProfitLoss = totalProfitLoss,
                        profitLossPercentage = (totalProfitLoss / (totalValue - totalProfitLoss)) * 100
                    )
                }

                _portfolioState.value = if (portfolio.isEmpty()) {
                    PortfolioState.Empty
                } else {
                    PortfolioState.Success(portfolio)
                }
            } catch (e: Exception) {
                _portfolioState.value = PortfolioState.Error(e.message ?: "Failed to load portfolio")
            }
        }
    }
}

sealed class PortfolioState {
    object Loading : PortfolioState()
    object Empty : PortfolioState()
    data class Success(val portfolio: List<PortfolioStock>) : PortfolioState()
    data class Error(val message: String) : PortfolioState()
}

data class PortfolioStats(
    val totalValue: Double,
    val totalProfitLoss: Double,
    val profitLossPercentage: Double
) 