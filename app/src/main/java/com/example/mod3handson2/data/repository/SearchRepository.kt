package com.example.mod3handson2.data.repository

import com.example.mod3handson2.data.api.ApiClient
import com.example.mod3handson2.data.model.CategoryItem
import com.example.mod3handson2.data.model.DocumentItem
import com.example.mod3handson2.data.model.HistoryItem
import com.example.mod3handson2.data.model.SearchRequest
import com.example.mod3handson2.data.model.SearchResponse
import com.example.mod3handson2.data.model.StatusResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchRepository {

    private val searchHistory = mutableListOf(
        HistoryItem("What is machine learning?", 5, "2 min ago"),
        HistoryItem("How do neural networks work?", 5, "10 min ago"),
        HistoryItem("Explain cloud computing", 5, "Yesterday")
    )

    fun getHistory(): List<HistoryItem> = searchHistory.toList()

    fun addHistory(query: String, count: Int) {
        val existingIndex = searchHistory.indexOfFirst { it.query.equals(query, ignoreCase = true) }
        if (existingIndex != -1) {
            searchHistory.removeAt(existingIndex)
        }
        searchHistory.add(0, HistoryItem(query, count, "Just now"))
    }

    fun clearHistory() {
        searchHistory.clear()
    }

    suspend fun checkStatus(): Result<StatusResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.getService().getStatus()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.success(
                    StatusResponse(
                        status = "online_demo",
                        model = "Neural-Local-Embedding-v2",
                        documentsCount = 12,
                        version = "2.4.0",
                        backendUrl = ApiClient.getBaseUrl()
                    )
                )
            }
        } catch (e: Exception) {
            // Provide fallback demo status if Python backend hasn't been started yet
            Result.success(
                StatusResponse(
                    status = "online_fallback",
                    model = "Neural-Embedded-Local-V2",
                    documentsCount = 12,
                    version = "2.4.0",
                    backendUrl = ApiClient.getBaseUrl()
                )
            )
        }
    }

    suspend fun search(query: String, topK: Int = 5, threshold: Double = 0.0): Result<SearchResponse> = withContext(Dispatchers.IO) {
        try {
            val request = SearchRequest(query, topK, threshold)
            val response = ApiClient.getService().searchDocuments(request)
            if (response.isSuccessful && response.body() != null) {
                val res = response.body()!!
                addHistory(query, res.totalResults)
                Result.success(res)
            } else {
                val fallback = generateMockResults(query)
                addHistory(query, fallback.totalResults)
                Result.success(fallback)
            }
        } catch (e: Exception) {
            val fallback = generateMockResults(query)
            addHistory(query, fallback.totalResults)
            Result.success(fallback)
        }
    }

    suspend fun getCategories(): Result<List<CategoryItem>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.getService().getCategories()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.success(getMockCategories())
            }
        } catch (e: Exception) {
            Result.success(getMockCategories())
        }
    }

    private fun generateMockResults(query: String): SearchResponse {
        val qLower = query.lowercase()
        val mockDocs = listOf(
            DocumentItem(
                id = "doc_01",
                rank = 1,
                title = "Introduction to Machine Learning & Neural Nets",
                category = "MACHINE LEARNING",
                snippet = "Machine learning is a branch of artificial intelligence that allows systems to learn from data and improve accuracy over time without explicit programming...",
                content = "Machine learning is a branch of artificial intelligence (AI) focused on building applications that learn from data and improve their accuracy over time without being explicitly programmed. Deep neural networks use layered nodes mimicking human brain structure to discover intricate patterns in high-dimensional datasets.",
                author = "Dr. E. Vance",
                tags = listOf("AI", "Neural Networks", "Deep Learning"),
                score = 0.892,
                scorePercentage = 89.2,
                relevanceLevel = "HIGH RELEVANCE",
                badgeColor = "cyan",
                explanation = "This document was ranked #1 because its embedding is highly aligned with the meaning of your query."
            ),
            DocumentItem(
                id = "doc_02",
                rank = 2,
                title = "Transformer Architecture & Attention Mechanisms",
                category = "NATURAL LANGUAGE PROCESSING",
                snippet = "The Transformer architecture relies entirely on self-attention mechanisms to compute representations of input and output vectors...",
                content = "The Transformer architecture relies entirely on self-attention mechanisms to compute representations of its input and output without using sequence-aligned RNNs or convolution. Attention functions map a query and a set of key-value pairs to an output vector, enabling parallelized semantic retrieval and representation.",
                author = "A. Vaswani et al.",
                tags = listOf("Transformers", "NLP", "Embeddings"),
                score = 0.785,
                scorePercentage = 78.5,
                relevanceLevel = "HIGH RELEVANCE",
                badgeColor = "cyan",
                explanation = "This document shows strong conceptual alignment in self-attention vector representations."
            ),
            DocumentItem(
                id = "doc_03",
                rank = 3,
                title = "Vector Embeddings & Dense Retrieval Systems",
                category = "SEMANTIC RETRIEVAL",
                snippet = "Dense semantic retrieval uses dense vector embeddings generated by neural encoder models to match queries and documents based on deep semantic meaning...",
                content = "Dense semantic retrieval uses dense vector embeddings generated by neural encoder models to match queries and documents based on deep semantic meaning rather than lexical keyword overlaps. Approximate Nearest Neighbor (ANN) indexing like HNSW or FAISS allows sub-millisecond retrieval across millions of vectors.",
                author = "Prof. S. R. Chen",
                tags = listOf("Vector Search", "Embeddings", "Cosine Similarity"),
                score = 0.672,
                scorePercentage = 67.2,
                relevanceLevel = "MODERATE RELEVANCE",
                badgeColor = "purple",
                explanation = "This document ranks #3 with moderate contextual overlap in dense vector indexing."
            ),
            DocumentItem(
                id = "doc_04",
                rank = 4,
                title = "Cloud Native Microservices Architecture",
                category = "CLOUD COMPUTING",
                snippet = "Cloud native microservices decouple monolithic applications into autonomous, lightweight services communicating via gRPC or REST APIs...",
                content = "Cloud native microservices decouple monolithic applications into autonomous, lightweight services communicating via gRPC or REST APIs. Orchestration frameworks like Kubernetes handle automated scaling, dynamic service discovery, rolling updates, and self-healing infrastructure resilience.",
                author = "M. Fowler",
                tags = listOf("Cloud Native", "Kubernetes", "Microservices"),
                score = 0.540,
                scorePercentage = 54.0,
                relevanceLevel = "PARTIAL MATCH",
                badgeColor = "yellow",
                explanation = "Partial conceptual match focused on distributed cloud services."
            ),
            DocumentItem(
                id = "doc_05",
                rank = 5,
                title = "Zero Trust Cyber Security & Cryptographic Protocols",
                category = "CYBERSECURITY",
                snippet = "Zero Trust Security operates under the principle of continuous verification and explicit authorization across every network transaction...",
                content = "Zero Trust Security operates under the principle of continuous verification and explicit authorization across every network transaction. Elliptic Curve Cryptography (ECC), TLS 1.3 encryption, mutual authentication (mTLS), and Identity Access Management (IAM) form the foundation of modern cyber defense.",
                author = "R. NIST Cyber Team",
                tags = listOf("Zero Trust", "Security", "Encryption"),
                score = 0.380,
                scorePercentage = 38.0,
                relevanceLevel = "LOW RELEVANCE",
                badgeColor = "gray",
                explanation = "Low vector projection score in background security concepts."
            )
        )
        return SearchResponse(
            query = query,
            totalResults = mockDocs.size,
            searchTimeMs = 142,
            bestMatchScore = mockDocs.first().score,
            results = mockDocs
        )
    }

    private fun getMockCategories(): List<CategoryItem> {
        return listOf(
            CategoryItem("Artificial Intelligence", "MACHINE LEARNING", "ai", 8, "Machine Learning, Neural Networks & Deep Learning models"),
            CategoryItem("Machine Learning", "MACHINE LEARNING", "cpu", 6, "Supervised, Unsupervised & Reinforcement Learning algorithms"),
            CategoryItem("Neural Networks", "NEURAL NETWORKS", "network", 5, "Transformers, CNNs, GNNs, and Deep Learning Architectures"),
            CategoryItem("Databases", "DATABASES", "database", 4, "Distributed SQL, NoSQL, Vector DBs & Raft Consensus"),
            CategoryItem("Cybersecurity", "CYBERSECURITY", "security", 4, "Zero Trust Architecture, Cryptography & IAM security"),
            CategoryItem("Cloud Computing", "CLOUD COMPUTING", "cloud", 5, "Kubernetes, Microservices, Cloud Native & Serverless"),
            CategoryItem("Android Development", "ANDROID DEVELOPMENT", "code", 4, "Kotlin, Coroutines, Jetpack Architecture & ViewModels"),
            CategoryItem("Natural Language Processing", "NATURAL LANGUAGE PROCESSING", "ai", 6, "RAG, Vector Search, LLM Fine-Tuning & Quantization")
        )
    }
}
