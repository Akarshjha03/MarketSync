package com.example.marketsync.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketsync.data.api.FinnhubApi
import com.example.marketsync.data.api.NewsArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val api: FinnhubApi,
    private val apiKey: String
) : ViewModel() {

    private val _newsState = MutableLiveData<NewsState>()
    val newsState: LiveData<NewsState> = _newsState

    init {
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            try {
                _newsState.value = NewsState.Loading

                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val toDate = dateFormat.format(calendar.time)
                
                calendar.add(Calendar.DAY_OF_YEAR, -7) // Get news from last 7 days
                val fromDate = dateFormat.format(calendar.time)

                // Get news for major market indices
                val news = api.getCompanyNews("AAPL", fromDate, toDate, apiKey)
                    .sortedByDescending { it.datetime }
                    .take(20) // Limit to 20 most recent articles

                _newsState.value = if (news.isEmpty()) {
                    NewsState.Empty
                } else {
                    NewsState.Success(news)
                }
            } catch (e: Exception) {
                _newsState.value = NewsState.Error(e.message ?: "Failed to load news")
            }
        }
    }
}

sealed class NewsState {
    object Loading : NewsState()
    object Empty : NewsState()
    data class Success(val news: List<NewsArticle>) : NewsState()
    data class Error(val message: String) : NewsState()
} 