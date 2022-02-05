package com.themarkettheory.user.ui.restaurant.restaurant_detail


import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.gson.GsonBuilder
import com.themarkettheory.user.R
import com.themarkettheory.user.database.MyRoomDatabase
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.home.Offer
import com.themarkettheory.user.newmodels.overview.Data
import com.themarkettheory.user.newmodels.overview.Image
import com.themarkettheory.user.ui.coupon.CouponActivity
import com.themarkettheory.user.ui.coupon.CouponDetailActivity
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.activity.AddReportActivity
import com.themarkettheory.user.ui.main.activity.MyPointsActivity
import com.themarkettheory.user.ui.main.activity.SeeAllPhotosActivity
import com.themarkettheory.user.ui.main.adapter.CrossPromotionListAdapter
import com.themarkettheory.user.ui.main.adapter.TrendingOfferListAdapter
import com.themarkettheory.user.ui.main.adapter.VendorPhotoListAdapter
import com.themarkettheory.user.ui.restaurant.LiveDealsActivity
import com.themarkettheory.user.ui.restaurant.RestaurantDescriptionActivity
import com.themarkettheory.user.viewmodel.GeneralViewModel
import com.themarkettheory.user.viewmodel.OfferViewModel
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.fragment_overview.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OverviewFragment : Fragment(), View.OnClickListener {
    private var is_liked: Int? = 0

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var vendorDetailViewModel: VendorDetailViewModel
    lateinit var generalViewModel: GeneralViewModel
    lateinit var offerViewModel: OfferViewModel
    var category: String? = ""
    var serviceId: String? = ""
    var vendorTitle: String? = ""
    var phone: String? = ""
    var email: String? = ""
    var data = ArrayList<Image>()
    var serviceDetail: Data? = null
    var title: String? = ""

    /*Database and Gson*/
    val myRoomDatabase = MyRoomDatabase.getDB(Config.mainActivity!!)!!
    val gson = GsonBuilder().serializeNulls().create()

    //To Get Current location
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mCurrentLat: Double = 0.0
    private var mCurrentLng: Double = 0.0
    private val PERMISSION_CALLBACK_CONSTANT = 100
    var allGranted = false

    //Adapter
    private lateinit var trendingOfferListAdapter: TrendingOfferListAdapter
    private var isFavoritePosition = 0

    private var isFirstTimeLoading = true


    private var runnableAutoScroll: Runnable? = null
    private var handlerAutoScroll: Handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        vendorDetailViewModel = ViewModelProvider(this).get(VendorDetailViewModel::class.java)
        generalViewModel = ViewModelProvider(requireActivity()).get(GeneralViewModel::class.java)
        offerViewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onResume() {
        super.onResume()
        try {
            // calling api for service detail
            if (PubFun.isInternetConnection(requireActivity())) {
                vendorDetailViewModel.service_details(serviceId)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startAutoSlider(count: Int) {
        try {
            var pos: Int = viewpager.currentItem
            runnableAutoScroll = object : Runnable {
                override fun run() {
                    pos += 1
                    if (pos >= count) pos = 0
                    viewpager.currentItem = pos
                    handlerAutoScroll.postDelayed(this, 3000)
                }
            }
            handlerAutoScroll.postDelayed(runnableAutoScroll!!, 3000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            category = arguments?.getString("category")
            serviceId = arguments?.getString("serviceId")
            vendorTitle = arguments?.getString("vendorTitle")
        }

        myRoomDatabase.daoConfig().apply {
            deleteConfigTableByField(Config.dbServiceId)
            insertConfigTable(
                TableConfig(
                    Config.dbServiceId,
                    serviceId!!.trim()
                )
            )
        }

        vendorDetailViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it!!) {
                Utils.showProgress(requireActivity())
            } else {
                Utils.hideProgress(requireActivity())
            }
        })

        vendorDetailViewModel.responseServiceDetail.observe(viewLifecycleOwner, Observer {
            when (it.status!!) {
                0 -> showMsgDialogAndProceed(it.message.toString())
                1 -> {
                    llOverview.visibility = View.VISIBLE
                    data.clear()
                    data.addAll(it.data?.image!!)
                    serviceDetail = it.data!!
                    Config.serviceDetail = it.data!!
                    Config.overviewImage = it.data?.image!! as ArrayList<Image>

                    email = it.data?.email
                    phone = it.data?.mobile

                    if (it.data?.mobile.isNullOrEmpty()) {
                        llPhone.visibility = View.GONE
                    } else {
                        llPhone.setText(phone)
                    }
                    if (it.data?.email.isNullOrEmpty()) {
                        llEmail.visibility = View.GONE
                    } else {
                        llEmail.setText(email)
                    }

                    if (it.data?.isOpen == 1) {
                        tvOpenHour.setText(getString(R.string.open))
                        tvOpenHour.setTextColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.malachite
                            )
                        );
                    } else {
                        tvOpenHour.setText(getString(R.string.close).uppercase())
                        tvOpenHour.setTextColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.cinnabar
                            )
                        );
                    }

                    /* tvPhone.setText(it.data?.phone)
                     tvEmail.setText(it.data?.email)*/
                    title = it.data?.title
                    tvTitle.text = title
                    tvTotalRatings.text = "(" + it.data?.totalRating + ")"
                    rating.rating = it.data?.rating!!.toFloat()
                    tvTotalFavorites.text = it.data?.overviewfavourite.toString()
                    tvAddress.text = it.data?.address
                    tvDistance.text = it.data?.distance
                    tvPoints.setText(it.data?.points.toString())
//                tvDescr.setTextMaxLength(80)
//                tvDescr.toggle()
//                tvDescr.setSeeMoreText(getString(R.string.see_more),getString(R.string.see_less))
//                tvDescr.setContent(it.data?.description)
                    if (it.data?.isLiveDeal == true) {
                        btnLiveDeals.visibility = View.VISIBLE
                    } else {
                        btnLiveDeals.visibility = View.INVISIBLE
                    }
                    is_liked = it.data?.isLiked

                    tvOverviewDescription.visibility =
                        if (it.data!!.description!!.isEmpty()) View.GONE else View.VISIBLE
                    tvDescr.visibility =
                        if (it.data!!.description!!.isEmpty()) View.GONE else View.VISIBLE

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        tvDescr.text = Html.fromHtml(
                            getString(
                                R.string.restaurant_desc_all,
                                it.data?.description
                            ),
                            Html.FROM_HTML_MODE_COMPACT
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        tvDescr.text = Html.fromHtml(
                            getString(
                                R.string.restaurant_desc_all,
                                it.data?.description
                            )
                        )
                    }
                    if (it.data?.isLiked == 1) {
                        ivFavorite.setImageResource(R.drawable.ic_like_selected)
                    } else {
                        ivFavorite.setImageResource(R.drawable.ic_like_unselected)
                    }

                    val favoriteListener = object : ListClickListener {
                        override fun onClickListener(view: View, pos: Int, objects: Any) {
                            try {
                                val offerData = objects as Offer
                                isFavoritePosition = pos
                                callOfferFavoriteCoupon(offerData.id!!.toString().trim())
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
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
                    trendingOfferListAdapter =
                        TrendingOfferListAdapter(
                            requireActivity(),
                            it.data!!.offers!!,
                            true,
                            rowListener,
                            favoriteListener,
                            Config.isCouponDetailOpeningFromOverview,
                            it.data!!.id!!,
                            it.data!!.title!!.trim(),
                            it.data!!.address!!.trim()
                        ) {
                            if (PubFun.isInternetConnection(requireActivity())) {
                                offerViewModel.activate_coupon(it.id.toString())
                            } else {
                                showMsgDialogAndProceed(Config.msgToastForInternet)
                            }
                        }
                    rvDiscountedCoupons.adapter = trendingOfferListAdapter
                    rvDiscountedCoupons.layoutManager = LinearLayoutManager(
                        requireActivity(),
                        LinearLayoutManager.HORIZONTAL, false
                    )
                    tvOverviewDiscountCoupon.visibility =
                        if (it.data!!.offers!!.isEmpty()) View.GONE else View.VISIBLE
                    tvSeeAll.visibility =
                        if (it.data!!.offers!!.isEmpty()) View.GONE else View.VISIBLE

                    ivVegTag.visibility = if (it.data?.isVeg == 1) View.VISIBLE else View.GONE
                    ivVegNovegTag.visibility =
                        if (it.data?.isNonVeg == 1) View.VISIBLE else View.GONE
                    ivEggTag.visibility = if (it.data?.isEgg == 1) View.VISIBLE else View.GONE

                    if (!it.data?.cuisineTypes.isNullOrEmpty()) {
                        var stringBuilder = StringBuilder()
                        for (element in it.data?.cuisineTypes!!) {
                            stringBuilder.append(element.name).append(" • ")
                        }
                        val cuisineTypes: String = stringBuilder.toString()
                            .substring(0, stringBuilder.toString().length - 3)
                        tvTags.setText(cuisineTypes)
                    }

                    /*val tagsAdapter = VendorTagsAdapter(requireActivity(),it.data?.tags!!)
                      rvVendorTags.adapter = tagsAdapter
                      rvVendorTags.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)*/
                    val images = ArrayList<Image>()
                    images.addAll(it.data?.image!!)
//                   images.sortBy { it.position}
                    if (images.isNotEmpty()) { // Condition added by Milan
                        val vendorPhotoListAdapter =
                            VendorPhotoListAdapter(requireActivity(), true, images, false)
                        viewpager.adapter = vendorPhotoListAdapter
                        indicator.setViewPager(viewpager)
                        if (Config.isVideoPlay) {
                            if (Config.isMenuFragmentComingFrom != Config.isMenuFragmentComingFromBucketCart ||
                                Config.isMenuFragmentComingFrom == Config.isMenuFragmentComingFromBookingTable
                            ) {
                                if (images[0].videoUrl!!.isNotEmpty() && isFirstTimeLoading) {
                                    isFirstTimeLoading = false
                                    vendorPhotoListAdapter.playVideo()
                                }
                                if (runnableAutoScroll != null) {
                                    handlerAutoScroll.removeCallbacks(runnableAutoScroll!!)
                                    handlerAutoScroll.removeCallbacksAndMessages(null)
                                }
                                startAutoSlider(images.size) // newly added code harish
                            }
                        } else {
                            Config.isVideoPlay = false
                            if (runnableAutoScroll != null) {
                                handlerAutoScroll.removeCallbacks(runnableAutoScroll!!)
                                handlerAutoScroll.removeCallbacksAndMessages(null)
                            }
                            startAutoSlider(images.size) // newly added code harish
                        }
                    }

                    /* if(it.data?.amenities.isNullOrEmpty()){
                         tvAmenities.visibility = View.GONE
                         rvAmenities.visibility = View.GONE
                     }else{
                         val amenitiesAdapter = AmenitiesAdapter(requireActivity(),it.data.amenities!!)
                         rvAmenities.adapter = amenitiesAdapter
                         rvAmenities.layoutManager = GridLayoutManager(activity,4)
                     }*/

                    if (it.data?.crossPromotions.isNullOrEmpty()) {
                        tvCrossPromotion.visibility = View.GONE
                        rvCrossPromotionPartners.visibility = View.GONE
                    } else {
                        tvCrossPromotion.visibility = View.VISIBLE
                        rvCrossPromotionPartners.visibility = View.VISIBLE

                        val crossPromotionListAdapter =
                            CrossPromotionListAdapter(requireActivity(), it.data?.crossPromotions!!)
                        rvCrossPromotionPartners.adapter = crossPromotionListAdapter
                        rvCrossPromotionPartners.layoutManager = LinearLayoutManager(
                            requireActivity(),
                            LinearLayoutManager.HORIZONTAL, false
                        )
                    }
                }
            }
        })

        offerViewModel.isLoadingActivateCoupon.observe(viewLifecycleOwner, Observer {
            if (it!!) {
                Utils.showProgress(requireActivity())
            } else {
                Utils.hideProgress(requireActivity())
            }
        })

        offerViewModel.responseActivateCoupon.observe(viewLifecycleOwner, Observer {
            if (it.status!!) {
                showMsgDialogAndProceed("Coupon activated")
            } else {
                showMsgDialogAndProceed(it.message.toString())
            }
        })

        offerViewModel.responseFavoriteCoupon.observe(viewLifecycleOwner, {
            when (it.status!!) {
                0 -> showMsgDialogAndProceed(it.message!!.trim())
                1 -> trendingOfferListAdapter.setFavoriteMark(
                    isFavoritePosition, if (it.message!!.trim()
                            .lowercase() == "added"
                    ) 1 else 0
                )
            }
        })

        /*if(category == Constants.GYM){
            tvAmenities.visibility =  View.VISIBLE
            rvAmenities.visibility =  View.VISIBLE

        }else{
            tvAmenities.visibility =  View.GONE
            rvAmenities.visibility =  View.GONE
        }*/

        ivBack.setOnClickListener(this)
        ivShare.setOnClickListener(this)
        // rlCollectStamp.setOnClickListener(this)
        tvDescr.setOnClickListener(this)
        ivFavorite.setOnClickListener(this)
        llCoins.setOnClickListener(this)
        tvSeeAll.setOnClickListener(this)
        llSeeAll.setOnClickListener(this)
        llPhone.setOnClickListener(this)
        llEmail.setOnClickListener(this)
        //  tvOpenHour.setOnClickListener(this)
        btnLiveDeals.setOnClickListener(this)
        tvViewonMap.setOnClickListener(this)
        llReport.setOnClickListener(this)

        //Animate Live Deal Tag
        val anim = ObjectAnimator.ofInt(
            btnLiveDeals, "backgroundColor",
            ContextCompat.getColor(requireActivity(), R.color.myMountainMeadow),
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.myMountainMeadow
                ), 85
            )
        )
        anim.apply {
            duration = 500
            setEvaluator(ArgbEvaluator())
            repeatMode = ValueAnimator.REVERSE
            repeatCount = Animation.INFINITE
            start()
        }

        val animText = ObjectAnimator.ofInt(
            btnLiveDeals, "textColor",
            ContextCompat.getColor(requireActivity(), R.color.myWhite),
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(requireActivity(), R.color.myWhite),
                85
            )
        )
        animText.apply {
            duration = 500
            setEvaluator(ArgbEvaluator())
            repeatMode = ValueAnimator.REVERSE
            repeatCount = Animation.INFINITE
            start()
        }
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
                it.tvTitle.text = title
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

    private fun getLastLocation() {
        if (checkPermissions()) {
            val locationManager =
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (LocationManagerCompat.isLocationEnabled(locationManager)) {

                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                        || ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        /*ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ),
                            PERMISSION_CALLBACK_CONSTANT
                        )*/
                    }
                }
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        mCurrentLat = location.latitude
                        mCurrentLng = location.longitude
                    }
                }
            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            }
        } else {
            requestPermissions()
        }
    }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.getMainLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            mCurrentLat = mLastLocation.latitude
            mCurrentLng = mLastLocation.longitude
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ),
            101
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 101) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Granted. Start getting the location information
                getLastLocation()
            }
        } else if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allGranted = true
                } else {
                    allGranted = false
                    break
                }
            }
            if (allGranted) {
                tvViewonMap.performClick()
            } else {
                getLastLocation()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OverviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OverviewFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onDetach() {
        super.onDetach()
        if (runnableAutoScroll != null) {
            handlerAutoScroll.removeCallbacks(runnableAutoScroll!!)
            handlerAutoScroll.removeCallbacksAndMessages(null)
        }
    }

    override fun onClick(v: View?) {
        try {
            when (v) {
                ivBack -> {
                    handlerAutoScroll.removeCallbacks(runnableAutoScroll!!)
                    handlerAutoScroll.removeCallbacksAndMessages(null)
                    activity?.onBackPressed()
                }

                ivShare -> {
                    if (serviceDetail != null) {
                        val share = Intent(Intent.ACTION_SEND)
                        share.type = "text/plain"
                        share.putExtra(
                            Intent.EXTRA_TEXT,
                            "Find this service on The Market Theory | " + serviceDetail?.title + "\n" + serviceDetail?.address
                        )
                        startActivity(Intent.createChooser(share, "Share"))
                    }
                }

                btnLiveDeals -> {
                    startActivity(
                        Intent(activity, LiveDealsActivity::class.java)
                            .putExtra("serviceId", serviceId)
                    )
                }

                /*            rlCollectStamp -> {
                                startActivity(Intent(activity,CollectStampsActivity::class.java)
                                    .putExtra("serviceId",serviceId))
                            }*/

                llSeeAll -> {
                    if (PubFun.isInternetConnection(requireActivity())) {
                        startActivity(
                            Intent(activity, SeeAllPhotosActivity::class.java)
                                .putExtra("title", title)
                        )
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }


                }

                tvViewonMap -> {
                    if (PubFun.isInternetConnection(requireActivity())) {
                        getLastLocation()
                        if (mCurrentLat != 0.0 && mCurrentLng != 0.0) {
                            val mapIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    "http://maps.google.com/maps?saddr=${mCurrentLat},${mCurrentLng}" +
                                            "&daddr=${serviceDetail?.latitude},${serviceDetail?.longitude}"
                                )
                            )
                            activity?.startActivity(mapIntent)
                        }
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }


                    /*if (checkPermissions()) {
                        startActivity(Intent(activity, MapActivity::class.java))
    //                    .putExtra("serviceDetail",serviceDetail))
                        activity?.overridePendingTransition(0, 0)
                    } else {
                        requestPermissions()
                    }*/
                }

                llCoins -> {
                    //  if (tvPoints.text.toString() != "0") {
                    if (PubFun.isInternetConnection(requireActivity())) {
                        if (Config.isVendorDetailComingFromTotalPoints) {
                            Config.isVendorDetailComingFromTotalPoints = false
                            Config.isMyPointClickedFromHome = false
                        } else {
                            Config.isMyPointClickedFromHome = true
                        }
                     /*Config.isMyPointClickedFromHome = true*/
                                Config.isMyPointsActivityComingFromOverviewFragment = true
                        startActivity(
                            Intent(activity, MyPointsActivity::class.java)
                                .putExtra("serviceId", serviceId)
                        )
                        requireActivity().finish()
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                    // }
                }

                llReport -> {
                    if (PubFun.isInternetConnection(requireActivity())) {
                        startActivity(
                            Intent(activity, AddReportActivity::class.java)
                                .putExtra("serviceId", serviceId)
                        )
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }

                tvSeeAll -> {
                    /*Config.isRestaurantListOpeningFrom =
                        Config.restaurantListOpeningFromTrendingOfferSeeAll
                    startActivity(
                        Intent(
                            requireActivity(),
                            RestaurantListActivity::class.java
                        )
                    )*/
                    if (PubFun.isInternetConnection(requireActivity())) {
                        myRoomDatabase.daoConfig().apply {
                            deleteConfigTableByField(Config.dbOfferListServiceId)
                            insertConfigTable(
                                TableConfig(
                                    Config.dbOfferListServiceId,
                                    serviceId
                                )
                            )
                        }
                        Config.isCouponOpeningFromBucket = false
                        Config.isEventBottomBarClicked = true
                        Config.isMyCouponClickedFromHome = true
                        Config.isCouponComingFromOverviewViewAll = true
                        startActivity(Intent(requireActivity(), CouponActivity::class.java))
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }

                tvOpenHour -> {
                    //                startActivity(Intent(activity,RestaurantDescriptionActivity::class.java)
                    //                    .putExtra("serviceId",serviceId))
                }

                tvDescr -> {
                    startActivity(
                        Intent(activity, RestaurantDescriptionActivity::class.java)
                            .putExtra("serviceId", serviceId)
                    )
                }

                ivFavorite -> {
                    if (is_liked == 0)
                        ivFavorite.setImageResource(R.drawable.ic_like_selected)
                    else
                        ivFavorite.setImageResource(R.drawable.ic_like_unselected)

                    is_liked = is_liked!!.xor(1)
                }

                llPhone -> {
                    if (!phone.isNullOrEmpty()) {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intent);
                    }
                }

                llEmail -> {
                    try {
                        val intent = Intent(Intent.ACTION_SENDTO)
                        intent.data = Uri.parse("mailto:")
                        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                        intent.putExtra(Intent.EXTRA_SUBJECT, "The market Theory | Support")
                        intent.putExtra(Intent.EXTRA_TEXT, "")
                        startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        showMsgDialogAndProceed(
                            "There are no email clients installed."
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            val isNavigate = data.getBooleanExtra("isNavigate", false)
            if (isNavigate) {
                generalViewModel.navigateToBooking.value = true
            }
        }
    }
}