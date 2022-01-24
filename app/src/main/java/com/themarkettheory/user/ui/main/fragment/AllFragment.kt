package com.themarkettheory.user.ui.main.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.CarouselEffectTransformer
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.FavoriteListener
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.interfaces.OnCategoryClickListener
import com.themarkettheory.user.newmodels.home.*
import com.themarkettheory.user.ui.coupon.CouponActivity
import com.themarkettheory.user.ui.coupon.CouponDetailActivity
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.activity.*
import com.themarkettheory.user.ui.main.adapter.*
import com.themarkettheory.user.ui.main.fragment.AllFragment.*
import com.themarkettheory.user.ui.restaurant.LiveDealsActivity
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity
import com.themarkettheory.user.ui.restaurant.restaurant_list.RestaurantListActivity
import com.themarkettheory.user.viewmodel.HomeViewModel
import com.themarkettheory.user.viewmodel.OfferViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_all.*

@AndroidEntryPoint
class AllFragment() : BaseFragment(), OnCategoryClickListener, View.OnClickListener {
    // TODO: Rename and change types of parameters
    lateinit var homeViewModel: HomeViewModel
    lateinit var newHomeRes: NewHomeRes

    /*View Model*/
    private lateinit var offerViewModel: OfferViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        offerViewModel = ViewModelProvider(this).get(OfferViewModel::class.java)

