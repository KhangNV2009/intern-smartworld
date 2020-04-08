package com.example.androidlogin.home_navigation.google_map

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.androidlogin.R
import com.example.androidlogin.`object`.API
import com.example.androidlogin.`object`.RxBus
import com.example.androidlogin.databinding.FragmentGoogleMapBinding
import com.example.androidlogin.home_navigation.weather_detail.WeatherDetailActivity
import com.example.androidlogin.model.weather_model.WeatherInfo
import com.example.androidlogin.model.weather_model.WeatherModel
import com.example.androidlogin.resources.Constant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GoogleMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentGoogleMapBinding
    private var mMap: GoogleMap? = null
    var mCurrentLocation : Location? = null
    private var mLocationList: ArrayList<WeatherInfo> = arrayListOf()
    private var mMarker = ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxBus.listen(Location::class.java).subscribe {
            mCurrentLocation = it
        }
        mLocationList.clear()
        mMarker.clear()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.onResume()
        binding.mapView.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        mMarker.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding(container)
        if(mLocationList.isNullOrEmpty()) {
            initData(mCurrentLocation)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun dataBinding(container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_google_map,
            container,
            false
        )
    }

    companion object {
        fun newInstance(location: Location?): GoogleMapFragment {
            val fragment = GoogleMapFragment()
            fragment.mCurrentLocation = location
            return fragment
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.isMyLocationEnabled = true
        val position = LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude)
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(position))
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10f))

        for (items in mLocationList) {
            mMarker.add(mMap!!.addMarker(MarkerOptions()
                .position(LatLng(items.coord!!.lat, items.coord.lon))
                .title(items.name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            ))
            mMarker[mMarker.size - 1].tag = items.id
        }
        mMap?.setOnMarkerClickListener {
            for(items in mMarker) {
                if(it.tag == items.tag) {
                    val intent = Intent(context!!, WeatherDetailActivity::class.java)
                    intent.putExtra("cityId", items.tag.toString().toInt())
                    startActivity(intent)
                }
            }
            false
        }
    }

    private fun initData(location: Location?) {
        API.apiService.getAPI(location!!.latitude, location.longitude, 15, Constant.APP_ID).enqueue(object:
            Callback<WeatherModel> {
            override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {
                if (response.body() == null) {
                    return
                }
                val list = response.body()!!.list
                mLocationList.addAll(list)
            }

            override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                Log.d("WeatherFragment", t.message.toString())
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show();
            }
        })
    }
}
