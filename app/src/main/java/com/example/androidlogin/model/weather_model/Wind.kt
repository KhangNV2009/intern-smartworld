package com.example.androidlogin.model.weather_model

import com.google.gson.annotations.SerializedName

data class Wind (
    @SerializedName("speed")
    val speed: Double
)