package com.example.androidlogin.model.weather_model

import com.google.gson.annotations.SerializedName

data class Main (
    @SerializedName("temp")
    val temp: Double,
    @SerializedName("feel_like")
    val feel_like: Double,
    @SerializedName("temp_min")
    val temp_min: Double,
    @SerializedName("temp_max")
    val temp_max: Double,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("humidity")
    val humidity: Int
)