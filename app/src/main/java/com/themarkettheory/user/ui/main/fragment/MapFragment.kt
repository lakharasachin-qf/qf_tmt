package com.themarkettheory.user.ui.main.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fuzzproductions.ratingbar.RatingBar
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.makeramen.roundedimageview.RoundedImageView
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.newmodels.getcategories.Category
import com.themarkettheory.user.newmodels.home.NewHomeRes
import com.themarkettheory.user.newmodels.map.Restaurant
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.activity.NotificationActivity
import com.themarkettheory.user.ui.main.adapter.HomeCategoriesAdapter
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity
import com.themarkettheory.user.ui.restaurant.restaurant_list.RestaurantListActivity
import com.themarkettheory.user.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_map.*
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var mapFragment: SupportMapFragment
    lateinit var mMap: GoogleMap
    var callback: onMapClick? = null
    lateinit var homeViewModel: HomeViewModel

    private lateinit var homeCategoriesAdapter: HomeCategoriesAdapter
    private var responseHome = NewHomeRes()
    var selectedCategoryId = ""
    private var previousMarker: Marker? = null

    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mCurrentLat: Double = 0.0
    private var mCurrentLng: Double = 0.0

    private var isLoadingFirstTime = true

    //check if all permissions are granted
    var allGranted = false
    private val PERMISSION_CALLBACK_CONSTANT = 100
    private var onActivityRequestCode = 0
    private val REQUEST_PERMISSION_SETTING = 101

    private var locationSettingCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as onMapClick
        } catch (e: Exception) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_map, container, false)
        mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        mapFragment = view.findViewById(R.id.map)
        try {
            Utils.hideProgress(requireActivity())
            tvMapAddress.text = Config.myRoomDatabase.daoConfig()
                .selectConfigTableByField(Config.dbCurrentCitySelection)
            responseHome = Config.gson.fromJson(
                Config.myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbNewHomeRes),
                NewHomeRes::class.java
            )

            ivMapMenu.setOnClickListener {
                if (PubFun.isInternetConnection(requireActivity())) {
                    Config.isRestaurantListOpeningFrom = Config.restaurantListOpeningFromMapFragment
                    startActivity(
                        Intent(
                            requireActivity(),
                            RestaurantListActivity::class.java
                        )
                    )
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            val subCategories = ArrayList<Category>()
            for (i in responseHome.data!!.categories!![0].subcategory!!.indices) {
                val category = Category()
                category.id = responseHome.data!!.categories!![0].subcategory!![i].id
                category.name = responseHome.data!!.categories!![0].subcategory!![i].name

                /*if(category.name!! == Config.subCategory0IndexRestaurantOldName) {
                    category.name = Config.subCategory0IndexRestaurantNewName
                }*/
                subCategories.add(category)
            }

            homeCategoriesAdapter = HomeCategoriesAdapter(
                requireActivity(),
                subCategories
            ) {
                if (PubFun.isInternetConnection(requireActivity())) {
                    selectedCategoryId = it.id.toString()
                    Utils.showProgress(requireActivity())
                    val subCategoryName = if (it.name!!.trim().lowercase(Locale.getDefault()) ==
                        Config.subCategory0IndexRestaurantOldName.lowercase(Locale.getDefault())
                    )
                        "" else it.name!!.trim()
                    Log.d("map", "sub category = $subCategoryName")
                    homeViewModel.service_list("1", subCategoryName, "0", "", "", "", "")
                } else {
                    homeCategoriesAdapter.setPosition(Config.foodCategoryPreviousPosition)
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
            rvMapCategory.apply {
                adapter = homeCategoriesAdapter
                layoutManager =
                    LinearLayoutManager(
                        requireActivity(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
            }
            ivMapNotification.setOnClickListener {
                startActivity(Intent(requireActivity(), NotificationActivity::class.java))
            }

            getResponse()

            /* activity?.runOnUiThread {
                 mapFragment.getMapAsync(this)
             }*/

            mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Config.isVendorDetailOpeningFrom = ""
    }

    override fun onResume() {
        super.onResume()
        try {
            if (Config.fragmentName == Config.mapFragment) {
                previousMarker = null
                getLastLocation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
                for (i in grantResults.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        allGranted = true
                    } else {
                        allGranted = false
                        break
                    }
                }
                if (allGranted) {
                    getLastLocation()
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    openPermissionDialog()

                    /*val builder = AlertDialog.Builder(requireActivity())
                    builder.setTitle("Location permission denied")
                    builder.setMessage(
                        "We still need location permission to be allowed in order " +
                                "to run few app functionality smoothly otherwise app might " +
                                "not work properly"
                    )
                    builder.setPositiveButton(
                        "GRANT"
                    ) { dialogInterface, i ->
                        dialogInterface.cancel()
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            PERMISSION_CALLBACK_CONSTANT
                        )
                    }
                    builder.setNegativeButton("CANCEL") { dialogInterface, i ->
                        dialogInterface.cancel()
                    }
                    builder.show()*/
                } else {
                    getLastLocation()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val onMapFragmenOnActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        when (onActivityRequestCode) {
            REQUEST_PERMISSION_SETTING -> {
                if (ActivityCompat.checkSelfPermission(
                        Config.mainActivity!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        Config.mainActivity!!,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //Got Permission
                    allGranted = true
                    getLastLocation()
                } else {
                    allGranted = false
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun openPermissionDialog() {
        try {
            val dialogPermission = DialogToast(Config.mainActivity!!)
            dialogPermission.show()
            dialogPermission.holder?.let { dialog ->
                dialog.tvTitle.text = "Location permission denied"
                dialog.tvMessage.text =
                    "We still need location permission to be allowed in order " +
                            "to run few app functionality smoothly otherwise app might " +
                            "not work properly"
                dialog.btnDialogLogout.apply {
                    text = "GRANT"
                    setOnClickListener {
                        dialogPermission.dismiss()
                        allGranted = false
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts(
                            "package",
                            Config.mainActivity!!.packageName, null
                        )
                        intent.data = uri
                        onActivityRequestCode = REQUEST_PERMISSION_SETTING
                        onMapFragmenOnActivityResult.launch(intent)
                    }
                }
                dialog.btnDialogCancel.apply {
                    text = "SKIP"
                    setOnClickListener {
                        dialogPermission.dismiss()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun getLastLocation() {
        try {
            val locationManager =
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (LocationManagerCompat.isLocationEnabled(locationManager)) {

                activity?.runOnUiThread {
                    mapFragment.getMapAsync(this)
                }

                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //Show Information about why you need the permission
                    openPermissionDialog()


                    /*val builder = AlertDialog.Builder(requireActivity())
                    builder.setTitle("Location permission denied")
                    builder.setMessage(
                        "We still need location permission to be allowed in order " +
                                "to run few app functionality smoothly otherwise app might " +
                                "not work properly"
                    )
                    builder.setPositiveButton(
                        "GRANT"
                    ) { dialogInterface, i ->
                        dialogInterface.cancel()
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            PERMISSION_CALLBACK_CONSTANT
                        )
                    }
                    builder.setNegativeButton("CANCEL") { dialogInterface, i ->
                        dialogInterface.cancel()
                    }
                    builder.show()*/
                } else {
                    mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                        val location: Location? = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            mCurrentLat = location.latitude
                            mCurrentLng = location.longitude
                            activity?.runOnUiThread {
                                mapFragment.getMapAsync(this)
                            }
                        }
                    }
                }
            } else {
                if (locationSettingCount == 0) {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                    locationSettingCount++
                } else {
                    //Show Information about why you need the permission
                    val dialogPermission = DialogToast(Config.mainActivity!!)
                    dialogPermission.show()
                    dialogPermission.holder?.let { dialog ->
                        dialog.tvTitle.text = "Location permission denied"
                        dialog.tvMessage.text =
                            "We still need location permission to be allowed in order " +
                                    "to run few app functionality smoothly otherwise app might " +
                                    "not work properly"
                        dialog.btnDialogLogout.apply {
                            text = "GRANT"
                            setOnClickListener {
                                dialogPermission.dismiss()
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                startActivity(intent)
                            }
                        }
                        dialog.btnDialogCancel.apply {
                            text = "SKIP"
                            setOnClickListener {
                                dialogPermission.dismiss()
                            }
                        }
                    }

                    /*val builder = AlertDialog.Builder(requireActivity())
                    builder.setTitle("Location permission denied")
                    builder.setMessage(
                        "We still need location permission to be allowed in order " +
                                "to run few app functionality smoothly otherwise app might " +
                                "not work properly"
                    )
                    builder.setPositiveButton(
                        "GRANT"
                    ) { dialogInterface, i ->
                        dialogInterface.cancel()
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }
                    builder.setNegativeButton("CANCEL") { dialogInterface, i ->
                        dialogInterface.cancel()
                    }
                    builder.show()*/
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestNewLocationData() {
        try {
            val mLocationRequest = LocationRequest.create()
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationRequest.interval = 0
            mLocationRequest.fastestInterval = 0
            mLocationRequest.numUpdates = 1

            mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
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
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    //Show Information about why you need the permission
                    openPermissionDialog()

                    /*val builder = AlertDialog.Builder(requireActivity())
                    builder.setTitle("Location permission denied")
                    builder.setMessage(
                        "We still need location permission to be allowed in order " +
                                "to run few app functionality smoothly otherwise app might " +
                                "not work properly"
                    )
                    builder.setPositiveButton(
                        "GRANT"
                    ) { dialogInterface, i ->
                        dialogInterface.cancel()
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            PERMISSION_CALLBACK_CONSTANT
                        )
                    }
                    builder.setNegativeButton("CANCEL") { dialogInterface, i ->
                        dialogInterface.cancel()
                    }
                    builder.show()*/
                } else {
                    mFusedLocationClient.requestLocationUpdates(
                        mLocationRequest, mLocationCallback,
                        Looper.getMainLooper()
                    )
                }
            } else {
                mFusedLocationClient.requestLocationUpdates(
                    mLocationRequest, mLocationCallback,
                    Looper.getMainLooper()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            try {
                var mLastLocation: Location = locationResult.lastLocation
                mCurrentLat = mLastLocation.latitude
                mCurrentLng = mLastLocation.longitude
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getResponse() {
        try {
            homeViewModel.responseServices.observe(viewLifecycleOwner, Observer { it ->
                Utils.hideProgress(requireActivity())
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> {
                        Log.d("map", "data size = ${it.data?.restaurant!!.size}")
                        mMap.clear()
                        if (it.data!!.restaurant!!.isNotEmpty()) {
                            setData(it.data!!.restaurant!!)
                        }
                        Utils.hideProgress(requireActivity())
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun animateToCurrentPosition() {
        try {
            val position = CameraPosition.Builder()
                .target(LatLng(mCurrentLat, mCurrentLng))
                .zoom(15.0f)
                .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setData(data: List<Restaurant>) {
        try {
            val point = LatLng(44.920474, -92.057851)
//        mMap.addMarker(MarkerOptions().position(point).title(""))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(point))
            val latLngs = ArrayList<LatLng>()
            /*data.map {
                    latLngs.add(LatLng(it.latitude!!.toDouble(), it.longitude!!.toDouble()))
                }*/

            for (i in data.indices) {
                val dataLat = PubFun.isNull(data[i].latitude, "0.0")
                val dataLng = PubFun.isNull(data[i].longitude, "0.0")
                latLngs.add(LatLng(dataLat.toDouble(), dataLng.toDouble()))
            }

            /*val latLngs = arrayOf<LatLng>(
                    LatLng(44.968046, -94.420307),
                    LatLng(44.33328, -89.132008),
                    LatLng(44.92057, -93.44786),
                    LatLng(44.968041, -94.419696)
                )*/
            val bounds = LatLngBounds.builder()

//            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location))
//            mMap.addMarker(markerOptions)
            data.mapIndexed { index, latLng ->

                mMap.setOnInfoWindowClickListener {
                    for (i in 0..data.size) {
                        val dataLat = PubFun.isNull(data[i].latitude, "0.0")
                        val dataLng = PubFun.isNull(data[i].longitude, "0.0")

                        if (dataLat.toDouble() == it.position.latitude &&
                            dataLng.toDouble() == it.position.longitude &&
                            it.title == data[i].id.toString()
                        ) {
                            Config.isMenuFragmentComingFrom = ""
                            Config.isVendorDetailOpeningFrom =
                                Config.isVendorDetailComingFromMapFragment
                            Config.isMyCouponClickedFromHome = false
                            Config.isEventBottomBarClicked = false
                            Config.isVendorComingFromMapFragment = true
                            startActivity(
                                Intent(requireActivity(), VendorDetailActivity::class.java)
                                    .putExtra("serviceId", data[i].id.toString())
                                    .putExtra("category", data[i].categoryId.toString())
                                    .putExtra("vendorTitle", data[i].categoryName)
                            )
                            break
                        }
                    }
                }
                // Setting the position for the marker

                mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                    override fun getInfoContents(p0: Marker): View? {
                        return null
                    }

                    @SuppressLint("SetTextI18n")
                    override fun getInfoWindow(p0: Marker?): View {
                        val v: View = View.inflate(activity, R.layout.infowindow, null)
                        try {
                            if (previousMarker != null) {
                                previousMarker!!.setIcon(
                                    bitmapDescriptorFromVector(
                                        requireActivity(),
                                        R.drawable.ic_location_svg
                                    )
                                )
                            }
                            previousMarker = p0!!

                            p0.setIcon(
                                bitmapDescriptorFromVector(
                                    requireActivity(),
                                    R.drawable.ic_location_selected_svg
                                )
                            )
                            //                        v = View.inflate(activity, R.layout.infowindow, null)
                            val iv = v.findViewById<RoundedImageView>(R.id.iv)
                            val tvVendorName = v.findViewById<TextView>(R.id.tvVendorName)
                            val tvDistance = v.findViewById<TextView>(R.id.tvDistance)
                            val tvRating = v.findViewById<TextView>(R.id.tvRating)
                            val rating = v.findViewById<RatingBar>(R.id.rating)

                            data.mapIndexed { index, serviceList ->
                                var mapLat =
                                    if (serviceList.latitude.isNullOrEmpty()) "0" else serviceList.latitude!!
                                var mapLng =
                                    if (serviceList.longitude.isNullOrEmpty()) "0" else serviceList.longitude!!

                                if (mapLat.toDouble() == p0.position.latitude &&
                                    mapLng.toDouble() == p0.position.longitude && serviceList.id.toString() == p0.title
                                ) {
                                    /*Glide.with(requireActivity()).load(serviceList.image).into(iv)*/
                                    if (serviceList.image.isNullOrEmpty()) {
                                        iv.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireActivity(),
                                                R.drawable.ic_camera
                                            )
                                        )
                                    } else {
                                        if (serviceList != null && !serviceList.image.isNullOrEmpty()) {
                                            /* Picasso.get().load(serviceList.image)
                                                 .error(R.drawable.ic_camera)
                                                 .into(iv)*/
                                            Glide.with(Config.mainActivity!!)
                                                .load(serviceList.image)
                                                .error(R.drawable.ic_camera)
                                                .into(iv)
                                        }
                                    }
                                    tvVendorName.text = serviceList.title
                                    tvDistance.text = serviceList.distance
                                    tvRating.text = "(${serviceList.rating})"
                                    rating.rating = serviceList.rating!!.toFloat()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return v
                    }

                })
                val markerOptions = MarkerOptions()

                val latLng1 = LatLng(
                    PubFun.isNull(latLng.latitude, "0.0").toDouble(),
                    PubFun.isNull(latLng.longitude, "0.0").toDouble()
                )
                markerOptions.position(latLng1)
                bounds.include(latLng1)

                /*if(latLng.category_id == 1){
                    markerOptions.icon(bitmapDescriptorFromVector(requireActivity(), R.drawable.ic_map_restaurant))
                }else if(latLng.category_id == 2){
                    markerOptions.icon(bitmapDescriptorFromVector(requireActivity(), R.drawable.ic_map_gym))
                }else if(latLng.category_id == 3){
                    markerOptions.icon(bitmapDescriptorFromVector(requireActivity(), R.drawable.ic_map_retail))
                }else if(latLng.category_id == 4){
                    markerOptions.icon(bitmapDescriptorFromVector(requireActivity(), R.drawable.ic_map_salon))
                }else if(latLng.category_id == 5){
                    markerOptions.icon(bitmapDescriptorFromVector(requireActivity(), R.drawable.ic_map_spa))
                }else if(latLng.category_id == 6){
                    markerOptions.icon(bitmapDescriptorFromVector(requireActivity(), R.drawable.ic_map_bakery))
                }*/
                markerOptions.icon(
                    bitmapDescriptorFromVector(
                        requireActivity(),
                        R.drawable.ic_location_svg
                    )
                )
                markerOptions.title(latLng.id.toString())
                mMap.addMarker(markerOptions)
            }
            bounds.include(LatLng(mCurrentLat, mCurrentLng))
            /*mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))*/
            if (isLoadingFirstTime) {
                isLoadingFirstTime = false
                animateToCurrentPosition()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) )
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom - 0.5f));
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }

    override fun onMapReady(p0: GoogleMap?) {
        try {
            mMap = p0!!

            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            try {
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        activity, R.raw.style_json
                    )
                );
                mMap.uiSettings.isScrollGesturesEnabled = true
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
                mMap.isMyLocationEnabled = true

            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (PubFun.isInternetConnection(requireActivity())) {
                homeViewModel.service_list("1", selectedCategoryId, "0", "", "", "", "")
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface onMapClick {
        fun allowTouchIntercept(isAllowed: Boolean)
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        try {
            val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
            vectorDrawable!!.setBounds(
                0,
                0,
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight
            )
            val bitmap = Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(Config.mainActivity!!)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Map"
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