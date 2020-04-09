package com.example.androidlogin.home_navigation.google_map

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidlogin.R
import com.example.androidlogin.databinding.ItemGoogleMapListBinding
import com.example.androidlogin.databinding.ItemWeatherListBinding
import com.example.androidlogin.home_navigation.weather_city.OnItemWeatherListener
import com.example.androidlogin.home_navigation.weather_city.WeatherViewHolder
import com.example.androidlogin.model.weather_model.WeatherInfo

interface OnItemStationListener {
    fun onItemStationClick(weather: WeatherInfo)
    fun onItemStationClickDirection(weather: WeatherInfo)
}

class StationAdapter(private val items: MutableList<WeatherInfo>, var listener: OnItemStationListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StationViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_google_map_list,
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is StationViewHolder) {
            holder.bindHolder(items[position])
        }
    }
}

class StationViewHolder(private val binding: ItemGoogleMapListBinding, var listener: OnItemStationListener) : RecyclerView.ViewHolder(binding.root) {

    fun bindHolder(model: WeatherInfo) {
        val celsius: Double = model.main?.temp!!.minus(273.15)
        binding.tvStationLocation.text = model.name
        val temp = "${celsius.toInt()}°C"
        binding.tvStationTemp.text = temp
        binding.btViewDetail.setOnClickListener {
            listener.onItemStationClick(model)
        }
        binding.btDirection.setOnClickListener {
            listener.onItemStationClickDirection(model)
        }
    }
}

