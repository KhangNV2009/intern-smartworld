package com.example.androidlogin.home_navigation.weather_detail

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlogin.R
import com.example.androidlogin.databinding.ItemFirstWeatherDailyBinding
import com.example.androidlogin.databinding.ItemWeatherDailyBinding
import com.example.androidlogin.model.daily_model.DailyInfo
import java.text.SimpleDateFormat
import java.util.*

class DailyAdapter(private val items: MutableList<DailyInfo>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mDailyFrist = 0;
    private val mDailyItem = 1;

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            mDailyFrist
        } else {
            mDailyItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == mDailyFrist) {
            FirstDailyViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_first_weather_daily,
                    parent,
                    false
                )
            )
        } else DailyViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_weather_daily,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FirstDailyViewHolder) {
            holder.bindHolder(items[position], position)
        } else if (holder is DailyViewHolder) {
            holder.bindHolder(items[position], position)
        }
    }
}

class FirstDailyViewHolder (private val binding: ItemFirstWeatherDailyBinding) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    fun bindHolder(model: DailyInfo, position: Int) {
        binding.tvDailyDayTemp.text = "${celsius(model.temp.day).toInt()}°C"
        binding.tvDailyNightTemp.text = "${celsius(model.temp.night).toInt()}°C"
        binding.tvDailyDate.text = "  Hôm nay  "
    }

    private fun celsius(temp: Double): Double {
        return temp.minus(273.15)
    }
}

class DailyViewHolder (private val binding: ItemWeatherDailyBinding) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    fun bindHolder(model: DailyInfo, position: Int) {
        val a = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE")
        val dayFormat = SimpleDateFormat("dd-MM-yyyy")
        a.add(Calendar.DATE,position)
        binding.tvDailyDayTemp.text = "${celsius(model.temp.day).toInt()}°C"
        binding.tvDailyNightTemp.text = "${celsius(model.temp.night).toInt()}°C"
        binding.tvDailyDate.text = dayFormat.format(a.time)
    }
    private fun celsius(temp: Double): Double {
        return temp.minus(273.15)
    }
}