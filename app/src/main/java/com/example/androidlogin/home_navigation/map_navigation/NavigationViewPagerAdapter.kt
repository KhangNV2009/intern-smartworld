package com.example.androidlogin.home_navigation.map_navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class NavigationViewPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    private val mListFragment = ArrayList<Fragment>()
    override fun getItem(position: Int): Fragment {
        return mListFragment[position]
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "map"
        1 -> "weather"
        else -> ""
    }

    override fun getCount(): Int {
        return mListFragment.size
    }

    fun addFragment(fragment: Fragment){
        mListFragment.add(fragment)
    }
}