        newHomeRes = gson.fromJson(
            myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbNewHomeRes),
            NewHomeRes::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bannerListAdapter =
            BannerListAdapter(mainActivity, newHomeRes.data!!.slider!!)
        viewpager_banner.adapter = bannerListAdapter
//        viewpager_banner.pageMargin = Utils.dpToPx(24.0f, resources.displayMetrics)
        viewpager_banner.setPageTransformer(true, CarouselEffectTransformer(mainActivity))
        viewpager_banner.setPadding(40, 0, 40, 0)
        viewpager_banner.pageMargin = Utils.dpToPx(8.0f, resources.displayMetrics)
        indicator.setViewPager(viewpager_banner)
//        viewpager_banner.setFadeEnabled(true)
//        viewpager_banner.setFadeFactor(0.6f)
        /*CoverFlow.Builder()
            .with(viewpager_banner)
            .pagerMargin(0f)
            .scale(0.3f)
            .spaceSize(30f)
            .rotationY(0f)
            .build();*/
        /*picker.adapter = bannerListAdapter
        picker.offsetLeftAndRight(20)
        picker.setOrientation(DSVOrientation.HORIZONTAL)
        picker.setItemTransitionTimeMillis(400);
        picker.setItemTransformer(
            ScaleTransformer.Builder()
                .setMaxScale(1.05f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                .build()
        )*/
        if (newHomeRes.data!!.restaurants != null && !newHomeRes.data!!.restaurants!!.isEmpty()) {
            val listener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    if (PubFun.isInternetConnection(requireActivity())) {
                        val data = objects as Restaurant
                        Config.isMenuFragmentComingFrom = ""
                        Config.isBookingDetailOpeningFrom =
                            Config.isBookingDetailOpeningFromRecommnededListAdapter
                        startActivity(
                            Intent(context, VendorDetailActivity::class.java)
                                .putExtra("category", data.categoryId.toString())
                                .putExtra("serviceId", data.id.toString())
                                .putExtra("vendorTitle", data.title)
                        )
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }
            }
            var recommendedListAdapter = RecommendedListAdapter(mainActivity, listener) {
                if (PubFun.isInternetConnection(requireActivity())) {
                    homeViewModel.favourite_service(it.id.toString())
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
            val liveDealListener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    if (PubFun.isInternetConnection(requireActivity())) {
                        val restaurant = objects as Restaurant
                        Config.vendorDetailServiceId = restaurant.id!!.toString().trim()
                        startActivity(Intent(activity, LiveDealsActivity::class.java))
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }
            }
            var liveDealsListAdapter = LiveDealsListAdapter(mainActivity, liveDealListener) {
                if (PubFun.isInternetConnection(requireActivity())) {
                    homeViewModel.favourite_service(it.id.toString())
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            val recommendedList = ArrayList<Restaurant>()
            val liveDealsList = ArrayList<Restaurant>()


            //region Recommended
            for (i in newHomeRes.data!!.restaurants!!.indices) {
                if (newHomeRes.data!!.restaurants!![i].isRecommanded == 1) {
                    val recommended = Restaurant()
                    recommended.id = newHomeRes.data!!.restaurants!![i].id
                    recommended.title = newHomeRes.data!!.restaurants!![i].title
                    recommended.description = newHomeRes.data!!.restaurants!![i].description
                    recommended.address = newHomeRes.data!!.restaurants!![i].address
                    recommended.latitude = newHomeRes.data!!.restaurants!![i].latitude
                    recommended.longitude = newHomeRes.data!!.restaurants!![i].longitude
                    recommended.isDeleted = newHomeRes.data!!.restaurants!![i].isDeleted
                    recommended.categoryId = newHomeRes.data!!.restaurants!![i].categoryId
                    recommended.createdAt = newHomeRes.data!!.restaurants!![i].createdAt
                    recommended.updatedAt = newHomeRes.data!!.restaurants!![i].updatedAt
                    recommended.rating = newHomeRes.data!!.restaurants!![i].rating
                    recommended.totalRating = newHomeRes.data!!.restaurants!![i].totalRating
                    recommended.area = newHomeRes.data!!.restaurants!![i].area
                    recommended.currency = newHomeRes.data!!.restaurants!![i].currency
                    recommended.currencyStr = newHomeRes.data!!.restaurants!![i].currencyStr
                    recommended.facebookUrl = newHomeRes.data!!.restaurants!![i].facebookUrl
                    recommended.twitterUrl = newHomeRes.data!!.restaurants!![i].twitterUrl
                    recommended.googleUrl = newHomeRes.data!!.restaurants!![i].googleUrl
                    recommended.instagramUrl =
                        newHomeRes.data!!.restaurants!![i].instagramUrl
                    recommended.subCategoryId =
                        newHomeRes.data!!.restaurants!![i].subCategoryId
                    recommended.email = newHomeRes.data!!.restaurants!![i].email
                    recommended.mobile = newHomeRes.data!!.restaurants!![i].mobile
                    recommended.isRecommanded =
                        newHomeRes.data!!.restaurants!![i].isRecommanded
                    recommended.youtubeUrl = newHomeRes.data!!.restaurants!![i].youtubeUrl
                    recommended.cuisineTypes =
                        newHomeRes.data!!.restaurants!![i].cuisineTypes
                    recommended.street = newHomeRes.data!!.restaurants!![i].street
                    recommended.town = newHomeRes.data!!.restaurants!![i].town
                    recommended.gstNo = newHomeRes.data!!.restaurants!![i].gstNo
                    recommended.deviceSystem =
                        newHomeRes.data!!.restaurants!![i].deviceSystem
                    recommended.advertise = newHomeRes.data!!.restaurants!![i].advertise
                    recommended.isOwner = newHomeRes.data!!.restaurants!![i].isOwner
                    recommended.ownerName = newHomeRes.data!!.restaurants!![i].ownerName
                    recommended.address = newHomeRes.data!!.restaurants!![i].address
                    recommended.isService = newHomeRes.data!!.restaurants!![i].isService
                    recommended.isVeg = newHomeRes.data!!.restaurants!![i].isVeg
                    recommended.isNonVeg = newHomeRes.data!!.restaurants!![i].isNonVeg
                    recommended.isEgg = newHomeRes.data!!.restaurants!![i].isEgg
                    recommended.serviceCost = newHomeRes.data!!.restaurants!![i].serviceCost
                    recommended.serviceName = newHomeRes.data!!.restaurants!![i].serviceName
                    recommended.otherOption = newHomeRes.data!!.restaurants!![i].otherOption
                    recommended.isInstering = newHomeRes.data!!.restaurants!![i].isInstering
                    recommended.notes = newHomeRes.data!!.restaurants!![i].notes
                    recommended.submitBy = newHomeRes.data!!.restaurants!![i].submitBy
                    recommended.isMarketVendor =
                        newHomeRes.data!!.restaurants!![i].isMarketVendor
                    recommended.submitDate = newHomeRes.data!!.restaurants!![i].submitDate
                    recommended.distance = newHomeRes.data!!.restaurants!![i].distance
                    recommended.isLiveDeal = newHomeRes.data!!.restaurants!![i].isLiveDeal
                    recommended.image = newHomeRes.data!!.restaurants!![i].image
                    recommended.baseUrl = newHomeRes.data!!.restaurants!![i].baseUrl
                    recommended.isOpen = newHomeRes.data!!.restaurants!![i].isOpen
                    recommended.restaurantfavourite =
                        newHomeRes.data!!.restaurants!![i].restaurantfavourite
                    recommended.categoryName =
                        newHomeRes.data!!.restaurants!![i].categoryName
                    recommended.isFavourite =
                        newHomeRes.data!!.restaurants!![i].isFavourite
                    recommendedList.add(recommended)
                }
            }

            if (recommendedList.isNotEmpty()) {
                rlRecommended.visibility = View.VISIBLE
                rvRecommended.visibility = View.VISIBLE

                rvRecommended.adapter = recommendedListAdapter
                rvRecommended.layoutManager = LinearLayoutManager(
                    mainActivity,
                    LinearLayoutManager.HORIZONTAL, false
                )

                val favoriteListener = object : FavoriteListener {
                    override fun onMarkFavorite(title: String, isFavorite: Int) {
                        liveDeals@ for (i in liveDealsList.indices) {
                            if (liveDealsList[i].title == title) {
                                liveDealsList[i].isFavourite = isFavorite
                                liveDealsListAdapter.notifyDataSetChanged()
                                break@liveDeals
                            }
                        }
                    }
                }
                recommendedListAdapter.addRecommendedList(recommendedList)
                recommendedListAdapter.addFavoriteListener(favoriteListener)
            } else {
                rlRecommended.visibility = View.GONE
                rvRecommended.visibility = View.GONE
            }
            //endregion

            //region Live Deals
            for (i in newHomeRes.data!!.restaurants!!.indices) {
                if (newHomeRes.data!!.restaurants!![i].isLiveDeal == true) {
                    val liveDeals = Restaurant()
                    liveDeals.id = newHomeRes.data!!.restaurants!![i].id
                    liveDeals.title = newHomeRes.data!!.restaurants!![i].title
                    liveDeals.description = newHomeRes.data!!.restaurants!![i].description
                    liveDeals.address = newHomeRes.data!!.restaurants!![i].address
                    liveDeals.latitude = newHomeRes.data!!.restaurants!![i].latitude
                    liveDeals.longitude = newHomeRes.data!!.restaurants!![i].longitude
                    liveDeals.isDeleted = newHomeRes.data!!.restaurants!![i].isDeleted
                    liveDeals.categoryId = newHomeRes.data!!.restaurants!![i].categoryId
                    liveDeals.createdAt = newHomeRes.data!!.restaurants!![i].createdAt
                    liveDeals.updatedAt = newHomeRes.data!!.restaurants!![i].updatedAt
                    liveDeals.rating = newHomeRes.data!!.restaurants!![i].rating
                    liveDeals.totalRating = newHomeRes.data!!.restaurants!![i].totalRating
                    liveDeals.area = newHomeRes.data!!.restaurants!![i].area
                    liveDeals.currency = newHomeRes.data!!.restaurants!![i].currency
                    liveDeals.currencyStr = newHomeRes.data!!.restaurants!![i].currencyStr
                    liveDeals.facebookUrl = newHomeRes.data!!.restaurants!![i].facebookUrl
                    liveDeals.twitterUrl = newHomeRes.data!!.restaurants!![i].twitterUrl
                    liveDeals.googleUrl = newHomeRes.data!!.restaurants!![i].googleUrl
                    liveDeals.instagramUrl = newHomeRes.data!!.restaurants!![i].instagramUrl
                    liveDeals.subCategoryId = newHomeRes.data!!.restaurants!![i].subCategoryId
                    liveDeals.email = newHomeRes.data!!.restaurants!![i].email
                    liveDeals.mobile = newHomeRes.data!!.restaurants!![i].mobile
                    liveDeals.isRecommanded = newHomeRes.data!!.restaurants!![i].isRecommanded
                    liveDeals.youtubeUrl = newHomeRes.data!!.restaurants!![i].youtubeUrl
                    liveDeals.cuisineTypes = newHomeRes.data!!.restaurants!![i].cuisineTypes
                    liveDeals.street = newHomeRes.data!!.restaurants!![i].street
                    liveDeals.town = newHomeRes.data!!.restaurants!![i].town
                    liveDeals.gstNo = newHomeRes.data!!.restaurants!![i].gstNo
                    liveDeals.deviceSystem = newHomeRes.data!!.restaurants!![i].deviceSystem
                    liveDeals.advertise = newHomeRes.data!!.restaurants!![i].advertise
                    liveDeals.isOwner = newHomeRes.data!!.restaurants!![i].isOwner
                    liveDeals.ownerName = newHomeRes.data!!.restaurants!![i].ownerName
                    liveDeals.address = newHomeRes.data!!.restaurants!![i].address
                    liveDeals.isService = newHomeRes.data!!.restaurants!![i].isService
                    liveDeals.isVeg = newHomeRes.data!!.restaurants!![i].isVeg
                    liveDeals.isNonVeg = newHomeRes.data!!.restaurants!![i].isNonVeg
                    liveDeals.isEgg = newHomeRes.data!!.restaurants!![i].isEgg
                    liveDeals.serviceCost = newHomeRes.data!!.restaurants!![i].serviceCost
                    liveDeals.serviceName = newHomeRes.data!!.restaurants!![i].serviceName
                    liveDeals.otherOption = newHomeRes.data!!.restaurants!![i].otherOption
                    liveDeals.isInstering = newHomeRes.data!!.restaurants!![i].isInstering
                    liveDeals.notes = newHomeRes.data!!.restaurants!![i].notes
                    liveDeals.submitBy = newHomeRes.data!!.restaurants!![i].submitBy
                    liveDeals.isMarketVendor = newHomeRes.data!!.restaurants!![i].isMarketVendor
                    liveDeals.submitDate = newHomeRes.data!!.restaurants!![i].submitDate
                    liveDeals.distance = newHomeRes.data!!.restaurants!![i].distance
                    liveDeals.isLiveDeal = newHomeRes.data!!.restaurants!![i].isLiveDeal
                    liveDeals.image = newHomeRes.data!!.restaurants!![i].image
                    liveDeals.baseUrl = newHomeRes.data!!.restaurants!![i].baseUrl
                    liveDeals.isOpen = newHomeRes.data!!.restaurants!![i].isOpen
                    liveDeals.restaurantfavourite =
                        newHomeRes.data!!.restaurants!![i].restaurantfavourite
                    liveDeals.categoryName = newHomeRes.data!!.restaurants!![i].categoryName
                    liveDeals.isFavourite = newHomeRes.data!!.restaurants!![i].isFavourite
                    liveDealsList.add(liveDeals)
                }
            }
            if (liveDealsList.isNotEmpty()) {
                rlLiveDeal.visibility = View.VISIBLE
                rvLiveDeal.visibility = View.VISIBLE

                rvLiveDeal.apply {
                    adapter = liveDealsListAdapter
                    layoutManager = LinearLayoutManager(
                        mainActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                }

                val favoriteListener = object : FavoriteListener {
                    override fun onMarkFavorite(title: String, isFavorite: Int) {
                        recommended@ for (i in recommendedList.indices) {
                            if (recommendedList[i].title == title) {
                                recommendedList[i].isFavourite = isFavorite
                                recommendedListAdapter.notifyDataSetChanged()
                                break@recommended
                            }
                        }
                    }
                }
                liveDealsListAdapter.addLiveDeals(liveDealsList)
                liveDealsListAdapter.addFavoriteListener(favoriteListener)
            } else {
                rlLiveDeal.visibility = View.GONE
                rvLiveDeal.visibility = View.GONE
            }
            //endregion
        } else {
            rlRecommended.visibility = View.GONE
            rvRecommended.visibility = View.GONE

            rlLiveDeal.visibility = View.GONE
            rvLiveDeal.visibility = View.GONE
        }

        if (newHomeRes.data!!.offers != null && newHomeRes.data!!.offers!!.isNotEmpty()) {
            rlTrendingOffer.visibility = View.VISIBLE
            rvTrendingOffers.visibility = View.VISIBLE

            var favoritePosition = 0
            val favoriteListener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    val offerData = objects as Offer
                    favoritePosition = pos
                    callOfferFavoriteCoupon(offerData.id!!.toString().trim())
                }
            }

            val rowListener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    if (PubFun.isInternetConnection(requireActivity())) {
                        startActivity(Intent(context, CouponDetailActivity::class.java))
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }
            }
            val trendingOfferListAdapter =
                TrendingOfferListAdapter(
                    mainActivity,
                    newHomeRes.data!!.offers!!,
                    true,
                    rowListener,
                    favoriteListener,
                    Config.isCouponDetailOpeningFromAllFragment,
                    0,
                    "",
                    ""
                ) {
                    if (PubFun.isInternetConnection(requireActivity())) {
                        homeViewModel.activate_coupon(it.id.toString())
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }
            rvTrendingOffers.adapter = trendingOfferListAdapter
            rvTrendingOffers.layoutManager = LinearLayoutManager(
                mainActivity,
                LinearLayoutManager.HORIZONTAL, false
            )

            //region Offer Loading Progress Bar
            offerViewModel.isLoading.observe(requireActivity(), {
                try {
                    if (it!!) {
                        Utils.showProgress(requireActivity())
                    } else {
                        Utils.hideProgress(requireActivity())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            //endregion

            //region Offer Favorite Coupon
            offerViewModel.responseFavoriteCoupon.observe(requireActivity(), {
                try {
                    when (it.status!!) {
                        0 -> showMsgDialogAndProceed(it.message!!.trim())
                        1 -> trendingOfferListAdapter.setFavoriteMark(
                            favoritePosition,
                            if (it.message!!.trim().lowercase() == "added")
                                1 else 0
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            //endregion
        } else {
            rlTrendingOffer.visibility = View.GONE
            rvTrendingOffers.visibility = View.GONE
        }

        if (newHomeRes.data!!.nearby != null && newHomeRes.data!!.nearby!!.isNotEmpty()) {
            lylNearby.visibility = View.VISIBLE
            rvnearest.visibility = View.VISIBLE

            val listener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    if (PubFun.isInternetConnection(requireActivity())) {
                        val data = objects as Nearby
                        Config.isMenuFragmentComingFrom = ""
                        Config.isBookingDetailOpeningFrom =
                            Config.isBookingDetailOpeningFromNearestListAdapter
                        startActivity(
                            Intent(requireActivity(), VendorDetailActivity::class.java)
                                .putExtra("category", data.categoryId.toString())
                                .putExtra("serviceId", data.id.toString())
                                .putExtra("vendorTitle", data.title)
                        )
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }
            }
            val nearestListAdapter =
                NearestListAdapter(mainActivity, listener, newHomeRes.data!!.nearby!!)
            rvnearest.adapter = nearestListAdapter
            rvnearest.layoutManager = GridLayoutManager(activity, 2)
        } else {
            lylNearby.visibility = View.GONE
            rvnearest.visibility = View.GONE
        }

        //region Popular Location Names
        if (newHomeRes.data!!.popularLocations != null && newHomeRes.data!!.popularLocations!!.isNotEmpty()) {
            rlPopularLocationsNames.visibility = View.VISIBLE
            rvPopularLocationName.visibility = View.VISIBLE

            val popularLocationListener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    val data = objects as PopularLocation
                    Config.isRestaurantListOpeningFrom =
                        Config.restaurantListOpeningFromPopularLocationId

                    Config.myRoomDatabase.daoConfig().apply {
                        deleteConfigTableByField(Config.dbPopularLocationId)
                        insertConfigTable(
                            TableConfig(
                                Config.dbPopularLocationId,
                                data.id!!.toString().trim()
                            )
                        )

                        deleteConfigTableByField(Config.dbPopularLocationAddress)
                        insertConfigTable(
                            TableConfig(
                                Config.dbPopularLocationAddress,
                                data.address!!.trim()
                            )
                        )
                    }

                    startActivity(Intent(requireActivity(), RestaurantListActivity::class.java))
                }
            }

            val popularEventsAdapter =
                PopularLocationNamesAdapter(
                    mainActivity,
                    newHomeRes.data!!.popularLocations!!,
                    popularLocationListener
                )
            rvPopularLocationName.apply {
                adapter = popularEventsAdapter
                layoutManager = GridLayoutManager(activity, 2)
            }
        } else {
            rlPopularLocationsNames.visibility = View.GONE
            rvPopularLocationName.visibility = View.GONE
        }
        //endregion

        /*if (param1?.upcomingEvent != null) {
            val upcomingListAdapter =
                UpcomingListAdapter(mainActivity, param1?.upcomingEvent!!) {
                    homeViewModel.favourite(it.id.toString())
                }
            viewpager_upcomingEvents.adapter = upcomingListAdapter
            viewpager_upcomingEvents.setPadding(40, 0, 40, 0)
            viewpager_upcomingEvents.setPageMargin(10);
            viewpager_upcomingEvents.clipToPadding = false
        }*/

        homeViewModel.responseActivateCoupon.observe(viewLifecycleOwner, Observer {
            if (it.status!!) {
                showMsgDialogAndProceed("Coupon activated")
            } else {
                showMsgDialogAndProceed(it.message.toString().trim())
            }
        })

        llRestaurant.setOnClickListener(this)
        llSalon.setOnClickListener(this)
        llGym.setOnClickListener(this)
        llSpa.setOnClickListener(this)
        llBakery.setOnClickListener(this)
        llRetail.setOnClickListener(this)

        tvRecommendedSeeAll.setOnClickListener(this)
        tvLiveDealSeeAll.setOnClickListener(this)
        tvPopularLocationSeeAll.setOnClickListener(this)
        tvnearestSeeAll.setOnClickListener(this)
        tvUpcominSeeAll.setOnClickListener(this)
        tvOffersSeeAll.setOnClickListener(this)
        rlPoints.setOnClickListener(this)
        rlMyCoupons.setOnClickListener(this)
        rlMyBookings.setOnClickListener(this)
    }

    private fun callOfferFavoriteCoupon(offerId: String) {
        try {
            if (PubFun.isInternetConnection(requireActivity())) {
                offerViewModel.offerFavoriteCoupon(offerId)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(requireActivity())
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Dashboard"
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.post(object : Runnable {
                    override fun run() {
                        if (i == 0) {
                            myDialog.dismiss()
                        } else {
                            i--
                            it.btnDialogLogout.postDelayed(this, 1000)
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            tvRecommendedSeeAll -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    Config.isRestaurantListOpeningFrom =
                        Config.restaurantListOpeningFromRecommendedSeeAll
                    startActivity(
                        Intent(
                            mainActivity,
                            RestaurantListActivity::class.java
                        )
                    )
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }

            }

            tvOffersSeeAll -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    Config.isRestaurantListOpeningFrom =
                        Config.restaurantListOpeningFromTrendingOfferSeeAll
                    startActivity(
                        Intent(
                            mainActivity,
                            RestaurantListActivity::class.java
                        )
                    )
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            tvLiveDealSeeAll -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    Config.isRestaurantListOpeningFrom =
                        Config.restaurantListOpeningFromLiveDealSeeAll
                    startActivity(
                        Intent(
                            mainActivity,
                            RestaurantListActivity::class.java
                        )
                    )
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }

            }

            tvnearestSeeAll -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    Config.isRestaurantListOpeningFrom =
                        Config.restaurantListOpeningFromNearestSellAll
                    startActivity(
                        Intent(
                            mainActivity,
                            RestaurantListActivity::class.java
                        )
                    )
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            tvPopularLocationSeeAll -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    Config.isRestaurantListOpeningFrom =
                        Config.restaurantListOpeningFromPopularLocationSeeAll
                    startActivity(
                        Intent(
                            mainActivity,
                            RestaurantListActivity::class.java
                        )
                    )
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            rlPoints -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    Config.isMyPointClickedFromHome = true
                    startActivity(
                        Intent(mainActivity, TotalPointsActivity::class.java)
                            .putExtra("title", "My Points")
                    )
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            rlMyCoupons -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    Config.isCouponOpeningFromBucket = false
                    Config.isEventBottomBarClicked = true
                    Config.isMyCouponClickedFromHome = true
                    Config.isCouponComingFromAllFragment = true
                    startActivity(Intent(mainActivity, CouponActivity::class.java))
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }

            }

            tvUpcominSeeAll -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    startActivity(
                        Intent(
                            mainActivity,
                            EventsActivity::class.java
                        )
                    )
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            rlMyBookings -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    Config.isMyBookingClickedFromHome = true
                    startActivity(Intent(activity, MyTableBookingsActivity::class.java))
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            llRestaurant -> {
//                Utils.category.value = "1"
                onCategoryClick("1")
            }

            llSalon -> {
//                Utils.category.value = "4"
                onCategoryClick("4")
            }

            llGym -> {
//                Utils.category.value = "2"
                onCategoryClick("2")
            }

            llSpa -> {
//                Utils.category.value = "5"
                onCategoryClick("5")
            }

            llBakery -> {
//                Utils.category.value = "6"
                onCategoryClick("6")
            }

            llRetail -> {
//                Utils.category.value = "3"
                onCategoryClick("3")
            }
        }
    }

    override fun onCategoryClick(category: String) {
        Utils.category.value = category
    }
}