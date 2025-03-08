package com.example.marketsync.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketsync.data.model.Stock
import com.example.marketsync.databinding.FragmentStockListBinding

class StockListFragment : Fragment() {

    private var _binding: FragmentStockListBinding? = null
    private val binding get() = _binding!!
    private lateinit var stockAdapter: StockAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        arguments?.getParcelableArrayList<Stock>(ARG_STOCKS)?.let { stocks ->
            stockAdapter.submitList(stocks)
        }
    }

    private fun setupRecyclerView() {
        stockAdapter = StockAdapter(
            onStockClick = { stock ->
                navigateToStockDetails(stock)
            }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stockAdapter
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

    companion object {
        private const val ARG_STOCKS = "stocks"

        fun newInstance(stocks: List<Stock>): StockListFragment {
            return StockListFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_STOCKS, ArrayList(stocks))
                }
            }
        }
    }
} 