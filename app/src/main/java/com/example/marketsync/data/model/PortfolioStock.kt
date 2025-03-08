package com.example.marketsync.data.model

data class PortfolioStock(
    val stock: Stock,
    val quantity: Int,
    val averagePrice: Double
) {
    val totalValue: Double
        get() = quantity * stock.currentPrice

    val profitLoss: Double
        get() = quantity * (stock.currentPrice - averagePrice)

    val profitLossPercentage: Double
        get() = ((stock.currentPrice - averagePrice) / averagePrice) * 100
} 