package com.example.marketsync.ui.portfolio

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketsync.R
import com.example.marketsync.data.model.PortfolioStock
import com.example.marketsync.data.model.Stock
import com.example.marketsync.databinding.FragmentPortfolioBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.NumberFormat
import java.util.Locale

class PortfolioFragment : Fragment() {

    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!
    private lateinit var portfolioAdapter: PortfolioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupChart()
        loadDummyData()
    }

    private fun setupChart() {
        binding.portfolioChart.apply {
            // Disable description and legend
            description.isEnabled = false
            legend.isEnabled = false

            // Configure X axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = resources.getColor(R.color.gray, null)
                valueFormatter = IndexAxisValueFormatter(listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun"))
            }

            // Configure Y axis
            axisLeft.apply {
                setDrawGridLines(true)
                textColor = resources.getColor(R.color.gray, null)
            }
            axisRight.isEnabled = false

            // Enable touch gestures
            setTouchEnabled(true)
            setPinchZoom(false)
        }
    }

    private fun setupRecyclerView() {
        portfolioAdapter = PortfolioAdapter { /* Do nothing for now */ }

        binding.portfolioRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = portfolioAdapter
        }
    }

    private fun loadDummyData() {
        // Create dummy portfolio performance data
        val entries = listOf(
            Entry(0f, 25000f),  // January
            Entry(1f, 26200f),  // February
            Entry(2f, 25800f),  // March
            Entry(3f, 26500f),  // April
            Entry(4f, 27100f),  // May
            Entry(5f, 27850f)   // June
        )

        // Create and style the line dataset
        val dataSet = LineDataSet(entries, "Portfolio Value").apply {
            color = resources.getColor(R.color.chart_line, null)
            lineWidth = 2f
            setDrawFilled(true)
            fillColor = resources.getColor(R.color.chart_line, null)
            fillAlpha = 30
            setDrawCircles(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
        }

        // Set the data to the chart
        binding.portfolioChart.data = LineData(dataSet)
        binding.portfolioChart.invalidate()

        // Load dummy portfolio stocks
        val dummyPortfolio = listOf(
            PortfolioStock(
                stock = Stock(
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
                quantity = 10,
                averagePrice = 150.00
            ),
            PortfolioStock(
                stock = Stock(
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
                quantity = 5,
                averagePrice = 380.00
            ),
            PortfolioStock(
                stock = Stock(
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
                quantity = 8,
                averagePrice = 135.00
            )
        )

        // Calculate portfolio stats
        val totalValue = dummyPortfolio.sumOf { it.totalValue }
        val totalProfitLoss = dummyPortfolio.sumOf { it.profitLoss }
        val profitLossPercentage = (totalProfitLoss / (totalValue - totalProfitLoss)) * 100

        // Update UI
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
        binding.totalValueText.text = currencyFormat.format(totalValue)
        binding.profitLossText.text = currencyFormat.format(totalProfitLoss)
        binding.profitLossText.setTextColor(
            resources.getColor(
                if (totalProfitLoss >= 0) R.color.positive_green else R.color.negative_red,
                null
            )
        )
        binding.profitLossPercentageText.text = String.format("%+.1f%%", profitLossPercentage)
        binding.profitLossPercentageText.setTextColor(
            resources.getColor(
                if (totalProfitLoss >= 0) R.color.positive_green else R.color.negative_red,
                null
            )
        )

        // Update RecyclerView
        portfolioAdapter.submitList(dummyPortfolio)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 