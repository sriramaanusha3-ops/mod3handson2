package com.example.mod3handson2.ui.search

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mod3handson2.R
import com.example.mod3handson2.data.model.DocumentItem
import com.example.mod3handson2.databinding.ItemSearchResultBinding

class SearchResultAdapter(
    private val onItemClick: (DocumentItem) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private val items = mutableListOf<DocumentItem>()

    fun submitList(newList: List<DocumentItem>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DocumentItem) {
            val context = binding.root.context

            binding.tvRank.text = String.format("%02d", item.rank)
            binding.tvCategory.text = item.category.uppercase()
            binding.tvTitle.text = item.title
            binding.tvSnippet.text = item.snippet
            binding.tvScore.text = String.format("%.3f", item.score)
            binding.tvBadge.text = item.relevanceLevel

            val progressVal = (item.score * 1000).toInt()
            binding.progressSimilarity.progress = 0
            
            val animator = ObjectAnimator.ofInt(binding.progressSimilarity, "progress", 0, progressVal)
            animator.duration = 750
            animator.interpolator = DecelerateInterpolator()
            animator.start()

            val (badgeBg, badgeTextColor, progressDrawableRes) = when (item.badgeColor) {
                "cyan" -> Triple(R.drawable.bg_badge_cyan, ContextCompat.getColor(context, R.color.cyan_accent), R.drawable.bg_progress_cyan)
                "purple" -> Triple(R.drawable.bg_badge_purple, ContextCompat.getColor(context, R.color.bright_purple), R.drawable.bg_progress_purple)
                "yellow" -> Triple(R.drawable.bg_badge_yellow, ContextCompat.getColor(context, R.color.warning_yellow), R.drawable.bg_progress_purple)
                else -> Triple(R.drawable.bg_badge_gray, ContextCompat.getColor(context, R.color.text_secondary), R.drawable.bg_progress_purple)
            }

            binding.tvBadge.setBackgroundResource(badgeBg)
            binding.tvBadge.setTextColor(badgeTextColor)
            binding.tvScore.setTextColor(badgeTextColor)
            binding.progressSimilarity.progressDrawable = ContextCompat.getDrawable(context, progressDrawableRes)

            binding.cardRoot.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
