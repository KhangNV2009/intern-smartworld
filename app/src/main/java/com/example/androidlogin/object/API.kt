package com.example.androidlogin.`object`

import com.example.androidlogin.APIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object API {
    private var retrofit: Retrofit? = null
    val apiService: APIService
    get() {
        if(retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(APIService::class.java)
    }
}