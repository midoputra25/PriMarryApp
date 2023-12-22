package com.example.primarryapp.retrofit

import com.google.gson.annotations.SerializedName

data class Prediction(
    @SerializedName("code")
    val code: Int,

    @SerializedName("prediction")
    val prediction: Int
)
