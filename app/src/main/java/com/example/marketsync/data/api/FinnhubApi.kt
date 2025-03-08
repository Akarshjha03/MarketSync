package com.example.marketsync.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface FinnhubApi {
    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): QuoteResponse

    @GET("stock/symbol")
    suspend fun searchSymbols(
        @Query("q") query: String,
        @Query("token") token: String
    ): List<SymbolSearchResult>

    @GET("company-news")
    suspend fun getCompanyNews(
        @Query("symbol") symbol: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("token") token: String
    ): List<NewsArticle>

    @GET("stock/candle")
    suspend fun getCandles(
        @Query("symbol") symbol: String,
        @Query("resolution") resolution: String,
        @Query("from") from: Long,
        @Query("to") to: Long,
        @Query("token") token: String
    ): CandleResponse
}

data class QuoteResponse(
    val c: Double, // Current price
    val d: Double, // Change
    val dp: Double, // Percent change
    val h: Double, // High price of the day
    val l: Double, // Low price of the day
    val o: Double, // Open price of the day
    val pc: Double, // Previous close price
)

data class SymbolSearchResult(
    val description: String,
    val displaySymbol: String,
    val symbol: String,
    val type: String
)

data class NewsArticle(
    val category: String,
    val datetime: Long,
    val headline: String,
    val id: Long,
    val image: String,
    val related: String,
    val source: String,
    val summary: String,
    val url: String
)

data class CandleResponse(
    val c: List<Double>, // List of close prices
    val h: List<Double>, // List of high prices
    val l: List<Double>, // List of low prices
    val o: List<Double>, // List of open prices
    val s: String, // Status
    val t: List<Long>, // List of timestamps
    val v: List<Long> // List of volume data
) 