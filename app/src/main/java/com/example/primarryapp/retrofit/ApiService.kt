package com.example.primarryapp.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Define a simple Retrofit service interface
interface ApiService {
    // Definisikan endpoint yang diperlukan untuk permintaan Anda
    @GET("/predict") // Ganti dengan path sesuai API Anda
    fun getApiResponse(@Query("content") content: String): Call<Prediction> // Ganti YourResponseModel dengan model respons yang sesuai
}
