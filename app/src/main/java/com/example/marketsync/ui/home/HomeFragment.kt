package com.example.marketsync.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.marketsync.data.model.Stock
import com.example.marketsync.data.model.StockList
import com.example.marketsync.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var stockPagerAdapter: StockPagerAdapter
    private lateinit var searchAdapter: StockAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Setup ViewPager
        stockPagerAdapter = StockPagerAdapter(this)
        binding.viewPager.adapter = stockPagerAdapter

        // Setup TabLayout with ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Gainers"
                1 -> "Losers"
                else -> "Trending"
            }
        }.attach()

        // Setup search
        binding.searchInput.addTextChangedListener { text ->
            viewModel.searchStocks(text.toString())
        }

        // Setup FAB
        binding.addToWatchlistFab.setOnClickListener {
            // Show search dialog or navigate to search screen
        }
    }

    private fun observeViewModel() {
        viewModel.marketOverview.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MarketOverviewState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.viewPager.visibility = View.GONE
                }
                is MarketOverviewState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.viewPager.visibility = View.VISIBLE
                    stockPagerAdapter.updateData(state.data)
                }
                is MarketOverviewState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.viewPager.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchState.Loading -> {
                    binding.searchProgressBar?.visibility = View.VISIBLE
                }
                is SearchState.Success -> {
                    binding.searchProgressBar?.visibility = View.GONE
                    // Show search results
                }
                is SearchState.Empty -> {
                    binding.searchProgressBar?.visibility = View.GONE
                    // Show empty state
                }
                is SearchState.Error -> {
                    binding.searchProgressBar?.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToStockDetails(stock: Stock) {
        val action = HomeFragmentDirections.actionHomeToStockDetails(stock.symbol)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class StockPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        private var gainers: List<Stock> = emptyList()
        private var losers: List<Stock> = emptyList()
        private var trending: List<Stock> = emptyList()

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return StockListFragment.newInstance(
                when (position) {
                    0 -> gainers
                    1 -> losers
                    else -> trending
                }
            )
        }

        fun updateData(data: StockList) {
            gainers = data.gainers
            losers = data.losers
            trending = data.trending
            notifyDataSetChanged()
        }
    }
} 