package com.example.androidlogin.model.detail_model

import com.example.androidlogin.model.weather_model.*
import com.google.gson.annotations.SerializedName

data class DetailModel(
    @SerializedName("coord")
    var coord: Coord,
    @SerializedName("weather")
    var weather: ArrayList<Weather>,
    @SerializedName("main")
    var main: Main,
    @SerializedName("visibility")
    var visibility: Double,
    @SerializedName("wind")
    var wind: Wind,
    @SerializedName("clouds")
    var clouds: Clouds
)