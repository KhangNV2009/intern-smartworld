package com.example.androidlogin.model.daily_model

import com.example.androidlogin.model.weather_model.Coord
import com.google.gson.annotations.SerializedName

data class CityInfo (
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("coord")
    var coord: Coord,
    @SerializedName("country")
    var country: String,
    @SerializedName("population")
    var population: Double,
    @SerializedName("timezone")
    var timezone: Int
)