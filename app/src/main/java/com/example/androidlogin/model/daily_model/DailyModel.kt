package com.example.androidlogin.model.daily_model

import com.google.gson.annotations.SerializedName

data class DailyModel (
    @SerializedName("city")
    var city: CityInfo,
    @SerializedName("cod")
    var cod: String,
    @SerializedName("message")
    var message: Double,
    @SerializedName("cnt")
    var cnt: Int,
    @SerializedName("list")
    var list: ArrayList<DailyInfo>
)