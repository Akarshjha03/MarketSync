package com.example.marketsync.data.repository

import com.example.marketsync.data.api.FinnhubApi
import com.example.marketsync.data.model.Stock
import com.example.marketsync.data.model.StockList
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class StockRepository @Inject constructor(
    private val api: FinnhubApi,
    private val firestore: FirebaseFirestore,
    private val apiKey: String
) {
    suspend fun getStockQuote(symbol: String): Stock {
        return withContext(Dispatchers.IO) {
            val quote = api.getQuote(symbol, apiKey)
            Stock(
                symbol = symbol,
                companyName = "", // We'll need to fetch this separately
                currentPrice = quote.c,
                priceChange = quote.d,
                percentChange = quote.dp,
                high = quote.h,
                low = quote.l,
                open = quote.o,
                previousClose = quote.pc
            )
        }
    }

    suspend fun searchStocks(query: String): List<Stock> {
        return withContext(Dispatchers.IO) {
            try {
                val searchResults = api.searchSymbols(query, apiKey)
                if (searchResults.isEmpty()) {
                    return@withContext emptyList()
                }
                
                searchResults.map { result ->
                    try {
                        async {
                            val quote = api.getQuote(result.symbol, apiKey)
                            Stock(
                                symbol = result.symbol,
                                companyName = result.description,
                                currentPrice = quote.c,
                                priceChange = quote.d,
                                percentChange = quote.dp,
                                high = quote.h,
                                low = quote.l,
                                open = quote.o,
                                previousClose = quote.pc
                            )
                        }
                    } catch (e: Exception) {
                        // Skip stocks that fail to fetch quotes
                        null
                    }
                }.filterNotNull().awaitAll()
            } catch (e: Exception) {
                // Log the error for debugging
                android.util.Log.e("StockRepository", "Search failed: ${e.message}", e)
                throw e
            }
        }
    }

    suspend fun getMarketOverview(): StockList {
        return withContext(Dispatchers.IO) {
            // For demo purposes, we'll use some popular stocks
            val symbols = listOf("AAPL", "GOOGL", "MSFT", "AMZN", "META", "TSLA", "NVDA", "AMD")
            val stocks = symbols.map { symbol ->
                async {
                    getStockQuote(symbol)
                }
            }.awaitAll()

            // Sort stocks by percent change
            StockList(
                gainers = stocks.filter { it.percentChange > 0 }
                    .sortedByDescending { it.percentChange }
                    .take(5),
                losers = stocks.filter { it.percentChange < 0 }
                    .sortedBy { it.percentChange }
                    .take(5),
                trending = stocks.sortedByDescending { abs(it.percentChange) }
                    .take(5)
            )
        }
    }

    suspend fun addToWatchlist(userId: String, stock: Stock) {
        withContext(Dispatchers.IO) {
            firestore.collection("users")
                .document(userId)
                .collection("watchlist")
                .document(stock.symbol)
                .set(stock)
        }
    }

    suspend fun removeFromWatchlist(userId: String, symbol: String) {
        withContext(Dispatchers.IO) {
            firestore.collection("users")
                .document(userId)
                .collection("watchlist")
                .document(symbol)
                .delete()
        }
    }

    suspend fun getWatchlist(userId: String): List<Stock> {
        return withContext(Dispatchers.IO) {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("watchlist")
                .get()
                .await()

            snapshot.documents.map { doc ->
                val symbol = doc.id
                getStockQuote(symbol) // Get latest price
            }
        }
    }
} 