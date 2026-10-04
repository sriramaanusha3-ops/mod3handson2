package com.example.mod3handson2.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private var baseUrl: String = "http://10.0.2.2:5000/"
    private var apiService: RetrievalApiService? = null

    fun getBaseUrl(): String = baseUrl

    fun setBaseUrl(url: String) {
        var formatted = url.trim()
        if (!formatted.endsWith("/")) {
            formatted += "/"
        }
        baseUrl = formatted
        apiService = buildApiService(baseUrl)
    }

    fun getService(): RetrievalApiService {
        if (apiService == null) {
            apiService = buildApiService(baseUrl)
        }
        return apiService!!
    }

    private fun buildApiService(url: String): RetrievalApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrievalApiService::class.java)
    }
}
