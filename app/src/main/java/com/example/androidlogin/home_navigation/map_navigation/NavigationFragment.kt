package com.example.androidlogin.home_navigation.map_navigation

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.androidlogin.R
import com.example.androidlogin.`object`.RxBus
import com.example.androidlogin.databinding.FragmentNavigationBinding
import com.example.androidlogin.home_navigation.google_map.GoogleMapFragment
import com.example.androidlogin.home_navigation.weather_city.WeatherFragment

class NavigationFragment : Fragment() {

    private lateinit var binding: FragmentNavigationBinding
    var mCurrentLocation: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding(container)
        setupViewPager()
        return binding.root
    }

    private fun dataBinding(container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_navigation,
            container,
            false
        )
    }

    private fun setupViewPager() {
        val adapter = NavigationViewPagerAdapter(childFragmentManager)
        adapter.addFragment(GoogleMapFragment.newInstance(mCurrentLocation))
        adapter.addFragment(WeatherFragment.newInstance(mCurrentLocation))
        binding.viewPagerMap.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPagerMap)
    }

    companion object {
        fun newInstance(location: Location?): NavigationFragment {
            val fragment = NavigationFragment()
            fragment.mCurrentLocation = location
        return fragment
        }
    }
}
