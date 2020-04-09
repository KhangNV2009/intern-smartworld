package com.example.androidlogin

import com.example.androidlogin.model.daily_model.DailyModel
import com.example.androidlogin.model.detail_model.DetailModel
import com.example.androidlogin.model.weather_model.WeatherModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("/data/2.5/find")
    fun getAPI(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("cnt") cnt: Int,
        @Query("appid") appid: String
    ): Call<WeatherModel>
    @GET("/data/2.5/forecast/daily")
    fun getDaily(
        @Query("id") id: Int,
        @Query("cnt") cnt: Int,
        @Query("appid") appid: String
    ): Call<DailyModel>
    @GET("/data/2.5/weather")
    fun getDetail(
        @Query("id") id: Int,
        @Query("appid") appid: String
    ): Call<DetailModel>
//    val posts: Call<JsonKotlin_Base>

}