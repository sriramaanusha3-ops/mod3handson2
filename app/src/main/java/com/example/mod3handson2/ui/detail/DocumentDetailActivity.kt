package com.example.mod3handson2.ui.detail

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.mod3handson2.R
import com.example.mod3handson2.data.model.DocumentItem
import com.example.mod3handson2.databinding.ActivityDocumentDetailBinding

class DocumentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDocumentDetailBinding

    companion object {
        private const val EXTRA_DOCUMENT = "extra_document"

        fun start(context: Context, doc: DocumentItem) {
            val intent = Intent(context, DocumentDetailActivity::class.java).apply {
                putExtra(EXTRA_DOCUMENT, doc)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val document = intent.getSerializableExtra(EXTRA_DOCUMENT) as? DocumentItem
        if (document == null) {
            finish()
            return
        }

        setupUI(document)
    }

    private fun setupUI(doc: DocumentItem) {
        binding.btnBack.setOnClickListener { finish() }

        binding.tvDetailCategory.text = doc.category.uppercase()
        binding.tvDetailTitle.text = doc.title
        binding.tvDetailDocId.text = "ID: ${doc.id} • Author: ${doc.author ?: "Neural AI Labs"}"
        
        binding.tvDetailPercentage.text = String.format("%.1f%%", doc.scorePercentage)
        binding.tvDetailBadgeLabel.text = doc.relevanceLevel

        val progressVal = (doc.score * 1000).toInt()
        binding.circleProgress.progress = 0

        val animator = ObjectAnimator.ofInt(binding.circleProgress, "progress", 0, progressVal)
        animator.duration = 900
        animator.interpolator = DecelerateInterpolator()
        animator.start()

        binding.tvDetailExplanation.text = doc.explanation
        binding.tvDetailContent.text = doc.content
        binding.tvAiInsight.text = "This document was ranked #${doc.rank} because its embedding is highly aligned with the meaning of your query vector."
    }
}
