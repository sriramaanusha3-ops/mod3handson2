package com.example.mod3handson2.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SearchRequest(
    @SerializedName("query") val query: String,
    @SerializedName("top_k") val topK: Int = 5,
    @SerializedName("threshold") val threshold: Double = 0.0
)

data class SearchResponse(
    @SerializedName("query") val query: String,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("search_time_ms") val searchTimeMs: Long,
    @SerializedName("best_match_score") val bestMatchScore: Double,
    @SerializedName("results") val results: List<DocumentItem>
)

data class DocumentItem(
    @SerializedName("id") val id: String,
    @SerializedName("rank") val rank: Int,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("snippet") val snippet: String,
    @SerializedName("content") val content: String,
    @SerializedName("author") val author: String?,
    @SerializedName("tags") val tags: List<String>?,
    @SerializedName("score") val score: Double,
    @SerializedName("score_percentage") val scorePercentage: Double,
    @SerializedName("relevance_level") val relevanceLevel: String,
    @SerializedName("badge_color") val badgeColor: String,
    @SerializedName("explanation") val explanation: String
) : Serializable

data class StatusResponse(
    @SerializedName("status") val status: String,
    @SerializedName("model") val model: String,
    @SerializedName("documents_count") val documentsCount: Int,
    @SerializedName("version") val version: String,
    @SerializedName("backend_url") val backendUrl: String?
)

data class CategoryItem(
    @SerializedName("title") val title: String,
    @SerializedName("key") val key: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("count") val count: Int,
    @SerializedName("description") val description: String
)

data class HistoryItem(
    val query: String,
    val resultCount: Int,
    val timeAgo: String,
    val timestamp: Long = System.currentTimeMillis()
)
