package com.example.androidlogin.model.weather_model

import com.google.gson.annotations.SerializedName

data class Sys (
    @SerializedName("country")
    val country: String
)