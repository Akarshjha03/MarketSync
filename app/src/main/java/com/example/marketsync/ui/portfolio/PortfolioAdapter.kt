package com.example.marketsync.ui.portfolio

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.marketsync.R
import com.example.marketsync.data.model.PortfolioStock
import com.example.marketsync.databinding.ItemPortfolioStockBinding
import java.text.NumberFormat
import java.util.Locale

class PortfolioAdapter(
    private val onStockClick: (PortfolioStock) -> Unit
) : ListAdapter<PortfolioStock, PortfolioAdapter.ViewHolder>(PortfolioStockDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPortfolioStockBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemPortfolioStockBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onStockClick(getItem(position))
                }
            }
        }

        fun bind(item: PortfolioStock) {
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
            
            binding.symbolText.text = item.stock.symbol
            binding.companyNameText.text = item.stock.companyName
            binding.quantityText.text = "${item.quantity} shares"
            
            binding.averagePriceText.text = "Avg: ${currencyFormat.format(item.averagePrice)}"
            binding.currentPriceText.text = "Current: ${currencyFormat.format(item.stock.currentPrice)}"

            val profitLossFormatted = currencyFormat.format(item.profitLoss)
            binding.profitLossText.text = profitLossFormatted
            binding.profitLossText.setTextColor(
                binding.root.context.getColor(
                    if (item.profitLoss >= 0) R.color.positive_green else R.color.negative_red
                )
            )
        }
    }
}

private class PortfolioStockDiffCallback : DiffUtil.ItemCallback<PortfolioStock>() {
    override fun areItemsTheSame(oldItem: PortfolioStock, newItem: PortfolioStock): Boolean {
        return oldItem.stock.symbol == newItem.stock.symbol
    }

    override fun areContentsTheSame(oldItem: PortfolioStock, newItem: PortfolioStock): Boolean {
        return oldItem == newItem
    }
} 