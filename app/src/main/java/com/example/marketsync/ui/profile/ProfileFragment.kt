package com.example.marketsync.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.marketsync.R
import com.example.marketsync.databinding.FragmentProfileBinding
import java.text.NumberFormat
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProfile()
        setupListeners()
    }

    private fun setupProfile() {
        // Profile Info
        binding.nameText.text = "John Doe"
        binding.emailText.text = "john.doe@example.com"
        binding.memberSinceText.text = "Member since March 2024"

        // Investment Summary
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
        binding.totalInvestedText.text = currencyFormat.format(25000.00)
        binding.currentValueText.text = currencyFormat.format(27850.00)
        binding.totalProfitText.text = currencyFormat.format(2850.00)
        binding.returnText.text = "+11.4%"

        // Set colors for profit/loss
        val profitColor = resources.getColor(R.color.positive_green, null)
        binding.totalProfitText.setTextColor(profitColor)
        binding.returnText.setTextColor(profitColor)

        // Default switch states
        binding.notificationsSwitch.isChecked = true
        binding.darkModeSwitch.isChecked = false
        binding.biometricSwitch.isChecked = false
    }

    private fun setupListeners() {
        // Settings switches
        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle notifications toggle
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle dark mode toggle
        }

        binding.biometricSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle biometric toggle
        }

        // Logout button
        binding.logoutButton.setOnClickListener {
            // Navigate to login screen
            findNavController().navigate(R.id.action_profile_to_login)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 