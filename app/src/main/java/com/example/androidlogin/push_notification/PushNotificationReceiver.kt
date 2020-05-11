package com.example.androidlogin.push_notification

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class PushNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("PushNotificationReceiver", "Running")
        val intent = Intent(context, PushNotificationService::class.java)
        context?.startForegroundService(intent)
    }
}