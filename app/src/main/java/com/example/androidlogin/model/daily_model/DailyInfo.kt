package com.example.androidlogin.model.daily_model

import com.example.androidlogin.model.weather_model.Weather
import com.google.gson.annotations.SerializedName

data class DailyInfo (
    @SerializedName("dt")
    var dt: Int,
    @SerializedName("sunrise")
    var sunrise: Int,
    @SerializedName("sunset")
    var sunset: Int,
    @SerializedName("temp")
    var temp: Temp,
    @SerializedName("feels_like")
    var feels_like: FeelsLike,
    @SerializedName("pressure")
    var pressure: Int,
    @SerializedName("humidity")
    var humidity: Int,
    @SerializedName("weather")
    var weather: ArrayList<Weather>,
    @SerializedName("speed")
    var speed: Double,
    @SerializedName("deg")
    var deg: Int,
    @SerializedName("clouds")
    var clouds: Int,
    @SerializedName("snow")
    var snow: Double,
    @SerializedName("rain")
    var rain: Double
)