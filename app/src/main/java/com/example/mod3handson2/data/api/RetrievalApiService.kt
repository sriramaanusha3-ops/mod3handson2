package com.example.mod3handson2.data.api

import com.example.mod3handson2.data.model.CategoryItem
import com.example.mod3handson2.data.model.SearchRequest
import com.example.mod3handson2.data.model.SearchResponse
import com.example.mod3handson2.data.model.StatusResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrievalApiService {
    @GET("api/status")
    suspend fun getStatus(): Response<StatusResponse>

    @POST("api/search")
    suspend fun searchDocuments(@Body request: SearchRequest): Response<SearchResponse>

    @GET("api/categories")
    suspend fun getCategories(): Response<List<CategoryItem>>
}
