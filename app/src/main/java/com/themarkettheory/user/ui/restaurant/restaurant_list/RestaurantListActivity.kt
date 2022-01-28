package com.themarkettheory.user.ui.restaurant.restaurant_list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.getcategories.Category
import com.themarkettheory.user.newmodels.home.NewHomeRes
import com.themarkettheory.user.newmodels.home.Offer
import com.themarkettheory.user.newmodels.home.PopularLocation
import com.themarkettheory.user.newmodels.map.Restaurant
import com.themarkettheory.user.ui.coupon.CouponDetailActivity
import com.themarkettheory.user.ui.dialog.dialogPopupList.AdapterDialogPopupList
import com.themarkettheory.user.ui.dialog.dialogPopupList.clsPopupDialogList
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.activity.BaseActivity
import com.themarkettheory.user.ui.main.activity.SearchActivity
import com.themarkettheory.user.ui.main.adapter.*
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity
import com.themarkettheory.user.viewmodel.HomeViewModel
import com.themarkettheory.user.viewmodel.OfferViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_restaurant_list.*
import kotlinx.android.synthetic.main.fragment_all.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*
import kotlin.collections.ArrayList

class RestaurantListActivity : BaseActivity(), View.OnClickListener {
    //General
    private var selectedFoodCategoryName = ""
    private var selectedType = ""
    private var selectedFavorite = "0" //Default value
    private var selectedPopularLocationId = "" //Default value

    //Food Type
    private var selectedFoodType = ""
    private val strVeg = "Veg"
    private val strNonVeg = "Non Veg"
    private val strEgg = "Egg"

    private val strVegValue = "1"
    private val strNonVegValue = "2"
    private val strEggValue = "3"

    //Sort Type
    private var selectedSortType = ""
    private val strAll = "All"
    private val strRecommendedByTMT = "Recommended by TMT"
    private val strRatingHighToLow = "Rating: High to Low"
    private val strMostFavorite = "Most Favorite"
    private val strNearBy = "Near By"
    private val strAscending = "Ascending"

    private val strDefaultValue = ""
    private val strRecommendedByTMTValue = "1"
    private val strRatingHighToLowValue = "2"
    private val strMostFavoriteValue = "3"
    private val strAscendingValue = "4"
    private val strNearByValue = "5"

    //Restaurant Type
    private val strRecommendedType = "1"
    private val strOffersType = "2"
    private val strNearestType = "3"
    private val strPopularLocationType = "4"
    private val strLiveDealsType = "5"

    //Dashboard Res
    private lateinit var newHomeRes: NewHomeRes

    //Food Categories Adapters
    private lateinit var foodCategoryAdapter: HomeCategoriesAdapter
    private var subCategoriesList = ArrayList<Category>()

    //Restaurant Adapters
    private lateinit var restaurantListAdapter: RestaurantListAdapter
    private lateinit var restaurantFavoriteListAdapter: RestaurantFavoriteListAdapter

    //HomeViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var offerViewModel: OfferViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_list)
        try {
            //Dashboard Res
            newHomeRes = gson.fromJson(
                myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbNewHomeRes),
                NewHomeRes::class.java
            )

            //HomeViewModel
            homeViewModel =
                ViewModelProvider(this@RestaurantListActivity).get(HomeViewModel::class.java)

            //OfferViewModel
            offerViewModel =
                ViewModelProvider(this@RestaurantListActivity).get(OfferViewModel::class.java)

