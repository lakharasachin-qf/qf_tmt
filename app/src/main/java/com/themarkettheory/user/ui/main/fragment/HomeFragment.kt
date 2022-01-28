package com.themarkettheory.user.ui.main.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.Constants
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.OnCategoryClickListener
import com.themarkettheory.user.newmodels.getcategories.Category
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.activity.CitySelectionActivity
import com.themarkettheory.user.ui.main.activity.FeedbackActivity
import com.themarkettheory.user.ui.main.activity.NotificationActivity
import com.themarkettheory.user.ui.main.activity.SearchActivity
import com.themarkettheory.user.ui.main.adapter.HomeCategoriesAdapter
import com.themarkettheory.user.ui.restaurant.MyBucketActivity
import com.themarkettheory.user.ui.restaurant.RestaurantFragment
import com.themarkettheory.user.ui.restaurant.restaurant_list.RestaurantListActivity
import com.themarkettheory.user.viewmodel.GeneralViewModel
import com.themarkettheory.user.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*

@AndroidEntryPoint
class HomeFragment : BaseFragment(), View.OnClickListener, OnCategoryClickListener {
    private lateinit var homeCategoriesAdapter: HomeCategoriesAdapter

    var relativeLayoutArray = arrayOf<RelativeLayout>()
    var imageViewArray = arrayOf<ImageView>()
    var textviewArray = arrayOf<TextView>()
    var isMapView = false
    var selectedCategoryId = ""
    lateinit var homeViewModel: HomeViewModel
    lateinit var generalViewModel: GeneralViewModel

