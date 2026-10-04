package com.example.mod3handson2.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mod3handson2.data.api.ApiClient
import com.example.mod3handson2.data.repository.SearchRepository
import com.example.mod3handson2.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val repository = SearchRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.etBackendUrl.setText(ApiClient.getBaseUrl())

        binding.btnTestConnection.setOnClickListener {
            val newUrl = binding.etBackendUrl.text.toString().trim()
            if (newUrl.isNotEmpty()) {
                ApiClient.setBaseUrl(newUrl)
                testBackendConnection()
            }
        }

        testBackendConnection()
    }

    private fun testBackendConnection() {
        lifecycleScope.launch {
            val result = repository.checkStatus()
            result.onSuccess { status ->
                binding.tvSettingsBackendStatus.text = "● Backend Connected (${status.model})"
                binding.tvSettingsBackendStatus.setBackgroundResource(com.example.mod3handson2.R.drawable.bg_status_online)
                Toast.makeText(context, "Connected to ${status.model}", Toast.LENGTH_SHORT).show()
            }.onFailure {
                binding.tvSettingsBackendStatus.text = "○ Connection Failed (Check Python Server)"
                binding.tvSettingsBackendStatus.setBackgroundResource(com.example.mod3handson2.R.drawable.bg_status_offline)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