            //Current Location
            tvRestaurantListLocation.text =
                myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbCurrentCitySelection)

            //Click Listeners
            ivRestaurantListMenu.setOnClickListener(this)
            cvRestaurantListFoodType.setOnClickListener(this)
            cvRestaurantListFoodSort.setOnClickListener(this)
            ivRestaurantListBack.setOnClickListener(this)
            lylHomeBottomBar.setOnClickListener(this)
            lylFavoriteBottomBar.setOnClickListener(this)
            ivRestaurantListLocationBottomBar.setOnClickListener(this)
            lylEventBottomBar.setOnClickListener(this)
            lylMenuBottomBar.setOnClickListener(this)
            llRestaurantListSearch.setOnClickListener(this)

            //Hide or Show toolbar and mapIcon as per different conditions
            when (Config.isRestaurantListOpeningFrom) {
                Config.restaurantListOpeningFromMapFragment -> {
                    ivRestaurantListMenu.visibility = View.VISIBLE
                    lylRestaurantListToolbar.visibility = View.GONE
                    ivMapLocation.visibility = View.VISIBLE
                    tvRestaurantListLocation.visibility = View.VISIBLE
                    frameRestaurantListNotification.visibility = View.VISIBLE
                    llRestaurantListSearch.visibility = View.VISIBLE
                    rvRestaurantListDetails.visibility = View.VISIBLE
                    rvRestaurantListPopularLocation.visibility = View.GONE
                    rvRestaurantFavoriteList.visibility = View.GONE
                    cvRestaurantListFoodType.visibility = View.VISIBLE
                    cvRestaurantListFoodSort.visibility = View.VISIBLE
                    tvRestaurantListTitle.text = ""
                    ivRestaurantListBack.visibility = View.GONE
                    lylRestaurantListBottomBar.visibility = View.VISIBLE
                    ivRestaurantListLocationBottomBar.visibility = View.VISIBLE
                    ivRestaurantListHomeBottomBar.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@RestaurantListActivity,
                            R.drawable.ic_home_unselected
                        )
                    )
                    ivRestaurantListHomeLineBottomBar.visibility = View.INVISIBLE
                }
                Config.restaurantListOpeningFromRecommendedSeeAll -> {
                    ivRestaurantListMenu.visibility = View.GONE
                    lylRestaurantListToolbar.visibility = View.VISIBLE
                    ivMapLocation.visibility = View.GONE
                    tvRestaurantListLocation.visibility = View.GONE
                    frameRestaurantListNotification.visibility = View.GONE
                    llRestaurantListSearch.visibility = View.GONE
                    rvRestaurantListDetails.visibility = View.VISIBLE
                    rvRestaurantListPopularLocation.visibility = View.GONE
                    rvRestaurantFavoriteList.visibility = View.GONE
                    cvRestaurantListFoodType.visibility = View.VISIBLE
                    cvRestaurantListFoodSort.visibility = View.VISIBLE
                    tvRestaurantListTitle.text = getString(R.string.recommended)
                    ivRestaurantListBack.visibility = View.VISIBLE
                    lylRestaurantListBottomBar.visibility = View.GONE
                    ivRestaurantListLocationBottomBar.visibility = View.GONE
                }
                Config.restaurantListOpeningFromHomeFragment -> {
                    ivRestaurantListMenu.visibility = View.VISIBLE
                    lylRestaurantListToolbar.visibility = View.GONE
                    ivMapLocation.visibility = View.VISIBLE
                    tvRestaurantListLocation.visibility = View.VISIBLE
                    frameRestaurantListNotification.visibility = View.VISIBLE
                    rvRestaurantListDetails.visibility = View.VISIBLE
                    rvRestaurantListPopularLocation.visibility = View.GONE
                    rvRestaurantFavoriteList.visibility = View.GONE
                    cvRestaurantListFoodType.visibility = View.VISIBLE
                    cvRestaurantListFoodSort.visibility = View.VISIBLE
                    tvRestaurantListTitle.text = ""
                    ivRestaurantListBack.visibility = View.GONE
                    lylRestaurantListBottomBar.visibility = View.VISIBLE
                    ivRestaurantListLocationBottomBar.visibility = View.VISIBLE
                    ivRestaurantListHomeBottomBar.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@RestaurantListActivity,
                            R.drawable.ic_home_selected
                        )
                    )
                    ivRestaurantListFavoriteBottomBar.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@RestaurantListActivity,
                            R.drawable.ic_favorite_unselected
                        )
                    )
                    ivRestaurantListHomeLineBottomBar.visibility = View.VISIBLE
                    ivRestaurantListFavoriteLineBottomBar.visibility = View.GONE
                }
                Config.restaurantListOpeningFromLiveDealSeeAll -> {
                    ivRestaurantListMenu.visibility = View.GONE
                    lylRestaurantListToolbar.visibility = View.VISIBLE
                    ivMapLocation.visibility = View.GONE
                    tvRestaurantListLocation.visibility = View.GONE
                    frameRestaurantListNotification.visibility = View.GONE
                    llRestaurantListSearch.visibility = View.GONE
                    rvRestaurantListDetails.visibility = View.VISIBLE
                    rvRestaurantListPopularLocation.visibility = View.GONE
                    rvRestaurantFavoriteList.visibility = View.GONE
                    cvRestaurantListFoodType.visibility = View.VISIBLE
                    cvRestaurantListFoodSort.visibility = View.VISIBLE
                    tvRestaurantListTitle.text = getString(R.string.live_deal)
                    ivRestaurantListBack.visibility = View.VISIBLE
                    lylRestaurantListBottomBar.visibility = View.GONE
                    ivRestaurantListLocationBottomBar.visibility = View.GONE
                }
                Config.restaurantListOpeningFromNearestSellAll -> {
                    ivRestaurantListMenu.visibility = View.GONE
                    lylRestaurantListToolbar.visibility = View.VISIBLE
                    ivMapLocation.visibility = View.GONE
                    tvRestaurantListLocation.visibility = View.GONE
                    frameRestaurantListNotification.visibility = View.GONE
                    llRestaurantListSearch.visibility = View.GONE
                    rvRestaurantListDetails.visibility = View.VISIBLE
                    rvRestaurantListPopularLocation.visibility = View.GONE
                    rvRestaurantFavoriteList.visibility = View.GONE
                    cvRestaurantListFoodType.visibility = View.VISIBLE
                    cvRestaurantListFoodSort.visibility = View.VISIBLE
                    tvRestaurantListTitle.text = getString(R.string.nearest)
                    ivRestaurantListBack.visibility = View.VISIBLE
                    lylRestaurantListBottomBar.visibility = View.GONE
                    ivRestaurantListLocationBottomBar.visibility = View.GONE
                }
                Config.restaurantListOpeningFromPopularLocationSeeAll -> {
                    ivRestaurantListMenu.visibility = View.GONE
                    lylRestaurantListToolbar.visibility = View.VISIBLE
                    ivMapLocation.visibility = View.GONE
                    tvRestaurantListLocation.visibility = View.GONE
                    frameRestaurantListNotification.visibility = View.GONE
                    llRestaurantListSearch.visibility = View.GONE
                    rvRestaurantListDetails.visibility = View.GONE
                    rvRestaurantListPopularLocation.visibility = View.VISIBLE
                    rvRestaurantFavoriteList.visibility = View.GONE
                    cvRestaurantListFoodType.visibility = View.GONE
                    cvRestaurantListFoodSort.visibility = View.GONE
                    tvRestaurantListTitle.text = getString(R.string.popular_locations)
                    ivRestaurantListBack.visibility = View.VISIBLE
                    lylRestaurantListBottomBar.visibility = View.GONE
                    ivRestaurantListLocationBottomBar.visibility = View.GONE
                }
                Config.restaurantListOpeningFromTrendingOfferSeeAll -> {
                    ivRestaurantListMenu.visibility = View.GONE
                    lylRestaurantListToolbar.visibility = View.VISIBLE
                    ivMapLocation.visibility = View.GONE
                    tvRestaurantListLocation.visibility = View.GONE
                    frameRestaurantListNotification.visibility = View.GONE
                    llRestaurantListSearch.visibility = View.GONE
                    rvRestaurantListDetails.visibility = View.GONE
                    rvRestaurantListPopularLocation.visibility = View.GONE
                    rvRestaurantListTrendingOffers.visibility = View.VISIBLE
                    rvRestaurantFavoriteList.visibility = View.GONE
                    cvRestaurantListFoodType.visibility = View.GONE
                    cvRestaurantListFoodSort.visibility = View.GONE
                    tvRestaurantListTitle.text = getString(R.string.trending_offers)
                    ivRestaurantListBack.visibility = View.VISIBLE
                    lylRestaurantListBottomBar.visibility = View.GONE
                    ivRestaurantListLocationBottomBar.visibility = View.GONE
                }
                Config.restaurantListOpeningFromFavoriteBottomSection -> {
                    ivRestaurantListMenu.visibility = View.GONE
                    lylRestaurantListToolbar.visibility = View.VISIBLE
                    ivMapLocation.visibility = View.GONE
                    tvRestaurantListLocation.visibility = View.GONE
                    frameRestaurantListNotification.visibility = View.GONE
                    llRestaurantListSearch.visibility = View.GONE
                    rvRestaurantListDetails.visibility = View.GONE
                    rvRestaurantListPopularLocation.visibility = View.GONE
                    rvRestaurantListTrendingOffers.visibility = View.GONE
                    rvRestaurantFavoriteList.visibility = View.VISIBLE
                    cvRestaurantListFoodType.visibility = View.GONE
                    cvRestaurantListFoodSort.visibility = View.GONE
                    tvRestaurantListTitle.text = getString(R.string.favorite)
                    ivRestaurantListBack.visibility = View.GONE
                    lylRestaurantListBottomBar.visibility = View.VISIBLE
                    ivRestaurantListHomeBottomBar.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@RestaurantListActivity,
                            R.drawable.ic_home_unselected
                        )
                    )
                    ivRestaurantListFavoriteBottomBar.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@RestaurantListActivity,
                            R.drawable.ic_favorite_selected
                        )
                    )
                    ivRestaurantListLocationBottomBar.visibility = View.VISIBLE
                    ivRestaurantListHomeLineBottomBar.visibility = View.GONE
                    ivRestaurantListFavoriteLineBottomBar.visibility = View.VISIBLE
                }
                Config.restaurantListOpeningFromPopularLocationId -> {
                    ivRestaurantListMenu.visibility = View.GONE
                    lylRestaurantListToolbar.visibility = View.VISIBLE
                    ivMapLocation.visibility = View.GONE
                    tvRestaurantListLocation.visibility = View.GONE
                    frameRestaurantListNotification.visibility = View.GONE
                    llRestaurantListSearch.visibility = View.GONE
                    rvRestaurantListDetails.visibility = View.VISIBLE
                    rvRestaurantListPopularLocation.visibility = View.GONE
                    rvRestaurantListTrendingOffers.visibility = View.GONE
                    rvRestaurantFavoriteList.visibility = View.GONE
                    cvRestaurantListFoodType.visibility = View.VISIBLE
                    cvRestaurantListFoodSort.visibility = View.VISIBLE
                    tvRestaurantListTitle.text = Config.myRoomDatabase.daoConfig()
                        .selectConfigTableByField(Config.dbPopularLocationAddress)!!
                    ivRestaurantListBack.visibility = View.VISIBLE
                    lylRestaurantListBottomBar.visibility = View.GONE
                    ivRestaurantListLocationBottomBar.visibility = View.GONE
                }
            }

            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            Config.isFavoriteMoveToBack = false
            //Call Service List API without any filters
            if (Config.isRestaurantListOpeningFrom != Config.restaurantListOpeningFromHomeFragment) {
                selectedType = when (Config.isRestaurantListOpeningFrom) {
                    Config.restaurantListOpeningFromRecommendedSeeAll ->
                        strRecommendedType
                    Config.restaurantListOpeningFromNearestSellAll ->
                        strNearestType
                    Config.restaurantListOpeningFromLiveDealSeeAll ->
                        strLiveDealsType
                    Config.restaurantListOpeningFromTrendingOfferSeeAll ->
                        strOffersType
                    Config.restaurantListOpeningFromPopularLocationSeeAll ->
                        strPopularLocationType
                    else -> strDefaultValue
                }
                selectedFavorite =
                    if (Config.isRestaurantListOpeningFrom == Config.restaurantListOpeningFromFavoriteBottomSection)
                        "1" else "0"
                selectedPopularLocationId =
                    if (Config.isRestaurantListOpeningFrom == Config.restaurantListOpeningFromPopularLocationId)
                        PubFun.isNull(
                            Config.myRoomDatabase.daoConfig()
                                .selectConfigTableByField(Config.dbPopularLocationId), ""
                        )
                    else ""
                callServiceList(
                    selectedFoodCategoryName,
                    selectedType,
                    selectedFoodType,
                    selectedSortType,
                    selectedFavorite,
                    selectedPopularLocationId
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        try {
            when (v!!) {
                ivRestaurantListMenu -> {
                    ivRestaurantListLocationBottomBar.performClick()
                }
                cvRestaurantListFoodType -> populateFilterList(true)
                cvRestaurantListFoodSort -> populateFilterList(false)
                ivRestaurantListBack -> onBackPressed()
                lylHomeBottomBar -> {
                    Config.bottomBarClickedName = Config.homeBottomBarClicked
                    onBackPressed()
                }
                lylFavoriteBottomBar -> {
                    Config.bottomBarClickedName = Config.favoriteBottomBarClick
                    onBackPressed()
                }
                ivRestaurantListLocationBottomBar -> {
                    Config.bottomBarClickedName = Config.locationBottomBarClick
                    onBackPressed()
                }
                lylEventBottomBar -> {
                    Config.bottomBarClickedName = Config.eventBottomBarClick
                    onBackPressed()
                }
                lylMenuBottomBar -> {
                    Config.bottomBarClickedName = Config.menuBottomBarClick
                    onBackPressed()
                }
                llRestaurantListSearch -> {
                    val activityOptionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this@RestaurantListActivity,
                            llRestaurantListSearch,
                            getString(R.string.search)
                        )
                    val intent: Intent =
                        Intent(this@RestaurantListActivity, SearchActivity::class.java)
                    startActivity(intent, activityOptionsCompat.toBundle())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (PubFun.isInternetConnection(this@RestaurantListActivity)) {
            if (Config.bottomBarClickedName.isEmpty()) {
                if (Config.isRestaurantListOpeningFrom.isNotEmpty()) {
                    when (Config.isRestaurantListOpeningFrom) {
                        Config.restaurantListOpeningFromRecommendedSeeAll,
                        Config.restaurantListOpeningFromTrendingOfferSeeAll,
                        Config.restaurantListOpeningFromLiveDealSeeAll,
                        Config.restaurantListOpeningFromNearestSellAll,
                        Config.restaurantListOpeningFromPopularLocationSeeAll,
                        Config.restaurantListOpeningFromPopularLocationId -> {
                            Config.bottomBarClickedName = Config.homeBottomBarClicked
                            super.onBackPressed()
                            finish()
                        }
                        Config.restaurantListOpeningFromFavoriteBottomSection -> {
                            Config.isFavoriteMoveToBack = true
                            this.moveTaskToBack(
                                true
                            )
                        }
                    }
                } else if (Config.isPopularLocationListCallFromInside) {
                    Config.isPopularLocationListCallFromInside = false
                    Config.isRestaurantListOpeningFrom =
                        Config.restaurantListOpeningFromPopularLocationSeeAll
                    startActivity(
                        Intent(
                            this@RestaurantListActivity,
                            RestaurantListActivity::class.java
                        )
                    )
                    finish()
                }
            } else if (!Config.isFavoriteMoveToBack) {
                super.onBackPressed()
                finish()
            }
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }
    }

    private fun init() {
        try {
            //region Filling Food Category List
            for (i in newHomeRes.data!!.categories!![0].subcategory!!.indices) {
                val category = Category()
                category.id = newHomeRes.data!!.categories!![0].subcategory!![i].id
                category.name = newHomeRes.data!!.categories!![0].subcategory!![i].name

                if (Config.isRestaurantListOpeningFrom == Config.restaurantListOpeningFromHomeFragment) {
                    if (category.name!! == Config.subCategory0IndexRestaurantOldName) {
                        category.name = Config.subCategory0IndexRestaurantNewName
                    }
                }
                subCategoriesList.add(category)
            }
            if (subCategoriesList.isNotEmpty()) {
                foodCategoryAdapter =
                    HomeCategoriesAdapter(this@RestaurantListActivity, subCategoriesList) {
                        try {
                            if (PubFun.isInternetConnection(this@RestaurantListActivity)) {
                                if (Config.isRestaurantListOpeningFrom == Config.restaurantListOpeningFromHomeFragment) {
                                    selectedFoodCategoryName = if (it.name!!.trim()
                                            .lowercase(Locale.getDefault()) ==
                                        Config.subCategory0IndexRestaurantNewName.lowercase(Locale.getDefault())
                                    ) "" else it.name!!.trim()
                                } else {
                                    selectedFoodCategoryName = if (it.name!!.trim()
                                            .lowercase(Locale.getDefault()) ==
                                        Config.subCategory0IndexRestaurantOldName.lowercase(Locale.getDefault())
                                    ) "" else it.name!!.trim()
                                }
                                if (Config.isRestaurantListOpeningFrom == Config.restaurantListOpeningFromHomeFragment &&
                                    selectedFoodCategoryName.isEmpty()
                                ) {
                                    lylHomeBottomBar.performClick()
                                } else {
                                    callServiceList(
                                        selectedFoodCategoryName,
                                        selectedType,
                                        selectedFoodType,
                                        selectedSortType,
                                        selectedFavorite,
                                        selectedPopularLocationId
                                    )
                                }
                            } else {
                                foodCategoryAdapter.setPosition(Config.foodCategoryPreviousPosition)
                                showMsgDialogAndProceed(Config.msgToastForInternet)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                //region Food Categories RecyclerView
                rvRestaurantListCategory.apply {
                    layoutManager = LinearLayoutManager(
                        this@RestaurantListActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter = foodCategoryAdapter
                }
                //endregion

                /*Below logic is when user navigates from home fragment by choosing any food category other than "all"
                * it will show specific selection and scrolling position */
                if (Config.isRestaurantListOpeningFrom == Config.restaurantListOpeningFromHomeFragment) {
                    loop@ for (i in newHomeRes.data!!.categories!![0].subcategory!!.indices) {
                        if (Config.foodCategorySelectedId == newHomeRes.data!!.categories!![0].subcategory!![i].id.toString()) {
                            foodCategoryAdapter.setSelectionAndCallClickListener(i)
                            rvRestaurantListCategory.smoothScrollToPosition(i)
                            break@loop
                        }
                    }
                }
            }
            //endregion

            //region Restaurant List Details RecyclerView
            val restaurantListener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    try {
                        val restaurant = objects as Restaurant
                        Config.isMenuFragmentComingFrom = ""
                        if (PubFun.isInternetConnection(this@RestaurantListActivity)) {
                            startActivity(
                                Intent(
                                    this@RestaurantListActivity,
                                    VendorDetailActivity::class.java
                                )
                                    .putExtra("category", restaurant.categoryId.toString())
                                    .putExtra("serviceId", restaurant.id.toString())
                                    .putExtra("vendorTitle", restaurant.title)
                            )
                        } else {
                            showMsgDialogAndProceed(Config.msgToastForInternet)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            restaurantListAdapter =
                RestaurantListAdapter(this@RestaurantListActivity, restaurantListener)
            rvRestaurantListDetails.apply {
                layoutManager = LinearLayoutManager(this@RestaurantListActivity)
                adapter = restaurantListAdapter
            }
            //endregion

            //region Restaurant Favorite List Details RecyclerView
            //*** Entire View Click Listener ****
            val restaurantViewListener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    try {
                        if (PubFun.isInternetConnection(this@RestaurantListActivity)) {
                            val restaurant = objects as Restaurant
                            Config.isMenuFragmentComingFrom = ""
                            Config.isFavoriteBottomBarListClick = true
                            startActivity(
                                Intent(
                                    this@RestaurantListActivity,
                                    VendorDetailActivity::class.java
                                )
                                    .putExtra("category", restaurant.categoryId.toString())
                                    .putExtra("serviceId", restaurant.id.toString())
                                    .putExtra("vendorTitle", restaurant.title)
                            )
                        } else {
                            showMsgDialogAndProceed(Config.msgToastForInternet)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            //*** Favorite Click Listener ***
            val restaurantFavoriteListener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    val favorite = objects as Restaurant
                    if (PubFun.isInternetConnection(this@RestaurantListActivity)) {
                        homeViewModel.favourite_service(favorite.id!!.toString().trim())
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }
            }
            restaurantFavoriteListAdapter =
                RestaurantFavoriteListAdapter(restaurantViewListener, restaurantFavoriteListener)
            rvRestaurantFavoriteList.apply {
                layoutManager = LinearLayoutManager(this@RestaurantListActivity)
                adapter = restaurantFavoriteListAdapter
            }
            //endregion

            //region Progress Loader
            homeViewModel.isLoading.observe(this, {
                try {
                    if (it!!) {
                        Utils.showProgress(this@RestaurantListActivity)
                    } else {
                        Utils.hideProgress(this@RestaurantListActivity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            //endregion

            //region Progress Loader for Offer View Model
            offerViewModel.isLoading.observe(this, {
                try {
                    if (it!!) {
                        Utils.showProgress(this@RestaurantListActivity)
                    } else {
                        Utils.hideProgress(this@RestaurantListActivity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            //endregion

            //region Service List API Response
            homeViewModel.responseServices.observe(this, {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> {
                        when (Config.isRestaurantListOpeningFrom) {
                            Config.restaurantListOpeningFromPopularLocationSeeAll -> populatePopularLocationList(
                                it.data!!.popularLocations!!
                            )
                            Config.restaurantListOpeningFromTrendingOfferSeeAll -> populateTrendingOfferList(
                                it.data!!.offers!!
                            )
                            else -> {
                             //   Log.e("SEARCH",gson.toJson(it.data))
                                if (it.data!!.restaurant!!.isNotEmpty()) {
                                    when (Config.isRestaurantListOpeningFrom) {
                                        Config.restaurantListOpeningFromPopularLocationSeeAll -> rvRestaurantListPopularLocation.visibility =
                                            View.VISIBLE
                                        Config.restaurantListOpeningFromTrendingOfferSeeAll -> rvRestaurantListTrendingOffers.visibility =
                                            View.VISIBLE
                                        Config.restaurantListOpeningFromFavoriteBottomSection -> rvRestaurantFavoriteList.visibility =
                                            View.VISIBLE
                                        else -> rvRestaurantListDetails.visibility = View.VISIBLE
                                    }
                                    populateRestaurantList(it.data!!.restaurant!!)
                                } else {
                                    homeViewModel.isLoading.value = false
                                    when (Config.isRestaurantListOpeningFrom) {
                                        Config.restaurantListOpeningFromPopularLocationSeeAll -> rvRestaurantListPopularLocation.visibility =
                                            View.GONE
                                        Config.restaurantListOpeningFromTrendingOfferSeeAll -> rvRestaurantListTrendingOffers.visibility =
                                            View.GONE
                                        Config.restaurantListOpeningFromFavoriteBottomSection -> rvRestaurantFavoriteList.visibility =
                                            View.GONE
                                        else -> rvRestaurantListDetails.visibility = View.GONE
                                    }
                                }
                            }
                        }
                    }
                }
                //RecyclerView List Details
                rvRestaurantListDetails.visibility = if (it.data!!.restaurant!!.isNotEmpty()
                    && Config.isRestaurantListOpeningFrom != Config.restaurantListOpeningFromPopularLocationSeeAll
                )
                    View.VISIBLE else View.GONE

                //Food Type
                when (Config.isRestaurantListOpeningFrom) {
                    Config.restaurantListOpeningFromPopularLocationSeeAll -> {
                        cvRestaurantListFoodType.visibility =
                            if (it.data!!.restaurant!!.isNotEmpty() &&
                                Config.isRestaurantListOpeningFrom != Config.restaurantListOpeningFromPopularLocationSeeAll
                            )
                                View.VISIBLE else View.GONE
                    }
                    Config.restaurantListOpeningFromFavoriteBottomSection -> {
                        cvRestaurantListFoodType.visibility =
                            if (it.data!!.restaurant!!.isNotEmpty() &&
                                Config.isRestaurantListOpeningFrom != Config.restaurantListOpeningFromFavoriteBottomSection
                            )
                                View.VISIBLE else View.GONE
                    }
                }


                //Food Sort
                when (Config.isRestaurantListOpeningFrom) {
                    Config.restaurantListOpeningFromPopularLocationSeeAll -> {
                        cvRestaurantListFoodSort.visibility =
                            if (it.data!!.restaurant!!.isNotEmpty() &&
                                Config.isRestaurantListOpeningFrom != Config.restaurantListOpeningFromPopularLocationSeeAll
                            )
                                View.VISIBLE else View.GONE
                    }
                    Config.restaurantListOpeningFromFavoriteBottomSection -> {
                        cvRestaurantListFoodSort.visibility =
                            if (it.data!!.restaurant!!.isNotEmpty() &&
                                Config.isRestaurantListOpeningFrom != Config.restaurantListOpeningFromFavoriteBottomSection
                            )
                                View.VISIBLE else View.GONE
                    }
                }
            })
            //endregion

            //region Favorite Service API Response
            homeViewModel.responseFavoriteService.observe(this, {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    //Call Service List API without any filters
                    1 -> callServiceList(
                        "", selectedType, "", "", selectedFavorite,
                        selectedPopularLocationId
                    )
                }
            })
            //endregion
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            Utils.hideProgress(this@RestaurantListActivity)
            val myDialog = DialogToast(this@RestaurantListActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Restaurant List"
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
                                postDelayed(this, 1000)
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

    private fun callServiceList(
        subCategoryName: String,
        type: String,
        foodType: String,
        sortType: String,
        favorite: String,
        popularLocationId: String
    ) {
        Log.e("CategoryName",subCategoryName)
        Log.e("type",type)
        Log.e("foodType",foodType)
        Log.e("sortType",sortType)
        Log.e("favorite",favorite)
        Log.e("popularLocationId",popularLocationId)

        try {
            if (PubFun.isInternetConnection(this@RestaurantListActivity)) {
                homeViewModel.service_list(
                    "1",
                    subCategoryName,
                    type,
                    foodType,
                    sortType,
                    favorite,
                    popularLocationId
                )
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            homeViewModel.isLoading.value = false
            e.printStackTrace()
        }
    }

    private fun populatePopularLocationList(popularLocationList: List<PopularLocation>) {
        try {
            //region Restaurant Popular Location RecyclerView
            val popularLocationListener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    val data = objects as PopularLocation
                    Config.isPopularLocationListCallFromInside = true
                    selectedPopularLocationId = data.id!!.toString().trim()
                    Config.isRestaurantListOpeningFrom = ""
                    callServiceListForPopularLocationId(data.address!!.trim())
                }
            }
            val popularLocationAdapter = PopularLocationListAdapter(
                this@RestaurantListActivity,
                popularLocationList,
                popularLocationListener
            )
            rvRestaurantListPopularLocation.apply {
                layoutManager = LinearLayoutManager(this@RestaurantListActivity)
                adapter = popularLocationAdapter
            }
            //endregion
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callServiceListForPopularLocationId(locationTitleName: String) {
        try {
            ivRestaurantListMenu.visibility = View.GONE
            lylRestaurantListToolbar.visibility = View.VISIBLE
            ivMapLocation.visibility = View.GONE
            tvRestaurantListLocation.visibility = View.GONE
            frameRestaurantListNotification.visibility = View.GONE
            llRestaurantListSearch.visibility = View.GONE
            rvRestaurantListDetails.visibility = View.VISIBLE
            rvRestaurantListPopularLocation.visibility = View.GONE
            rvRestaurantListTrendingOffers.visibility = View.GONE
            rvRestaurantFavoriteList.visibility = View.GONE
            cvRestaurantListFoodType.visibility = View.VISIBLE
            cvRestaurantListFoodSort.visibility = View.VISIBLE
            tvRestaurantListTitle.text = locationTitleName
            ivRestaurantListBack.visibility = View.VISIBLE
            lylRestaurantListBottomBar.visibility = View.GONE
            ivRestaurantListLocationBottomBar.visibility = View.GONE

            callServiceList(
                selectedFoodCategoryName,
                selectedType,
                selectedFoodType,
                selectedSortType,
                selectedFavorite,
                selectedPopularLocationId
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callOfferFavoriteCoupon(offerId: String) {
        try {
            if (PubFun.isInternetConnection(this@RestaurantListActivity)) {
                offerViewModel.offerFavoriteCoupon(offerId)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateTrendingOfferList(offersList: List<Offer>) {
        var favoritePosition = 0
        val favoriteListener = object : ListClickListener {
            override fun onClickListener(view: View, pos: Int, objects: Any) {
                try {
                    val offerData = objects as Offer
                    favoritePosition = pos
                    callOfferFavoriteCoupon(offerData.id!!.toString().trim())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        //region Restaurant Trending Offers RecyclerView
        val rowListener = object : ListClickListener {
            override fun onClickListener(view: View, pos: Int, objects: Any) {
                if (PubFun.isInternetConnection(this@RestaurantListActivity)) {
                    startActivity(
                        Intent(
                            this@RestaurantListActivity,
                            CouponDetailActivity::class.java
                        )
                    )
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
        }
        val trendingOfferListAdapter =
            TrendingOfferListAdapter(
                this@RestaurantListActivity,
                offersList,
                true,
                rowListener,
                favoriteListener,
                "",
                0,
                "",
                ""
            ) {
                if (PubFun.isInternetConnection(this@RestaurantListActivity)) {
                    homeViewModel.activate_coupon(it.id.toString())
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
        rvRestaurantListTrendingOffers.apply {
            layoutManager = LinearLayoutManager(this@RestaurantListActivity)
            adapter = trendingOfferListAdapter
        }
        //endregion

        //region Offer Favorite Response
        offerViewModel.responseFavoriteCoupon.observe(this@RestaurantListActivity, {
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
    }

    private fun populateRestaurantList(restaurantList: List<Restaurant>) {
        try {
            if (Config.isRestaurantListOpeningFrom == Config.restaurantListOpeningFromLiveDealSeeAll) {
                val liveDealRestaurantList = ArrayList<Restaurant>()
                for (m in restaurantList.indices) {
                    if (restaurantList[m].isLiveDeal!!) {
                        liveDealRestaurantList.add(restaurantList[m])
                    }
                }
                if (liveDealRestaurantList.isNotEmpty() &&
                    Config.isRestaurantListOpeningFrom != Config.restaurantListOpeningFromPopularLocationSeeAll
                ) {
                    restaurantListAdapter.setRestaurantList(liveDealRestaurantList)

                    //RecyclerView List Details
                    rvRestaurantListDetails.visibility = View.VISIBLE

                    //Food Type
                    cvRestaurantListFoodType.visibility = View.VISIBLE

                    //Food Sort
                    cvRestaurantListFoodSort.visibility = View.VISIBLE
                } else {
                    //RecyclerView List Details
                    rvRestaurantListDetails.visibility = View.GONE

                    //Food Type
                    cvRestaurantListFoodType.visibility = View.GONE

                    //Food Sort
                    cvRestaurantListFoodSort.visibility = View.GONE
                }
            } else if (Config.isRestaurantListOpeningFrom == Config.restaurantListOpeningFromFavoriteBottomSection) {
                restaurantFavoriteListAdapter.setRestaurantFavoriteList(restaurantList as ArrayList<Restaurant>)
            } else {
                restaurantListAdapter.setRestaurantList(restaurantList as ArrayList<Restaurant>)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            homeViewModel.isLoading.value = false
        }
    }

    private fun populateFilterList(isFoodType: Boolean) {
        try {
            lylRestaurantListFoodType.visibility = if (isFoodType) View.VISIBLE else View.GONE
            lylRestaurantListFoodSort.visibility = if (isFoodType) View.GONE else View.VISIBLE

            val arrayListItems = ArrayList<clsPopupDialogList>()
            if (isFoodType) {
                arrayListItems.add(clsPopupDialogList(strAll, ""))
                arrayListItems.add(clsPopupDialogList(strVeg, ""))
                arrayListItems.add(clsPopupDialogList(strNonVeg, ""))
                arrayListItems.add(clsPopupDialogList(strEgg, ""))
            } else {
                arrayListItems.add(clsPopupDialogList(strAll, ""))
                if (Config.isRestaurantListOpeningFrom != Config.restaurantListOpeningFromRecommendedSeeAll) {
                    arrayListItems.add(clsPopupDialogList(strRecommendedByTMT, ""))
                }
                arrayListItems.add(clsPopupDialogList(strRatingHighToLow, ""))
                arrayListItems.add(clsPopupDialogList(strMostFavorite, ""))
//                if (Config.isRestaurantListOpeningFrom != Config.restaurantListOpeningFromNearestSellAll) {
//                    arrayListItems.add(clsPopupDialogList(strNearBy, ""))
//                }
                arrayListItems.add(clsPopupDialogList(strAscending, ""))
            }

            val listener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    try {
                        val popupDialogList = objects as clsPopupDialogList
                        if (isFoodType) {
                            tvRestaurantListFoodType.text = popupDialogList.strTitle
                            lylRestaurantListFoodType.visibility = View.GONE
                            selectedFoodType =
                                when (popupDialogList.strTitle) {
                                    strVeg -> strVegValue
                                    strNonVeg -> strNonVegValue
                                    strEgg -> strEggValue
                                    else -> strDefaultValue
                                }
                        } else {
                            tvRestaurantListSortType.text = popupDialogList.strTitle
                            lylRestaurantListFoodSort.visibility = View.GONE
                            selectedSortType = when (popupDialogList.strTitle) {
                                strRecommendedByTMT -> strRecommendedByTMTValue
                                strRatingHighToLow -> strRatingHighToLowValue
                                strMostFavorite -> strMostFavoriteValue
                                strNearBy -> strNearByValue
                                strAscending -> strAscendingValue
                                else -> strDefaultValue
                            }
                        }
                        callServiceList(
                            selectedFoodCategoryName,
                            selectedType,
                            selectedFoodType,
                            selectedSortType,
                            selectedFavorite,
                            selectedPopularLocationId
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val adapterFilterList = AdapterDialogPopupList(listener)
            adapterFilterList.addPopupDataItems(arrayListItems)
            if (isFoodType) {
                rvRestaurantListFoodType.apply {
                    adapter = adapterFilterList
                    layoutManager = LinearLayoutManager(this@RestaurantListActivity)
                }
            } else {
                rvRestaurantListFoodSort.apply {
                    adapter = adapterFilterList
                    layoutManager = LinearLayoutManager(this@RestaurantListActivity)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}