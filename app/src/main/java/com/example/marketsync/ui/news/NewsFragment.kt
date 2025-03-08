package com.example.marketsync.ui.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketsync.databinding.FragmentNewsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewsViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter { article ->
            // Open article URL in browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
            startActivity(intent)
        }

        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.newsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NewsState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.emptyText.visibility = View.GONE
                    binding.newsRecyclerView.visibility = View.GONE
                }
                is NewsState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyText.visibility = View.GONE
                    binding.newsRecyclerView.visibility = View.VISIBLE
                    newsAdapter.submitList(state.news)
                }
                is NewsState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyText.visibility = View.VISIBLE
                    binding.newsRecyclerView.visibility = View.GONE
                }
                is NewsState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyText.visibility = View.GONE
                    binding.newsRecyclerView.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 