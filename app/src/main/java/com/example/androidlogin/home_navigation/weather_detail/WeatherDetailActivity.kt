package com.example.androidlogin.home_navigation.weather_detail

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.androidlogin.R
import com.example.androidlogin.databinding.ActivityWeatherDetailBinding

class WeatherDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherDetailBinding
    private var mCityId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_weather_detail)
        supportActionBar?.title = "Weather detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mCityId = intent.getIntExtra("cityId", 0)
        Log.d("DetailActivity", "$mCityId")

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.detail_root,
                    WeatherDetailFragment.newInstance(mCityId), "Hello")
                .commit()
        }
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        return super.onCreateView(parent, name, context, attrs)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
