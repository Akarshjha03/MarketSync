package com.example.marketsync.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketsync.data.model.Stock
import com.example.marketsync.databinding.FragmentWatchlistBinding
import com.example.marketsync.ui.home.StockAdapter

class WatchlistFragment : Fragment() {

    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var stockAdapter: StockAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViews()
        loadDummyData()
    }

    private fun setupRecyclerView() {
        stockAdapter = StockAdapter(
            onStockClick = { /* Do nothing for now */ },
            onRemoveClick = { stock ->
                // Remove the stock from the list
                val currentList = stockAdapter.currentList.toMutableList()
                currentList.remove(stock)
                stockAdapter.submitList(currentList)
                
                // Show empty state if list is empty
                if (currentList.isEmpty()) {
                    binding.emptyText.visibility = View.VISIBLE
                    binding.watchlistRecyclerView.visibility = View.GONE
                }
            }
        )

        binding.watchlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stockAdapter
        }
    }

    private fun setupViews() {
        binding.addStockFab.setOnClickListener {
            // Do nothing for now
            // StockSearchDialog().show(childFragmentManager, StockSearchDialog.TAG)
        }
    }

    private fun loadDummyData() {
        val dummyStocks = listOf(
            Stock(
                symbol = "AAPL",
                companyName = "Apple Inc.",
                currentPrice = 172.50,
                priceChange = 2.30,
                percentChange = 1.35,
                high = 173.20,
                low = 170.80,
                open = 171.00,
                previousClose = 170.20
            ),
            Stock(
                symbol = "MSFT",
                companyName = "Microsoft Corporation",
                currentPrice = 415.20,
                priceChange = 5.80,
                percentChange = 1.42,
                high = 416.00,
                low = 410.50,
                open = 411.00,
                previousClose = 409.40
            ),
            Stock(
                symbol = "GOOGL",
                companyName = "Alphabet Inc.",
                currentPrice = 141.80,
                priceChange = -1.20,
                percentChange = -0.84,
                high = 143.00,
                low = 141.50,
                open = 142.80,
                previousClose = 143.00
            ),
            Stock(
                symbol = "AMZN",
                companyName = "Amazon.com Inc.",
                currentPrice = 175.35,
                priceChange = 2.15,
                percentChange = 1.24,
                high = 176.00,
                low = 173.50,
                open = 173.80,
                previousClose = 173.20
            ),
            Stock(
                symbol = "TSLA",
                companyName = "Tesla, Inc.",
                currentPrice = 185.10,
                priceChange = -3.40,
                percentChange = -1.80,
                high = 189.00,
                low = 184.50,
                open = 188.50,
                previousClose = 188.50
            ),
            Stock(
                symbol = "NVDA",
                companyName = "NVIDIA Corporation",
                currentPrice = 875.35,
                priceChange = 15.80,
                percentChange = 1.84,
                high = 880.00,
                low = 865.20,
                open = 868.50,
                previousClose = 859.55
            ),
            Stock(
                symbol = "META",
                companyName = "Meta Platforms Inc.",
                currentPrice = 505.25,
                priceChange = 8.75,
                percentChange = 1.76,
                high = 507.00,
                low = 499.50,
                open = 500.00,
                previousClose = 496.50
            )
        )

        binding.emptyText.visibility = View.GONE
        binding.watchlistRecyclerView.visibility = View.VISIBLE
        stockAdapter.submitList(dummyStocks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 