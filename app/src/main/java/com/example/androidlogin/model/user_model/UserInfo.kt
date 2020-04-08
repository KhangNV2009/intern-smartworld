package com.example.androidlogin.model.user_model

import com.google.gson.annotations.SerializedName

data class UserInfo (
    @SerializedName("user_phone")
    var user_phone: String? = null
)