    //  lateinit var cartViewModel: CartViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        generalViewModel = ViewModelProvider(requireActivity()).get(GeneralViewModel::class.java)
        // cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        relativeLayoutArray = arrayOf(
            rlAll, rlRestaurant, rlSalon,
            rlSpa, rlRetail, rlBakery, rlGym
        )
        imageViewArray = arrayOf(
            ivAll, ivRestaurant, ivSalon,
            ivSpa, ivRetail, ivBakery, ivGym
        )
        textviewArray = arrayOf(
            tvAll, tvRestaurant, tvSalon,
            tvSpa, tvRetail, tvBakery, tvGym
        )
        /*tvAddress.setText(Utils.getAddress(prefs.getLoginModel()))*/

        tvAddress.text =
            myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbCurrentCitySelection)
        /*tvAddress.text = "Ahmebabad, Gujarat"*/
        /*if(param1?.selectedCategory == "all") {
            setFragment(AllFragment())
        }else{
            isMapView = true
            updateUI(rlRestaurant,tvRestaurant,ivRestaurant,Constants.RESTAURANT)
        }*/

        Config.currentLat = myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbLat)!!
        Config.currentLng = myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbLng)!!

        getHomeResponse()

        Utils.category.observe(viewLifecycleOwner, Observer<String> {
            setFragment(RestaurantFragment.newInstance(it, ""))
            if (::homeCategoriesAdapter.isInitialized) {
                homeCategoriesAdapter.setPosition(it.toInt())
            }
            if (it == "0") {
                /*ivMap.visibility = View.GONE*/
            } else
                selectedCategoryId = it.toString()
        })

        llAll.setOnClickListener(this)
        llRestaurant.setOnClickListener(this)
        llSalon.setOnClickListener(this)
        llSpa.setOnClickListener(this)
        llRetail.setOnClickListener(this)
        llBakery.setOnClickListener(this)
        llGym.setOnClickListener(this)
        llSearch.setOnClickListener(this)
        llSearches.setOnClickListener(this)
        ivNotification.setOnClickListener(this)
        tvAddress.setOnClickListener(this)
        ivMap.setOnClickListener(this)
        ivLocation.setOnClickListener(this)
        coCart.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        try {
            if (PubFun.isInternetConnection(requireActivity())) {
                homeViewModel.home(Config.currentLat, Config.currentLng)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getHomeResponse() {

        homeViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it!!) {
                Utils.showProgress(requireActivity())
            } else {
                Utils.hideProgress(requireActivity())
            }
        })

        homeViewModel.responseHome.observe(viewLifecycleOwner, Observer { home ->
            when (home.status!!) {
                0 -> showMsgDialogAndProceed(home.message!!.trim())
                1 -> {

                    // setting up the cart size
                    tvCartCount.visibility =
                        if (home.data!!.cartCount!!.toString().trim().toInt() > 0
                        ) View.VISIBLE else View.GONE
                    tvCartCount.text = home.data!!.cartCount!!.toString().trim()
                    if (home.isFeedBack == 1) {
                        Log.e("isFEEd","Count")
                        startActivity(
                            Intent(
                                requireActivity(),
                                FeedbackActivity::class.java
                            ).putExtra("serviceId", home.serviceId)
                        )
                    }

                    home.data?.let {
                        myRoomDatabase.daoConfig().deleteConfigTableByField(Config.dbNewHomeRes)
                        myRoomDatabase.daoConfig().insertConfigTable(
                            TableConfig(
                                Config.dbNewHomeRes,
                                gson.toJson(home)
                            )
                        )
                        /*setFragment(AllFragment.newInstance(home.newHomeData, ""))*/
                        setFragment(AllFragment())
                        val subCategories = ArrayList<Category>()
                        for (i in it.categories!![0].subcategory!!.indices) {
                            val category = Category()
                            category.id = it.categories!![0].subcategory!![i].id
                            category.name = it.categories!![0].subcategory!![i].name

                            if (category.name!! == Config.subCategory0IndexRestaurantOldName) {
                                category.name = Config.subCategory0IndexRestaurantNewName
                            }
                            subCategories.add(category)
                        }

                        homeCategoriesAdapter = HomeCategoriesAdapter(
                            requireActivity(),
                            subCategories
                        ) {
                            if (PubFun.isInternetConnection(requireActivity())) {
                                if (selectedCategoryId != it.id.toString()) {
                                    selectedCategoryId = it.id.toString()
                                    if (it.name!!.lowercase() == Config.subCategory0IndexRestaurantNewName) {
                                        /*setFragment(AllFragment.newInstance(home.newHomeData, ""))*/
                                        setFragment(AllFragment())
                                        /*ivMap.visibility = View.GONE*/
                                    } else {
                                        /*ivMap.visibility = View.VISIBLE*/
                                        /*setFragment(
                                            RestaurantFragment.newInstance(
                                                it.id.toString(),
                                                ""
                                            )
                                        )*/
                                        Config.foodCategorySelectedId = selectedCategoryId
                                        Config.isRestaurantListOpeningFrom =
                                            Config.restaurantListOpeningFromHomeFragment
                                        startActivity(
                                            Intent(
                                                requireActivity(),
                                                RestaurantListActivity::class.java
                                            )
                                        )
                                    }
                                }
                            } else {
                                homeCategoriesAdapter.setPosition(Config.foodCategoryPreviousPosition)
                                showMsgDialogAndProceed(Config.msgToastForInternet)
                            }
                        }
                        rvHomeCategory.apply {
                            adapter = homeCategoriesAdapter
                            layoutManager =
                                LinearLayoutManager(
                                    requireActivity(),
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                        }
                    }
                }
            }
        })

        /* generalViewModel.responseGetCart.observe(viewLifecycleOwner, Observer {
             if (it.status!!) {
                 if (it.data?.list.isNullOrEmpty()) {
                     tvCartCount.visibility = View.GONE
                 } else {
                     tvCartCount.visibility = View.VISIBLE
                     tvCartCount.setText(it.data?.list!!.size.toString())
                 }
             }
         })*/

        /*generalViewModel.responseGetProfile.observe(viewLifecycleOwner, Observer {
            if (it.status!! == 1) {
                *//*if(it.data?.address?.houseNumber.isNullOrEmpty() &&it.data?.address?.tower.isNullOrEmpty()){
                    tvAddress.setText(Utils.getAddress(it.data!!))
                }*//*
                *//*tvAddress.setText(Utils.getAddress(it.data!!))*//*
            }
        })*/
    }

    override fun onClick(v: View?) {
        when (v) {


            ivNotification -> {
                // startActivity(Intent(requireActivity(), FeedbackActivity::class.java))
                if (PubFun.isInternetConnection(requireActivity())) {
                    startActivity(Intent(requireActivity(), NotificationActivity::class.java))
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }


            llAll -> {
                updateUI(rlAll, tvAll, ivAll, "")
                setFragment(AllFragment())
                /*ivMap.visibility = View.GONE*/
            }

            llRestaurant -> {
                updateUI(rlRestaurant, tvRestaurant, ivRestaurant, Constants.RESTAURANT)
                setFragment(RestaurantFragment.newInstance("1", ""))
                if (::homeCategoriesAdapter.isInitialized) {
                    homeCategoriesAdapter.setPosition(1)
                }
            }

            tvAddress, ivLocation -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    startActivity(Intent(requireActivity(), CitySelectionActivity::class.java))
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
                /* startActivity(Intent(requireActivity(), MyAddressActivity::class.java))*/
            }

            coCart -> {
                if (tvCartCount.visibility == View.VISIBLE) {
                    Config.isHomeGoCartClicked = true
                    Config.isMyPointClickedFromHome = true
                    if (PubFun.isInternetConnection(requireActivity())) {
                        startActivity(Intent(requireActivity(), MyBucketActivity::class.java))
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                } else {
                    showMsgDialogAndProceed("There is no items in your cart")
                }
            }

            llSalon -> {
                updateUI(rlSalon, tvSalon, ivSalon, Constants.SALON)
                setFragment(RestaurantFragment.newInstance("4", ""))
                if (::homeCategoriesAdapter.isInitialized) {
                    homeCategoriesAdapter.setPosition(4)
                }
            }

            llSpa -> {
                updateUI(rlSpa, tvSpa, ivSpa, Constants.SPA)
                setFragment(RestaurantFragment.newInstance("5", ""))
                if (::homeCategoriesAdapter.isInitialized) {
                    homeCategoriesAdapter.setPosition(5)
                }
            }

            llRetail -> {
                updateUI(rlRetail, tvRetail, ivRetail, Constants.RETAIL)
                setFragment(RestaurantFragment.newInstance("3", ""))
                if (::homeCategoriesAdapter.isInitialized) {
                    homeCategoriesAdapter.setPosition(3)
                }
            }

            llBakery -> {
                updateUI(rlBakery, tvBakery, ivBakery, Constants.BAKERY)
                setFragment(RestaurantFragment.newInstance("6", ""))
                if (::homeCategoriesAdapter.isInitialized) {
                    homeCategoriesAdapter.setPosition(6)
                }
            }

            llGym -> {
                updateUI(rlGym, tvGym, ivGym, Constants.GYM)
                setFragment(RestaurantFragment.newInstance("2", ""))
                if (::homeCategoriesAdapter.isInitialized) {
                    homeCategoriesAdapter.setPosition(2)
                }
            }

            ivMap -> {
                if (!isMapView) {
//                    setFragment(MapFragment())
                    setMapFragment()
//                    MapFragment.newInstance(selectedCategoryId, "")
                    ivMap.setImageResource(R.drawable.ic_list)
                } else {
                    setFragment(RestaurantFragment.newInstance("", ""))
                    ivMap.setImageResource(R.drawable.ic_map)
                }
                isMapView = !isMapView
            }

            llSearch -> {
                val activityOptionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        llSearch,
                        getString(R.string.search)
                    )
                val intent: Intent = Intent(activity, SearchActivity::class.java)
                startActivity(intent, activityOptionsCompat.toBundle())
            }
            llSearches -> {
                val activityOptionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        llSearch,
                        getString(R.string.search)
                    )
                val intent: Intent = Intent(activity, SearchActivity::class.java)
                startActivity(intent, activityOptionsCompat.toBundle())
            }

        }
    }

    fun updateUI(rl: RelativeLayout, tv: TextView, iv: ImageView, category: String) {
        /*ivMap.visibility = View.VISIBLE*/
        relativeLayoutArray.map {
            it.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_menu_bg)
        }

        textviewArray.map {
            it.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
        }

        imageViewArray.map {
            it.setColorFilter(
                ContextCompat.getColor(requireActivity(), R.color.gull_gray),
                android.graphics.PorterDuff.Mode.MULTIPLY
            );
        }
        rl.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_menu_selected_bg)
        tv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.robins_egg_blue))
        iv.setColorFilter(Color.argb(255, 255, 255, 255))

        /*if (category.isNotEmpty()) {
            if (isMapView) {
//                setMapFragment()
                setMapFragment()
                ivMap.setImageResource(R.drawable.ic_list)
            } else {
                setFragment(RestaurantFragment.newInstance(category, ""))
                ivMap.setImageResource(R.drawable.ic_map)
            }
        }*/
    }

    private fun setFragment(fragment: Fragment) {
        frameLayoutHome.visibility = View.VISIBLE
        frameLayoutMap.visibility = View.GONE
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayoutHome, fragment)
            .commit()
    }

    private fun setMapFragment() {
        /*frameLayoutHome.visibility = View.GONE
        frameLayoutMap.visibility = View.VISIBLE
        requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayoutMap, MapFragment.newInstance(selectedCategoryId, ""))
                .commit()*/
    }


    fun setUI(category: String) {
        when (category) {
            Constants.RESTAURANT -> {
                updateUI(rlRestaurant, tvRestaurant, ivRestaurant, Constants.RESTAURANT)
            }

            Constants.SALON -> {
                updateUI(rlSalon, tvSalon, ivSalon, Constants.SALON)
            }

            Constants.GYM -> {
                updateUI(rlGym, tvGym, ivGym, Constants.GYM)
            }

            Constants.SPA -> {
                updateUI(rlSpa, tvSpa, ivSpa, Constants.SPA)
            }

            Constants.BAKERY -> {
                updateUI(rlBakery, tvBakery, ivBakery, Constants.BAKERY)
            }

            Constants.RETAIL -> {
                updateUI(rlRetail, tvRetail, ivRetail, Constants.RETAIL)
            }
        }
    }

    override fun onCategoryClick(category: String) {
        if (::homeCategoriesAdapter.isInitialized) {
            homeCategoriesAdapter.setPosition(category.toInt())
        }
        setFragment(RestaurantFragment.newInstance(category, ""))
        selectedCategoryId = category
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
}