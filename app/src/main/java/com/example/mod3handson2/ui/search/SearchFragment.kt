package com.example.mod3handson2.ui.search

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mod3handson2.data.model.DocumentItem
import com.example.mod3handson2.data.repository.SearchRepository
import com.example.mod3handson2.databinding.FragmentSearchBinding
import com.example.mod3handson2.ui.detail.DocumentDetailActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val repository = SearchRepository()
    private lateinit var adapter: SearchResultAdapter

    companion object {
        private const val REQUEST_CODE_SPEECH = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        checkBackendStatus()
    }

    private fun setupRecyclerView() {
        adapter = SearchResultAdapter { doc ->
            context?.let { ctx -> DocumentDetailActivity.start(ctx, doc) }
        }
        binding.rvSearchResults.layoutManager = LinearLayoutManager(context)
        binding.rvSearchResults.adapter = adapter
    }

    private fun setupListeners() {
        binding.etQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnClear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnClear.setOnClickListener {
            binding.etQuery.setText("")
        }

        binding.btnMic.setOnClickListener {
            startVoiceInput()
        }

        binding.etQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        binding.btnSearchContainer.setOnClickListener {
            performSearch()
        }

        binding.btnRetry.setOnClickListener {
            performSearch()
        }

        // Chip suggestion click listeners
        binding.chipMl.setOnClickListener { executeSearchQuery("Machine Learning") }
        binding.chipNn.setOnClickListener { executeSearchQuery("Neural Networks") }
        binding.chipCloud.setOnClickListener { executeSearchQuery("Cloud Computing") }
        binding.chipCyber.setOnClickListener { executeSearchQuery("Cybersecurity") }
        binding.chipDb.setOnClickListener { executeSearchQuery("Databases") }

        binding.btnTryQuery.setOnClickListener {
            executeSearchQuery("How does machine learning work?")
        }
    }

    fun executeSearchQuery(query: String) {
        binding.etQuery.setText(query)
        performSearch()
    }

    private fun performSearch() {
        val query = binding.etQuery.text.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(context, "Please enter a search query", Toast.LENGTH_SHORT).show()
            return
        }

        showLoadingState()

        lifecycleScope.launch {
            // Animated step feedback
            binding.tvLoadingStep.text = "Generating semantic representation..."
            delay(350)
            binding.tvLoadingStep.text = "Ranking documents with similarity vectors..."
            delay(350)
            binding.tvLoadingStep.text = "Finding relevant knowledge..."
            delay(200)

            val result = repository.search(query)
            result.onSuccess { response ->
                hideLoadingState()
                if (response.results.isNotEmpty()) {
                    showResultsState(response.totalResults, response.bestMatchScore, response.searchTimeMs, response.results)
                } else {
                    showEmptyState()
                }
            }.onFailure {
                hideLoadingState()
                showErrorState()
            }
        }
    }

    private fun checkBackendStatus() {
        lifecycleScope.launch {
            val statusResult = repository.checkStatus()
            statusResult.onSuccess {
                binding.tvStatus.text = "● AI ENGINE ONLINE"
                binding.statusPill.setBackgroundResource(com.example.mod3handson2.R.drawable.bg_status_online)
            }.onFailure {
                binding.tvStatus.text = "○ OFFLINE (FALLBACK)"
            }
        }
    }

    private fun showLoadingState() {
        binding.btnSearchContent.visibility = View.INVISIBLE
        binding.btnProgress.visibility = View.VISIBLE
        binding.loadingStateContainer.visibility = View.VISIBLE
        binding.emptyStateContainer.visibility = View.GONE
        binding.errorStateContainer.visibility = View.GONE
        binding.resultsSection.visibility = View.GONE
    }

    private fun hideLoadingState() {
        binding.btnSearchContent.visibility = View.VISIBLE
        binding.btnProgress.visibility = View.GONE
        binding.loadingStateContainer.visibility = View.GONE
    }

    private fun showResultsState(totalCount: Int, bestScore: Double, timeMs: Long, results: List<DocumentItem>) {
        binding.emptyStateContainer.visibility = View.GONE
        binding.errorStateContainer.visibility = View.GONE
        binding.resultsSection.visibility = View.VISIBLE

        binding.tvResultsMeta.text = "$totalCount relevant documents found • Query processed in $timeMs ms"
        binding.tvStatCount.text = totalCount.toString()
        binding.tvStatScore.text = String.format("%.3f", bestScore)
        binding.tvStatTime.text = "$timeMs ms"

        adapter.submitList(results)
    }

    private fun showEmptyState() {
        binding.emptyStateContainer.visibility = View.VISIBLE
        binding.errorStateContainer.visibility = View.GONE
        binding.resultsSection.visibility = View.GONE
    }

    private fun showErrorState() {
        binding.emptyStateContainer.visibility = View.GONE
        binding.errorStateContainer.visibility = View.VISIBLE
        binding.resultsSection.visibility = View.GONE
    }

    private fun startVoiceInput() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask your document search query...")
            }
            startActivityForResult(intent, REQUEST_CODE_SPEECH)
        } catch (e: Exception) {
            Toast.makeText(context, "Voice input not supported", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH && data != null) {
            val matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                executeSearchQuery(matches[0])
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
