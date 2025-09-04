package com.tech.vircle.ui.splash

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tech.vircle.ui.splash.welcome.WelcomeFirstFragment
import com.tech.vircle.ui.splash.welcome.WelcomeSecondFragment


class WelcomePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WelcomeFirstFragment()
            1 -> WelcomeSecondFragment()
            else -> WelcomeFirstFragment()
        }
    }
}