package com.example.marketsync.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stock(
    val symbol: String,
    val companyName: String,
    val currentPrice: Double,
    val priceChange: Double,
    val percentChange: Double,
    val high: Double,
    val low: Double,
    val open: Double,
    val previousClose: Double,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable {
    val isPositiveChange: Boolean
        get() = priceChange >= 0

    fun formattedPrice(): String = String.format("$%.2f", currentPrice)
    fun formattedChange(): String = String.format("%.2f%%", percentChange)
}

data class StockList(
    val gainers: List<Stock>,
    val losers: List<Stock>,
    val trending: List<Stock>
)

data class WatchlistStock(
    val stock: Stock,
    val addedAt: Long = System.currentTimeMillis(),
    val alertPrice: Double? = null
) 