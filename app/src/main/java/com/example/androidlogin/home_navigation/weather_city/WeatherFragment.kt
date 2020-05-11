package com.example.androidlogin.home_navigation.weather_city

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.androidlogin.R
import com.example.androidlogin.`object`.API
import com.example.androidlogin.`object`.RxBus
import com.example.androidlogin.databinding.FragmentWeatherBinding
import com.example.androidlogin.home_navigation.weather_detail.WeatherDetailActivity
import com.example.androidlogin.model.weather_model.WeatherInfo
import com.example.androidlogin.model.weather_model.WeatherList
import com.example.androidlogin.model.weather_model.WeatherModel
import com.example.androidlogin.resources.Constant
import com.example.androidlogin.utils.Utils
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_weather.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WeatherFragment() : Fragment(), OnItemWeatherListener {

    private lateinit var binding: FragmentWeatherBinding
    private var mWeatherList: MutableList<WeatherInfo> = mutableListOf()
    var mCurrentLocation : Location? = null
    private var mWeatherAdapter : WeatherAdapter? = null
    private lateinit var mLocationDisposable: Disposable

    override fun onDestroy() {
        super.onDestroy()
        if(!mLocationDisposable.isDisposed) mLocationDisposable.dispose()
    }

    override fun onItemWeatherClick(weather: WeatherInfo) {
        val intent = Intent(context!!,
            WeatherDetailActivity::class.java)
        intent.putExtra("cityId", weather.id)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mLocationDisposable = RxBus.listen(WeatherList::class.java).subscribe {
            val loading = Utils.showLoading(context!!)
            mWeatherList.clear()
            mWeatherList.addAll(it.list)
            binding.rvWeather.adapter?.notifyDataSetChanged()
            loading?.dismiss()
            Log.d("WeatherFragment", "${it.list.first().name}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding(container)
        mWeatherAdapter = WeatherAdapter(mWeatherList, this)
        binding.rvWeather.layoutManager = LinearLayoutManager(context!!)
        binding.rvWeather.adapter = mWeatherAdapter

        binding.srlPullToRefresh.setOnRefreshListener {
//            mWeatherList.clear()
            binding.srlPullToRefresh.isRefreshing = false
        }
        return binding.root
    }

    private fun dataBinding(container: ViewGroup?) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_weather, container, false)
    }

    private fun initData(location: Location?) {
        API.apiService.getAPI(location?.latitude, location?.longitude, 1, Constant.APP_ID).enqueue(object: Callback<WeatherModel> {
            override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {
                if (response.body() == null) {
                    return
                }
                val list = response.body()!!.list
                mWeatherList.addAll(list)
                binding.rvWeather.adapter?.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                Log.d("WeatherFragment", t.message.toString())
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show();
            }
        })
    }

    companion object {
        fun newInstance(location: Location?): WeatherFragment {
            val fragment = WeatherFragment()
            fragment.mCurrentLocation = location
            return fragment
        }
    }
}