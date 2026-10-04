package com.example.mod3handson2.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mod3handson2.R
import com.example.mod3handson2.data.model.CategoryItem
import com.example.mod3handson2.databinding.ItemExploreCategoryBinding

class ExploreAdapter(
    private val onCategoryClick: (CategoryItem) -> Unit
) : RecyclerView.Adapter<ExploreAdapter.ViewHolder>() {

    private val items = mutableListOf<CategoryItem>()

    fun submitList(newList: List<CategoryItem>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExploreCategoryBinding.inflate(
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

    inner class ViewHolder(private val binding: ItemExploreCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryItem) {
            binding.tvCatTitle.text = item.title
            binding.tvCatDesc.text = item.description
            binding.tvCatCount.text = "${item.count} Docs"

            val iconRes = when (item.icon) {
                "ai" -> R.drawable.ic_ai_sparkle
                "cpu" -> R.drawable.ic_cpu
                "network" -> R.drawable.ic_network
                "database" -> R.drawable.ic_database
                "security" -> R.drawable.ic_security
                "cloud" -> R.drawable.ic_cloud
                "code" -> R.drawable.ic_code
                else -> R.drawable.ic_document
            }
            binding.ivCatIcon.setImageResource(iconRes)

            binding.cardCategory.setOnClickListener {
                onCategoryClick(item)
            }
        }
    }
}
