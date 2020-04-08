package com.example.androidlogin

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDexApplication

class MainApplication: MultiDexApplication() {
    companion object {
        private lateinit var mInstance: MainApplication
        fun getInstance(): MainApplication {
            return mInstance
        }
        const val TAG = "MainApplication"
    }

}
