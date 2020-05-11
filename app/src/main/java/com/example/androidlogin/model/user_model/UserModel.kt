package com.example.androidlogin.model.user_model

import com.google.gson.annotations.SerializedName

data class UserModel (
    var user_phone: String? = "",
    var user_lat: Double? = null,
    var user_long: Double? = null
)