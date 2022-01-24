package com.themarkettheory.user.ui.salon

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.themarkettheory.user.R
import com.themarkettheory.user.ui.main.activity.BaseActivity
import com.themarkettheory.user.ui.main.fragment.SalonServicesFragment
import com.themarkettheory.user.ui.restaurant.restaurant_detail.OverviewFragment
import com.themarkettheory.user.ui.restaurant.restaurant_detail.ReviewsFragment
import kotlinx.android.synthetic.main.activity_salon_detail.*

class SalonDetailActivity : BaseActivity() {
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salon_detail)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            if (position == 0) {
                return OverviewFragment()
            } else if (position == 1) {
                return SalonServicesFragment()
            } else {
                return ReviewsFragment()
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }
}