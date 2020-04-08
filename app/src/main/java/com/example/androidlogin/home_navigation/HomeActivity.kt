package com.example.androidlogin.home_navigation

import android.Manifest
import android.content.Context
import android.content.Intent
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
import com.example.androidlogin.home_navigation.user_profile.ProfileFragment
import com.example.androidlogin.home_navigation.weather_city.WeatherFragment
import com.example.androidlogin.databinding.ActivityHomeBinding
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*
import com.example.androidlogin.R
import com.example.androidlogin.`object`.RxBus
import com.example.androidlogin.home_navigation.google_map.GoogleMapFragment
import com.example.androidlogin.home_navigation.map_navigation.NavigationFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng


class HomeActivity : AppCompatActivity(), LocationListener {

    lateinit var binding: ActivityHomeBinding
    private var mfusedLocationProviderClient: FusedLocationProviderClient? = null
    val PERMISSION_ID = 42
    var mLastLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        binding.viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }
            override fun onPageSelected(position: Int) {
                bottom_navigation.menu.getItem(position).isChecked = true
            }
        })
    }

    override fun onStop() {
        super.onStop()
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
                // Granted. Start getting the location information
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
                        Log.d("HomeActivity", "${mLastLocation?.latitude} -- ${mLastLocation?.longitude}")
                        setupViewPager()
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
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 10000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mfusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
            Looper.myLooper())
    }
    private val mLocationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onLocationResult(locationResult: LocationResult) {
            onLocationChanged(locationResult.lastLocation)
        }
    }

    override fun onLocationChanged(location: Location?) {
        val distance = mLastLocation?.distanceTo(location)?.toInt()
        if(distance != null) {
            if(distance > 500) {
                mLastLocation = location
                RxBus.publish(Location(location))
            }
            else if(distance >= 1) {
                mLastLocation = location
                RxBus.publish(Location(location))
            }
        }
    }
}