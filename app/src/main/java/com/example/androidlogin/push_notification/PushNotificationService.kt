package com.example.androidlogin.push_notification

import android.app.LauncherActivity
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.androidlogin.R
import com.example.androidlogin.`object`.API
import com.example.androidlogin.authentication.LoginActivity
import com.example.androidlogin.home_navigation.HomeActivity
import com.example.androidlogin.model.user_model.UserModel
import com.example.androidlogin.model.weather_model.WeatherInfo
import com.example.androidlogin.model.weather_model.WeatherModel
import com.example.androidlogin.resources.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PushNotificationService: Service() {
    private val mChannelID = "com.example.androidlogin.push_notification"
    val mUser = FirebaseAuth.getInstance().currentUser
    private var mUserLat: Double? = null
    private var mUserLong: Double? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val builder: Notification.Builder = Notification.Builder(this, mChannelID)
        val notificationCompat = builder.build()
        startForeground(1234, notificationCompat)
        Log.d("PushNotificationService", "OnCreate")
    }

    override fun onDestroy() {
        Log.d("PushNotificationService", "OnDestroy")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("PushNotificationService", "onStartCommand")
        initData(mUserLat, mUserLong)
        getUserLocation()
//        initData(mUserLat, mUserLong)
        return START_NOT_STICKY
    }
    private fun initData(latitude: Double?, longitude: Double?) {
        API.apiService.getAPI(10.842769, 106.64926, 1, Constant.APP_ID).enqueue(object :
            Callback<WeatherModel> {
            override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {
                if (response.body() == null) {
                    return
                }
                val list = response.body()!!.list
                Log.d("PushNotificationService", "get data successfully")
                setupNotification(list.first())
            }

            override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                Log.d("PushNotificationService", t.message.toString())
                Log.d("PushNotificationService", "get data fail")
            }
        })
    }

    private fun setupNotification(weatherInfo: WeatherInfo) {
        Log.d("PushNotificationService","PushNotificationService")
        val builder: Notification.Builder = Notification.Builder(this, mChannelID)
        val notifyIntent = Intent(this, HomeActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationCompat = builder.build()
        val managerCompat = NotificationManagerCompat.from(this)

        builder.setContentTitle(weatherInfo.name)
        builder.setContentText("${celsius(weatherInfo.main!!.temp)}°C - ${weatherInfo.weather!!.first().description}")
        builder.setSmallIcon(R.mipmap.ic_launcher_round)
        builder.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
        builder.setContentIntent(pendingIntent)
        Glide.with(this)
            .asBitmap()
            .load("http://openweathermap.org/img/wn/${weatherInfo.weather.first().icon}@2x.png")
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    builder.setLargeIcon(resource)
                    managerCompat.notify(1234, notificationCompat)
                }
            })
    }

    private fun celsius(temp: Double): Double {
        return temp.minus(273.15)
    }

//    private fun getUserLocation() {
//        val db = Firebase.firestore
//
//        db.collection("user").document(mUser?.uid.toString())
//            .get()
//            .addOnSuccessListener { result ->
//                val user = result.toObject(UserModel::class.java)
//                mUserLat = user?.user_lat
//                mUserLong = user?.user_long
//                initData(mUserLat, mUserLong)
//                Log.d("PushNotificationService", "Get current location successfully")
//            }
//            .addOnFailureListener { exception ->
//                Log.w("PushNotificationService", "Error getting documents.", exception)
//            }
//    }
    private fun getUserLocation() {
        val userDataPreferences: SharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)
        val editorUserData: SharedPreferences.Editor = userDataPreferences.edit()
        mUserLat = userDataPreferences.getFloat("userDataLat", 0.0f).toDouble()
        mUserLong = userDataPreferences.getFloat("userDataLon", 0.0f).toDouble()
        Log.d("PushNotificationService", "${userDataPreferences.getFloat("userDataLat", 0.0f)}")
        Log.d("PushNotificationService", "${userDataPreferences.getFloat("userDataLon", 0.0f)}")
        initData(mUserLat, mUserLong)
    }
}