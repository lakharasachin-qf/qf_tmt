package com.themarkettheory.user.ui.restaurant

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textview.MaterialTextView
import com.themarkettheory.customer.ui.restaurant.restaurant_detail.BookingFragment
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.NonSwipeableViewPager
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.newmodels.overview.Data
import com.themarkettheory.user.ui.coupon.CouponDetailActivity
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.gym.PackagesFragment
import com.themarkettheory.user.ui.main.activity.BaseActivity
import com.themarkettheory.user.ui.main.activity.MyTableBookingDetailActivity
import com.themarkettheory.user.ui.main.activity.TotalPointsActivity
import com.themarkettheory.user.ui.main.fragment.AttireMenuFragment
import com.themarkettheory.user.ui.main.fragment.SalonServicesFragment
import com.themarkettheory.user.ui.restaurant.restaurant_detail.OverviewFragment
import com.themarkettheory.user.ui.restaurant.restaurant_detail.RestaurantMenuFragment
import com.themarkettheory.user.ui.restaurant.restaurant_detail.ReviewsFragment
import com.themarkettheory.user.viewmodel.GeneralViewModel
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.activity_see_all_photos.*
import kotlinx.android.synthetic.main.activity_vendor_detail.*
import kotlinx.android.synthetic.main.fragment_booking.*
import kotlinx.android.synthetic.main.toolbar_restaurant.*

class VendorDetailActivity : BaseActivity(), View.OnClickListener {
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var restaurantSectionsPagerAdapter: RestaurantSectionsPagerAdapter? = null
    lateinit var vendorDetailViewModel: VendorDetailViewModel
    lateinit var generalViewModel: GeneralViewModel
    var category: String? = null
    var serviceId: String? = ""
    var vendorTitle: String? = ""
    var selectPosition: Int? = 0
    var isFavorite = false
    var is_liked: Int? = 0
    var serviceDetail: Data? = null
    private lateinit var container: NonSwipeableViewPager

    //Toolbar
    private lateinit var viewToolBar: View
    private lateinit var toolBarBack: ShapeableImageView
    private lateinit var toolBarShare: ShapeableImageView
    private lateinit var toolBarFavorite: ShapeableImageView
    private lateinit var toolBarTitle: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendor_detail)
        container = findViewById(R.id.bottomContainer)
        container.isPagingEnabled = false
        vendorDetailViewModel = ViewModelProvider(this).get(VendorDetailViewModel::class.java)
        generalViewModel = ViewModelProvider(this).get(GeneralViewModel::class.java)
        if (intent != null) {
            category = intent.getStringExtra("category")
            serviceId = intent.getStringExtra("serviceId")
            vendorTitle = intent.getStringExtra("vendorTitle")
            selectPosition = intent.getIntExtra("selectPosition", 0)
        }

        Log.e("category", category.toString())
        Log.e("serviceId", serviceId.toString())
        Log.e("vendorTitle", vendorTitle.toString())
        Log.e("selectPosition", selectPosition.toString())
        viewToolBar = findViewById(R.id.layoutToolbar)
        toolBarBack = viewToolBar.findViewById(R.id.ivBack)
        toolBarShare = viewToolBar.findViewById(R.id.ivShare)
        toolBarFavorite = viewToolBar.findViewById(R.id.ivFavorite)
        toolBarTitle = viewToolBar.findViewById(R.id.tvTitle)

        //Replace && with || in if condition by Milan on 24-Dec-2021
