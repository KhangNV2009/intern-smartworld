package com.example.androidlogin.home_navigation.google_map

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlogin.R
import com.example.androidlogin.`object`.API
import com.example.androidlogin.`object`.RxBus
import com.example.androidlogin.databinding.FragmentGoogleMapBinding
import com.example.androidlogin.home_navigation.weather_detail.WeatherDetailActivity
import com.example.androidlogin.model.weather_model.WeatherInfo
import com.example.androidlogin.model.weather_model.WeatherList
import com.example.androidlogin.model.weather_model.WeatherModel
import com.example.androidlogin.resources.Constant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import io.reactivex.rxjava3.disposables.Disposable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.sign

class GoogleMapFragment : Fragment(), OnMapReadyCallback, OnItemStationListener {

    private lateinit var binding: FragmentGoogleMapBinding
    private var mMap: GoogleMap? = null
    var mCurrentLocation : Location? = null
    private var mLocationList: ArrayList<WeatherInfo> = arrayListOf()
    private var mMarker = ArrayList<Marker>()
    private lateinit var mLocationDisposable: Disposable
    private var mIsGetLocation: Boolean? = false
    private var mStationAdapter : StationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLocationDisposable = RxBus.listen(Location::class.java).subscribe {
            if(mIsGetLocation == false) {
                mCurrentLocation = it
                initData(it.latitude, it.longitude, false)
                mIsGetLocation = true
            }
            val distance = mCurrentLocation?.distanceTo(it)?.toInt()
            if (mIsGetLocation == true) {
                if(distance!! >= 50000) {
                    mCurrentLocation = it
                    initData(it.latitude, it.longitude, false)
                    Log.d("GoogleMapFragment", "lat: ${it.latitude} --- long: ${it.longitude}")
                }
            }
        }
    }

    override fun onItemStationClick(weather: WeatherInfo) {
        val intent = Intent(context!!, WeatherDetailActivity::class.java)
        intent.putExtra("cityId", weather.id)
        startActivity(intent)
    }

    override fun onItemStationClickDirection(weather: WeatherInfo) {
        val intent = Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + weather.coord!!.lat +"," + weather.coord.lon))
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.onResume()
        binding.mapView.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding(container)
        setupRecyclerView()
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

    override fun onDestroy() {
        super.onDestroy()
        if(!mLocationDisposable.isDisposed) mLocationDisposable.dispose()
    }

    companion object {
        fun newInstance(location: Location?): GoogleMapFragment {
            val fragment = GoogleMapFragment()
            return fragment
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.isMyLocationEnabled = true
        mMap?.setOnMapLongClickListener {
            initData(it.latitude, it.longitude, true)
        }
        mMap?.setOnMyLocationButtonClickListener {
            initData(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude, false)
            false
        }
    }

    private fun setupMarker(vararg newMarker: LatLng) {
        mMarker.clear()
        mMap?.clear()
        if(newMarker.isNotEmpty()) {
            mMap!!.addMarker(MarkerOptions()
                .position(newMarker.first())
                .title(mLocationList.first().name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
            )
            Log.d("GoogleMapFragment", "new Location: ${newMarker.first()}")
        }
        var bounds: LatLngBounds? = null
        var builder = LatLngBounds.builder()
        for (items in mLocationList) {
            mMarker.add(mMap!!.addMarker(MarkerOptions()
                .position(LatLng(items.coord!!.lat, items.coord.lon))
                .title(items.name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.station))
            ))
            mMarker[mMarker.size - 1].tag = items.id
            builder = builder.include(LatLng(items.coord.lat, items.coord.lon))
        }
        bounds = builder.build()
        mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        mMap?.setOnMarkerClickListener {
            for(i in 0 until mMarker.size) {
                if(it.tag == mMarker[i].tag) {
                    binding.rvStation.smoothScrollToPosition(i)
                }
            }
            false
        }
    }

    private fun initData(latitude: Double, longitude: Double, isNewLocation: Boolean) {
        API.apiService.getAPI(latitude, longitude, 15, Constant.APP_ID).enqueue(object:
            Callback<WeatherModel> {
            override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {
                if (response.body() == null) {
                    return
                }
                mLocationList.clear()
                Log.d("GoogleMapFragment", "fetch data done")
                val model = response.body()
                val list = response.body()!!.list
                mLocationList.addAll(list)
                RxBus.publish(WeatherList(list))
                if(isNewLocation) setupMarker(LatLng(latitude, longitude))
                else setupMarker()
                binding.rvStation.adapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                Log.d("GoogleMapFragment", t.message.toString())
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show();
            }
        })
    }
    private fun setupRecyclerView() {
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvStation)
        mStationAdapter = StationAdapter(mLocationList, this)
        binding.rvStation.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        binding.rvStation.adapter = mStationAdapter
        binding.rvStation.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val position = (binding.rvStation.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                if(position != -1) {
                    val location = LatLng(mLocationList[position].coord!!.lat, mLocationList[position].coord!!.lon)
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
                }
            }
        })
    }
}
