package com.example.androidlogin.model.weather_model

import com.google.gson.annotations.SerializedName

data class WeatherModel (
    @SerializedName("message")
    val message: String,
    @SerializedName("String")
    val cod: String,
    @SerializedName("count")
    val count: Double,
    @SerializedName("list")
    val list: ArrayList<WeatherInfo>
)