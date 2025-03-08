package com.example.marketsync.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.marketsync.R
import com.example.marketsync.data.model.Stock
import com.example.marketsync.databinding.ItemStockBinding

class StockAdapter(
    private val onStockClick: (Stock) -> Unit,
    private val onRemoveClick: ((Stock) -> Unit)? = null
) : ListAdapter<Stock, StockAdapter.StockViewHolder>(StockDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = ItemStockBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StockViewHolder(binding, onStockClick, onRemoveClick)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StockViewHolder(
        private val binding: ItemStockBinding,
        private val onStockClick: (Stock) -> Unit,
        private val onRemoveClick: ((Stock) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(stock: Stock) {
            binding.apply {
                symbolText.text = stock.symbol
                companyNameText.text = stock.companyName
                priceText.text = stock.formattedPrice()
                changeText.text = stock.formattedChange()

                // Set color based on price change
                val changeColor = if (stock.isPositiveChange) {
                    R.color.positive_green
                } else {
                    R.color.negative_red
                }
                changeText.setTextColor(ContextCompat.getColor(root.context, changeColor))

                // Set click listener for the whole item
                root.setOnClickListener { onStockClick(stock) }

                // Show remove button only if onRemoveClick is provided
                removeButton?.apply {
                    visibility = if (onRemoveClick != null) View.VISIBLE else View.GONE
                    setOnClickListener { onRemoveClick?.invoke(stock) }
                }
            }
        }
    }

    private class StockDiffCallback : DiffUtil.ItemCallback<Stock>() {
        override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
            return oldItem.symbol == newItem.symbol
        }

        override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
            return oldItem == newItem
        }
    }
} 