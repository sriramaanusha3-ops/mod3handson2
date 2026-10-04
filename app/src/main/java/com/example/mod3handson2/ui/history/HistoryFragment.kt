package com.example.mod3handson2.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mod3handson2.data.repository.SearchRepository
import com.example.mod3handson2.databinding.FragmentHistoryBinding
import com.example.mod3handson2.ui.main.MainActivity

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val repository = SearchRepository()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadHistory()

        binding.btnClearHistory.setOnClickListener {
            repository.clearHistory()
            loadHistory()
        }
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter { historyItem ->
            (activity as? MainActivity)?.navigateToSearchAndQuery(historyItem.query)
        }
        binding.rvHistory.layoutManager = LinearLayoutManager(context)
        binding.rvHistory.adapter = adapter
    }

    private fun loadHistory() {
        val historyList = repository.getHistory()
        adapter.submitList(historyList)
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