//        if (selectPosition == 0){
//                             if (Config.isMenuFragmentComingFrom.isEmpty() || Config.isBookingDetailOpeningFrom.isNotEmpty()) {
//                rlToolbar1.setBackgroundColor(Color.TRANSPARENT)
//                bottomContainer.layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT
//                bottomContainer.translationY = TypedValue.applyDimension(
//                    TypedValue.COMPLEX_UNIT_DIP,
//                    -48f,
//                    resources.displayMetrics
//                )
//                toolBarTitle.visibility = View.GONE
//            }
//        }
        toolBarTitle.text = vendorTitle

        Config.vendorDetailServiceId = serviceId!!
        Config.vendorDetailServiceName = vendorTitle!!


        /*if (category == "2") { //Gym
            tabs.getTabAt(1)?.setText(getString(R.string.packages))
        } else if (category == "4" || category == "5") { //Salon
            tabs.getTabAt(1)?.setText(getString(R.string.services))
        } else if (category == "3") { //Retail
            tabs.getTabAt(1)?.setText(getString(R.string.menu))
        }*/

        getResponse()

        generalViewModel.navigateToBooking.observe(this, Observer {
            if (it) {
                container.currentItem = 1
            }
        })

        if (PubFun.isInternetConnection(this@VendorDetailActivity)) {
            vendorDetailViewModel.service_details(serviceId)
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        restaurantSectionsPagerAdapter = RestaurantSectionsPagerAdapter(supportFragmentManager)

        if (category == "1") {
            container.adapter = restaurantSectionsPagerAdapter
            tabs.getTabAt(2)?.view?.visibility = View.VISIBLE
        } else {
            container.adapter = mSectionsPagerAdapter
            tabs.getTabAt(2)?.view?.visibility = View.GONE
        }

        if (Config.isMenuFragmentComingFrom == Config.isMenuFragmentComingFromBucketCart ||
            Config.isMenuFragmentComingFrom == Config.isMenuFragmentComingFromBookingTable
        ) {
            tabs.getTabAt(2)?.select()
        }

        if (Config.isBookingDetailOpeningFrom.isEmpty()) {
            tabs.getTabAt(selectPosition!!)?.select()
        }

//        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
//        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        listeners()
        container.offscreenPageLimit = 3
        container.currentItem = selectPosition!!

        toolBarBack.setOnClickListener(this)
        toolBarShare.setOnClickListener(this)
        toolBarFavorite.setOnClickListener(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {
            if (PubFun.isInternetConnection(this@VendorDetailActivity)) {
                if (Config.isMenuFragmentComingFrom == Config.isMenuFragmentComingFromBookingTable) {
                    startActivity(
                        Intent(
                            this@VendorDetailActivity,
                            MyTableBookingDetailActivity::class.java
                        )
                    )
                } else if (Config.isBookingDetailOpeningFrom == Config.isBookingDetailOpeningFromTotalPoints) {
                    Config.isBookingDetailOpeningFrom = ""
                    startActivity(
                        Intent(
                            this@VendorDetailActivity,
                            TotalPointsActivity::class.java
                        )
                    )
                }
                if (Config.isBucketAddMoreClicked) {
                    Config.isBucketAddMoreClicked = false
                    startActivity(Intent(this@VendorDetailActivity, MyBucketActivity::class.java))
                }
                if (Config.isVendorComingFromCouponDetail) {
                    Config.isVendorComingFromCouponDetail = false
                    startActivity(
                        Intent(
                            this@VendorDetailActivity,
                            CouponDetailActivity::class.java
                        )
                    )
                }
                if (Config.isVendorComingFromMapFragment) {
                    Config.isVendorComingFromMapFragment = false
                    Config.isEventBottomBarClicked = false
                    Config.bottomBarClickedName = Config.locationBottomBarClick
                }
                finish()
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getResponse() {
        vendorDetailViewModel.responseServiceDetail.observe(this, Observer {
            when (it.status!!) {
                0 -> showMsgDialogAndProceed(it.message!!.trim())
                1 -> {
                    serviceDetail = it.data
                    isFavorite = it.data?.isLiked == 1
                    is_liked = it.data?.isLiked
                    if (it.data?.isLiked == 1) {
                        toolBarFavorite.setImageResource(R.drawable.ic_like_selected)
                    } else {
                        toolBarFavorite.setImageResource(R.drawable.ic_like_unselected)
                    }
                    tabs.getTabAt(1)!!.view.visibility =
                        if (it.data!!.isTableBooking!! == 1) View.VISIBLE else View.GONE
                }
            }
        })
    }

    private fun listeners() {
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                container.currentItem = tab!!.position
                when (tab.position) {
                    0 -> {
                        toolBarTitle.visibility = View.VISIBLE
                        llAction.visibility = View.VISIBLE
                        rlToolbar1.setBackgroundColor(
                            ContextCompat.getColor(
                                this@VendorDetailActivity,
                                R.color.colorPrimaryDark
                            )
                        )
                        bottomContainer.layoutParams.height = 0
                        bottomContainer.translationY = 0f
//
//                        toolBarTitle.visibility = View.GONE
//                        rlToolbar1.setBackgroundColor(Color.TRANSPARENT)
//                        bottomContainer.layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT
//                        bottomContainer.translationY = TypedValue.applyDimension(
//                            TypedValue.COMPLEX_UNIT_DIP,
//                            -48f,
//                            resources.displayMetrics
//                        )
                    }
                    else -> {
                        toolBarTitle.visibility = View.VISIBLE
                        llAction.visibility = View.VISIBLE
                        rlToolbar1.setBackgroundColor(
                            ContextCompat.getColor(
                                this@VendorDetailActivity,
                                R.color.colorPrimaryDark
                            )
                        )
                        bottomContainer.layoutParams.height = 0
                        bottomContainer.translationY = 0f
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        /*container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                window.statusBarColor = ContextCompat.getColor(this@VendorDetailActivity, R.color.myWhite)
            }

            override fun onPageSelected(position: Int) {
                window.statusBarColor = ContextCompat.getColor(this@VendorDetailActivity, R.color.myWhite)
                container.currentItem = position
                tabs.setScrollPosition(position, 0.0f, true)
            }

            override fun onPageScrollStateChanged(state: Int) {
                window.statusBarColor = ContextCompat.getColor(this@VendorDetailActivity, R.color.myWhite)
            }
        })*/
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                val fragment = OverviewFragment()
                val bundle = Bundle()
                bundle.putString("category", category)
                bundle.putString("serviceId", serviceId)
                bundle.putString("vendorTitle", vendorTitle)
                fragment.arguments = bundle
                return fragment
            } else if (position == 1) {
                if (category == "2") {
                    return PackagesFragment.newInstance(serviceId, vendorTitle)
                } else if (category == "4" || category == "5") {
                    return SalonServicesFragment.newInstance(serviceId, vendorTitle!!)
                } else if (category == "3") {
                    return AttireMenuFragment.newInstance(serviceId, "")
                } else {
                    return RestaurantMenuFragment.newInstance(serviceId, vendorTitle)
                }
            } else {
                return ReviewsFragment.newInstance(serviceId, vendorTitle)
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }

    inner class RestaurantSectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                val fragment = OverviewFragment()
                val bundle = Bundle()
                bundle.putString("category", category)
                bundle.putString("serviceId", serviceId)
                bundle.putString("vendorTitle", vendorTitle)
                Config.toolbartitle = ""
                Config.toolbartitle = vendorTitle
                fragment.arguments = bundle
                return fragment
            } else if (position == 1) {
                return BookingFragment.newInstance(serviceId, vendorTitle)
            } else if (position == 2) {
                return RestaurantMenuFragment.newInstance(serviceId, vendorTitle)
            } else {
                return ReviewsFragment.newInstance(serviceId, vendorTitle)
            }
        }

        override fun getCount(): Int {
            // Show 4 total pages.
            return 4
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            toolBarBack -> {
                if (PubFun.isInternetConnection(this@VendorDetailActivity)) {
                    if (Config.isVendorDetailOpeningFrom != Config.isVendorDetailComingFromMapFragment) {
                        if (!Config.isPopularLocationListCallFromInside) {
                            Config.bottomBarClickedName = Config.homeBottomBarClicked
                        }
                    }
                    if (Config.isFavoriteBottomBarListClick) {
                        Config.isFavoriteBottomBarListClick = false
                        Config.bottomBarClickedName = ""
                        Config.isRestaurantListOpeningFrom =
                            Config.restaurantListOpeningFromFavoriteBottomSection
                    }
                    onBackPressed()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            toolBarFavorite -> {
                if (PubFun.isInternetConnection(this@VendorDetailActivity)) {
                    if (is_liked == 0)
                        toolBarFavorite.setImageResource(R.drawable.ic_like_selected)
                    else
                        toolBarFavorite.setImageResource(R.drawable.ic_like_unselected)
                    isFavorite = !isFavorite
                    is_liked = is_liked!!.xor(1)
                    vendorDetailViewModel.favourite_service(serviceId)
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            toolBarShare -> {
                if (serviceDetail != null) {
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "text/plain"
                    share.putExtra(
                        Intent.EXTRA_TEXT,
                        serviceDetail?.title + "\n" + serviceDetail?.address + " \n" + "https://themarkettheory.com"
                    )
                    //"Find this service on The Market Theory | " +
                    startActivity(Intent.createChooser(share, "Share"))
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@VendorDetailActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = vendorTitle
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.apply {
                    visibility = View.GONE
                    post(object : Runnable {
                        override fun run() {
                            if (i == 0) {
                                myDialog.dismiss()
                            } else {
                                i--
                                postDelayed(this, 500)
                            }
                        }
                    })
                }
                /*it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.setOnClickListener {
                    myDialog.dismiss()
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}