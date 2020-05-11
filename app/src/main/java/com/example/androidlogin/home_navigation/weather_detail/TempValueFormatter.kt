package com.example.androidlogin.home_navigation.weather_detail

import android.util.Log
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class TempValueFormatter(suffix: String) :
    ValueFormatter() {
    private val mFormat: DecimalFormat = DecimalFormat("###,###,###,##")
    private val suffix: String = suffix
    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value.toInt()) + suffix
    }

    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        return when {
            axis is XAxis -> {
                mFormat.format(value.toInt())
            }
            value > 0 -> {
                mFormat.format(value.toInt()) + suffix
            }
            else -> {
                mFormat.format(value.toInt())
            }
        }
    }

}