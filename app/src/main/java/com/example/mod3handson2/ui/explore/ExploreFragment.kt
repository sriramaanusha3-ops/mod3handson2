package com.example.mod3handson2.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mod3handson2.data.repository.SearchRepository
import com.example.mod3handson2.databinding.FragmentExploreBinding
import com.example.mod3handson2.ui.main.MainActivity
import kotlinx.coroutines.launch

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private val repository = SearchRepository()
    private lateinit var adapter: ExploreAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadCategories()
    }

    private fun setupRecyclerView() {
        adapter = ExploreAdapter { category ->
            (activity as? MainActivity)?.navigateToSearchAndQuery(category.title)
        }
        binding.rvExploreCategories.layoutManager = LinearLayoutManager(context)
        binding.rvExploreCategories.adapter = adapter
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            val result = repository.getCategories()
            result.onSuccess { categories ->
                adapter.submitList(categories)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
