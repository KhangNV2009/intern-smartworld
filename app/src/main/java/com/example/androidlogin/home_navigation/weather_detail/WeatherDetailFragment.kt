package com.example.androidlogin.home_navigation.weather_detail

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.DashPathEffect
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
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WeatherDetailFragment(): Fragment() {

    private var dList: ArrayList<DailyInfo> = arrayListOf()
    var mCityId: Int? = null
    private var mLineCount: HashSet<Int> = hashSetOf()

    companion object {
        fun newInstance(cityId: Int?) : WeatherDetailFragment {
            val fragment = WeatherDetailFragment()
            fragment.mCityId = cityId
            return fragment
        }
    }
    private lateinit var binding: FragmentWeatherDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding(container)

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
                setData(dList)
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
    private fun setupLineChart() {
        binding.dayLineChart.description.isEnabled = false
        binding.dayLineChart.dragDecelerationFrictionCoef = 0.9f
        binding.dayLineChart.setTouchEnabled(true)
        binding.dayLineChart.isDragEnabled = false
        binding.dayLineChart.setScaleEnabled(false)
        binding.dayLineChart.setDrawGridBackground(false)
        binding.dayLineChart.isHighlightPerDragEnabled = true
        binding.dayLineChart.setPinchZoom(true)

        val xAxisFormatter: ValueFormatter = DayAxisValueFormatter("X")

        val xAxis: XAxis = binding.dayLineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = 15
        xAxis.valueFormatter = xAxisFormatter

        val customLeft: ValueFormatter = TempValueFormatter("°C")

        val leftAxis: YAxis = binding.dayLineChart.axisLeft
        leftAxis.granularity = 1f
        leftAxis.labelCount = mLineCount.size
        leftAxis.valueFormatter = customLeft
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)

        val customRight: ValueFormatter = TempValueFormatter("°C")

        val rightAxis: YAxis = binding.dayLineChart.axisRight
        rightAxis.granularity = 1f
        rightAxis.setDrawGridLines(false)
        rightAxis.labelCount = mLineCount.size
        rightAxis.valueFormatter = customRight
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
    }
    private fun setData(list: ArrayList<DailyInfo>) {
        val values: ArrayList<Entry> = arrayListOf()
        for (i in 0 until list.size) {
            values.add(Entry(i.toFloat(), celsius(list[i].temp.day).toInt().toFloat()))
            mLineCount.add(celsius(list[i].temp.day).toInt())
        }

        val set1: LineDataSet
        if (binding.dayLineChart.data != null && binding.dayLineChart.data.dataSetCount > 0) {
            set1 = binding.dayLineChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            binding.dayLineChart.data.notifyDataChanged()
            binding.dayLineChart.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values, "Nhiệt độ buổi sáng")

            set1.axisDependency = YAxis.AxisDependency.LEFT;
            set1.color = ColorTemplate.getHoloBlue();
            set1.setCircleColor(Color.BLACK);
            set1.lineWidth = 3f
            set1.circleRadius = 4f
            set1.fillAlpha = 65
            set1.fillColor = ColorTemplate.getHoloBlue();
            set1.valueTextSize = 10f
            set1.highLightColor = Color.rgb(244, 117, 117);
            set1.setDrawCircleHole(false);

            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1)
            val data = LineData(dataSets)
            val custom: ValueFormatter = TempValueFormatter("°C")
            data.setValueFormatter(custom)
            binding.dayLineChart.data = data
            setupLineChart()
        }
    }
}