package com.example.androidlogin.model.weather_model

import com.google.gson.annotations.SerializedName

data class Clouds (
    @SerializedName("all")
    val all: Double
)