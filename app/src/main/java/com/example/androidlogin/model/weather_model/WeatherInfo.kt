package com.example.androidlogin.model.weather_model

import com.google.gson.annotations.SerializedName

data class WeatherInfo (
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("coord")
    val coord: Coord?,
    @SerializedName("main")
    val main: Main?,
    @SerializedName("dt")
    val dt: Int?,
    @SerializedName("wind")
    val wind: Wind?,
    @SerializedName("sys")
    val sys: Sys?,
    @SerializedName("rain")
    val rain: Any?,
    @SerializedName("snow")
    val snow: Any?,
    @SerializedName("clouds")
    val clouds: Clouds?,
    @SerializedName("weather")
    val weather: ArrayList<Weather>?
)