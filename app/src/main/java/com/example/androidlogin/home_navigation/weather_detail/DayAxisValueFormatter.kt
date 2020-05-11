package com.example.androidlogin.home_navigation.weather_detail

import android.annotation.SuppressLint
import android.util.Log
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class DayAxisValueFormatter(suffix: String) : ValueFormatter() {

    @SuppressLint("SimpleDateFormat")
    private var dateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)

    override fun getFormattedValue(value: Float): String {
        var date: Calendar = Calendar.getInstance()
        date.add(Calendar.DATE, value.toInt())
        return dateFormat.format(date.time)
    }
}