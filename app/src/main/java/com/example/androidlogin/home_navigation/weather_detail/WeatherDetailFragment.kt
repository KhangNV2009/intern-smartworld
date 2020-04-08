package com.example.androidlogin.home_navigation.weather_detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidlogin.R
import com.example.androidlogin.`object`.API
import com.example.androidlogin.databinding.FragmentWeatherDetailBinding
import com.example.androidlogin.model.daily_model.DailyInfo
import com.example.androidlogin.model.daily_model.DailyModel
import com.example.androidlogin.model.detail_model.DetailModel
import com.example.androidlogin.resources.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.util.*

class WeatherDetailFragment(): Fragment() {

    private var dList: MutableList<DailyInfo> = mutableListOf()
    var mCityId: Int? = null

    companion object {
        fun newInstance(cityId: Int?) : WeatherDetailFragment {
            val fragment = WeatherDetailFragment()
            fragment.mCityId = cityId
            return fragment
        }
    }
    private lateinit var binding: FragmentWeatherDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding(container)

//        binding.rvDaily.layoutManager = LinearLayoutManager(context!!)
        val layoutManager = LinearLayoutManager(context!!,LinearLayoutManager.HORIZONTAL,false)
        binding.rvDaily.layoutManager = layoutManager
        binding.rvDaily.adapter = DailyAdapter(dList)

        getDailyData()
        getDetailData()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    private fun celsius(temp: Double): Double {
        return temp.minus(273.15)
    }

    private fun getDailyData() {
        API.apiService.getDaily(mCityId?:0, 15, Constant.APP_ID).enqueue(object :Callback<DailyModel> {
            override fun onFailure(call: Call<DailyModel>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show();
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<DailyModel>, response: Response<DailyModel>) {
                if(response.body() == null) {
                    return
                }
                dList.addAll(response.body()!!.list)
                binding.rvDaily.adapter?.notifyDataSetChanged()
                binding.tvWeatherLocation.text = response.body()!!.city.name
            }

        })
    }

    private fun getDetailData() {
        API.apiService.getDetail(mCityId?:0, Constant.APP_ID).enqueue(object :Callback<DetailModel> {
            override fun onFailure(call: Call<DetailModel>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show();
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<DetailModel>, response: Response<DetailModel>) {
                if(response.body() == null) {
                    return
                }
                val data = response.body()
                binding.tvWeatherTemp.text = "${celsius(data!!.main.temp).toInt()}°C"
                binding.tvWeatherType.text = data.weather.first().main
                binding.tvFeelLike.text = "${celsius(data.main.temp).toInt()}°C"
                binding.tvHumidity.text = "${data.main.humidity}%"
                binding.tvVisibility.text = "${data.visibility / 1000} km"
                binding.tvSpeed.text = "${data.wind.speed} m/s"
                binding.tvPressure.text = "${data.main.pressure}hPa"
            }

        })
    }

    private fun dataBinding(container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_weather_detail,
            container,
            false
        )
    }
}