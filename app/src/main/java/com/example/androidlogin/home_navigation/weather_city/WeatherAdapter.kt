package com.example.androidlogin.home_navigation.weather_city

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidlogin.R
import com.example.androidlogin.databinding.FirstItemWeatherListBinding
import com.example.androidlogin.databinding.ItemWeatherListBinding
import com.example.androidlogin.model.weather_model.WeatherInfo

interface OnItemWeatherListener {
    fun onItemWeatherClick(weather: WeatherInfo)
}

class WeatherAdapter(
    private val items: MutableList<WeatherInfo>,
    var listener: OnItemWeatherListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WeatherViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_weather_list,
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is WeatherViewHolder) {
            holder.bindHolder(items[position])
        }
    }
}


class WeatherViewHolder(
    private val binding: ItemWeatherListBinding,
    var listener: OnItemWeatherListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bindHolder(model: WeatherInfo) {
        val celsius: Double = model.main?.temp!!.minus(273.15)
        binding.tvWeatherLocation.text = model.name
        val temp = "${celsius.toInt()}°C"
        binding.tvTemp.text = temp
        binding.root.setOnClickListener {
            listener.onItemWeatherClick(model)
        }
        Glide.with(binding.ivWeatherType.context)
            .load("http://openweathermap.org/img/wn/${model.weather?.first()?.icon}@2x.png")
            .into(binding.ivWeatherType)
    }
}