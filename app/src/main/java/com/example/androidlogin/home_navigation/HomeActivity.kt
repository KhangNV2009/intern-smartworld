package com.example.androidlogin.home_navigation

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.example.androidlogin.R
import com.example.androidlogin.`object`.RxBus
import com.example.androidlogin.databinding.ActivityHomeBinding
import com.example.androidlogin.home_navigation.map_navigation.NavigationFragment
import com.example.androidlogin.home_navigation.user_profile.ProfileFragment
import com.example.androidlogin.push_notification.PushNotificationReceiver
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*


class HomeActivity : AppCompatActivity(), LocationListener {

    lateinit var binding: ActivityHomeBinding
    private var mfusedLocationProviderClient: FusedLocationProviderClient? = null
    val PERMISSION_ID = 42
    var mLastLocation: Location? = null
    private lateinit var mLocationDisposable: Disposable
    val mUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        setupViewPager()
    }

    override fun onResume() {
        super.onResume()
        getLastLocation()
        requestNewLocationData()
    }

    private fun setupViewPager(){
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(NavigationFragment.newInstance(mLastLocation))
        adapter.addFragment(ProfileFragment.newInstance(this))
        binding.viewPager.adapter = adapter
        supportActionBar?.hide()
        binding.bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        binding.viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }
            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.menu.getItem(position).isChecked = true
            }
        })
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.itemHome -> {
                binding.viewPager.currentItem = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.itemProfile -> {
                binding.viewPager.currentItem = 1
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mfusedLocationProviderClient?.lastLocation?.addOnCompleteListener(this) { currentLocation ->
                    val location: Location? = currentLocation.result
                    if (location == null){
                        requestNewLocationData()
                    }else{
                        mLastLocation = location
                        RxBus.publish(Location(location))
                        updateUserLocation(location)
                        setupPushNotification()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mfusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }
    private val mLocationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onLocationResult(locationResult: LocationResult) {
            onLocationChanged(locationResult.lastLocation)
        }
    }

    override fun onLocationChanged(location: Location?) {
        RxBus.publish(Location(location))
        val distance = location?.distanceTo(mLastLocation)?.toInt()
        if(distance!! >= 50000) {
            updateUserLocation(location)
        }
    }

    private fun updateUserLocation(location: Location?) {
        val db = Firebase.firestore
        db.collection("user").document(mUser?.uid.toString())
            .update(mapOf(
                "user_lat" to location?.latitude,
                "user_long" to location?.longitude
            ))
            .addOnSuccessListener {
                val userDataPreferences: SharedPreferences = getSharedPreferences("userData", 0)
                val editorUserData: SharedPreferences.Editor = userDataPreferences.edit()
                editorUserData.putFloat("userDataLat", location!!.latitude.toFloat()).apply()
                editorUserData.putFloat("userDataLon", location.longitude.toFloat()).apply()
                setupPushNotification()
                Log.d("HomeActivity", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e -> Log.w("HomeActivity", "Error writing document", e) }
    }
    private fun setupPushNotification() {
        val userDataPreferences: SharedPreferences = getSharedPreferences("userData", 0)
        val editorUserData: SharedPreferences.Editor = userDataPreferences.edit()
        if(!userDataPreferences.getBoolean("isSetupNotification", false)) {
            Log.d("HomeActivity", "is setup notification")
            val notifyIntent = Intent(this, PushNotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this.applicationContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager: AlarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, 7)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 1)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
            editorUserData.putBoolean("isSetupNotification", true).apply()
        }
    }
}